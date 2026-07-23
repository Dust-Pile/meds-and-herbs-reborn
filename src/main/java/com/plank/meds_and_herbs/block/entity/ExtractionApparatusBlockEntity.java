package com.plank.meds_and_herbs.block.entity;

import com.plank.meds_and_herbs.client.gui.menu.ExtractApparatusGUIMenu;
import com.plank.meds_and_herbs.init.*;
import com.plank.meds_and_herbs.procedures.LoadItemList;
import com.plank.meds_and_herbs.recipe.ExtractionRecipe;
import com.plank.meds_and_herbs.recipe.ExtractionRecipeInput;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static com.plank.meds_and_herbs.recipe.ExtractionRecipe.MAX_PROGRESS;

public class ExtractionApparatusBlockEntity extends BlockEntity implements MenuProvider {

    // 内部物品处理器
    private final ItemStackHandler internalHandler = new ItemStackHandler(5) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.is(Tags.Items.POWDERS);
                case 1 -> stack.is(Tags.Items.MEDICINE);
                case 2 -> stack.is(Tags.Items.EMPTY_BOTTLE);
                case 3 -> true;
                case 4 -> stack.is(Tags.Items.FILTER);
                default -> false;
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }

            // 🔥 槽位变化时，清理无效的待提取记录
            if (pendingSlots.containsKey(slot)) {
                ItemStack recorded = pendingSlots.get(slot);
                ItemStack current = getStackInSlot(slot);
                // 如果当前物品与记录不匹配（或为空），移除记录
                if (!ItemStack.isSameItemSameComponents(recorded, current)) {
                    pendingSlots.remove(slot);
                }
            }
        }
    };

    // 待提取记录：槽位 -> 物品
    private final Map<Integer, ItemStack> pendingSlots = new HashMap<>();

    // ---------- 方向过滤器 ----------
    private final IItemHandler upHandler = new FilteredItemHandler(
            internalHandler,
            slot -> slot == 0 || slot == 1,
            slot -> false
    );

    private final IItemHandler sideHandler = new FilteredItemHandler(
            internalHandler,
            slot -> slot == 2 || slot == 4,
            slot -> false
    );

    private final IItemHandler downHandler = new FilteredItemHandler(
            internalHandler,
            slot -> false,
            slot -> true // 允许提取所有槽位，但受 pendingSlots 限制
    ) {
        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            // 检查该槽位是否有待提取记录
            ItemStack recorded = pendingSlots.get(slot);
            if (recorded == null || recorded.isEmpty()) {
                return ItemStack.EMPTY;
            }

            ItemStack current = getStackInSlot(slot);
            // 验证当前物品是否与记录匹配
            if (!ItemStack.isSameItemSameComponents(recorded, current)) {
                // 不匹配，移除无效记录
                pendingSlots.remove(slot);
                return ItemStack.EMPTY;
            }

            // 执行提取
            ItemStack result = super.extractItem(slot, amount, simulate);
            if (!simulate && !result.isEmpty()) {
                // 提取成功后移除记录
                pendingSlots.remove(slot);
            }
            return result;
        }
    };

    private int progress = 0;
    private int soundTimer = 0;
    private static final int SOUND_INTERVAL = 40;

    public ExtractionApparatusBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.EXTRACTION_APPARATUS.get(), pos, state);
    }

    public IItemHandler getHandlerForSide(@Nullable Direction side) {
        if (side == null) return internalHandler;
        return switch (side) {
            case UP -> upHandler;
            case DOWN -> downHandler;
            default -> sideHandler;
        };
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        ExtractionRecipeInput input = new ExtractionRecipeInput(
                internalHandler.getStackInSlot(0),
                internalHandler.getStackInSlot(1),
                internalHandler.getStackInSlot(2),
                internalHandler.getStackInSlot(4)
        );

        ExtractionRecipe recipe = getMatchingRecipe(input);
        if (recipe == null) {
            if (progress != 0) change();
            return;
        }

        if (!canInsertOutputs(recipe)) {
            if (progress != 0) change();
            return;
        }

        if (progress < MAX_PROGRESS) {
            progress++;
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            if (--soundTimer <= 0) {
                soundTimer = SOUND_INTERVAL;
                float pitch = 0.8f + level.random.nextFloat() * 0.4f;
                level.playSound(null, worldPosition, Sounds.EXTRACT_APPARATUS.get(),
                        SoundSource.BLOCKS, 0.5f, pitch);
            }
        }

        if (progress >= MAX_PROGRESS) {
            // 配方完成，清空之前的记录（确保不会残留）
            pendingSlots.clear();

            // 保存输入副本（用于返还物品）
            ItemStack powderBefore = internalHandler.getStackInSlot(0).copy();
            ItemStack solventBefore = internalHandler.getStackInSlot(1).copy();
            ItemStack bottleBefore = internalHandler.getStackInSlot(2).copy();

            // 消耗物品
            internalHandler.extractItem(0, recipe.powder().count(), false);
            internalHandler.extractItem(1, recipe.solvent().count(), false);
            if (recipe.emptyBottle().isPresent()) {
                internalHandler.extractItem(2, recipe.emptyBottle().get().count(), false);
            }
            // 过滤棉耐久消耗
            internalHandler.getStackInSlot(4).hurtAndBreak(1, (ServerLevel) level, null, item -> {});

            // 弹出槽2中多余的空瓶
            ItemStack leftover = internalHandler.getStackInSlot(2);
            if (!leftover.isEmpty()) {
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), leftover);
                internalHandler.setStackInSlot(2, ItemStack.EMPTY);
            }

            // 返还物品并记录
            processRemainderAndRecord(0, powderBefore);
            processRemainderAndRecord(1, solventBefore);
            processRemainderAndRecord(2, bottleBefore);

            // 放入主产物（槽2）并记录
            ItemStack output = recipe.output().copy();
            if (!output.isEmpty()) {
                if (!internalHandler.getStackInSlot(2).isEmpty()) {
                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                            internalHandler.getStackInSlot(2));
                }
                internalHandler.setStackInSlot(2, output);
                pendingSlots.put(2, output.copy());
            }

            // 放入副产物（槽3）并记录
            ItemStack stillage = recipe.stillage().copy();
            if (!stillage.isEmpty()) {
                if (!internalHandler.getStackInSlot(3).isEmpty()) {
                    ItemStack existing = internalHandler.getStackInSlot(3);
                    if (ItemStack.isSameItemSameComponents(existing, stillage) &&
                            existing.getCount() + stillage.getCount() <= existing.getMaxStackSize()) {
                        existing.grow(stillage.getCount());
                        internalHandler.setStackInSlot(3, existing);
                    } else {
                        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), existing);
                        internalHandler.setStackInSlot(3, stillage);
                    }
                } else {
                    internalHandler.setStackInSlot(3, stillage);
                }
                pendingSlots.put(3, stillage.copy());
            }

            // 重置进度（但不清除 pendingSlots）
            progress = 0;
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    /**
     * 处理返还物品，并记录到 pendingSlots（如果放入某个槽位）
     */
    private void processRemainderAndRecord(int slot, ItemStack stack) {
        ItemStack remainder = stack.getCraftingRemainingItem();
        if (remainder.isEmpty()) return;

        // 尝试放入原槽
        ItemStack existing = internalHandler.getStackInSlot(slot);
        if (existing.isEmpty()) {
            internalHandler.setStackInSlot(slot, remainder);
            pendingSlots.put(slot, remainder.copy());
            return;
        }
        if (ItemStack.isSameItemSameComponents(existing, remainder) &&
                existing.getCount() + remainder.getCount() <= existing.getMaxStackSize()) {
            existing.grow(remainder.getCount());
            internalHandler.setStackInSlot(slot, existing);
            pendingSlots.put(slot, existing.copy());
            return;
        }

        // 尝试放入槽2
        ItemStack existing2 = internalHandler.getStackInSlot(2);
        if (existing2.isEmpty()) {
            internalHandler.setStackInSlot(2, remainder);
            pendingSlots.put(2, remainder.copy());
            return;
        }
        if (ItemStack.isSameItemSameComponents(existing2, remainder) &&
                existing2.getCount() + remainder.getCount() <= existing2.getMaxStackSize()) {
            existing2.grow(remainder.getCount());
            internalHandler.setStackInSlot(2, existing2);
            pendingSlots.put(2, existing2.copy());
            return;
        }

        // 尝试放入槽3
        ItemStack existing3 = internalHandler.getStackInSlot(3);
        if (existing3.isEmpty()) {
            internalHandler.setStackInSlot(3, remainder);
            pendingSlots.put(3, remainder.copy());
            return;
        }
        if (ItemStack.isSameItemSameComponents(existing3, remainder) &&
                existing3.getCount() + remainder.getCount() <= existing3.getMaxStackSize()) {
            existing3.grow(remainder.getCount());
            internalHandler.setStackInSlot(3, existing3);
            pendingSlots.put(3, existing3.copy());
            return;
        }

        // 所有槽都无法容纳，掉落
        if (level != null) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), remainder);
        }
    }

    private void change() {
        progress = 0;
        // 注意：不清除 pendingSlots，因为可能还有待提取的物品
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public ItemStackHandler getItemHandler() {
        return internalHandler;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return MAX_PROGRESS;
    }

    private ExtractionRecipe getMatchingRecipe(ExtractionRecipeInput input) {
        if (level == null) return null;
        return level.getRecipeManager()
                .getRecipeFor(Recipes.EXTRACTION_TYPE.get(), input, level)
                .map(RecipeHolder::value)
                .orElse(null);
    }

    private boolean canInsertOutputs(ExtractionRecipe recipe) {
        ItemStack main = internalHandler.getStackInSlot(2);
        ItemStack sub = internalHandler.getStackInSlot(3);

        boolean mainOk = main.isEmpty() ||
                (recipe.emptyBottle().isPresent() &&
                        ItemStack.isSameItemSameComponents(main, recipe.emptyBottle().get().ingredient().getItems()[0]));

        if (!mainOk) return false;

        if (!recipe.stillage().isEmpty()) {
            if (!sub.isEmpty() && !ItemStack.isSameItemSameComponents(sub, recipe.stillage())) return false;
            return sub.isEmpty() || sub.getCount() + recipe.stillage().getCount() <= sub.getMaxStackSize();
        }
        return true;
    }

    public boolean isCooking() {
        if (level == null) return false;
        ExtractionRecipeInput input = new ExtractionRecipeInput(
                internalHandler.getStackInSlot(0),
                internalHandler.getStackInSlot(1),
                internalHandler.getStackInSlot(2),
                internalHandler.getStackInSlot(4)
        );
        ExtractionRecipe recipe = getMatchingRecipe(input);
        return recipe != null && canInsertOutputs(recipe);
    }

    // ---------- MenuProvider ----------
    @Override
    @Nonnull
    public Component getDisplayName() {
        return Component.translatable("container.meds_and_herbs.extraction_apparatus");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @Nonnull Inventory inv, @Nonnull Player player) {
        return new ExtractApparatusGUIMenu(id, inv, this);
    }

    // ---------- 数据同步 ----------
    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    @Nonnull
    public CompoundTag getUpdateTag(@Nonnull HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    // ---------- NBT ----------
    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", internalHandler.serializeNBT(registries));
        tag.putInt("progress", progress);

        // 保存 pendingSlots
        ListTag pendingTag = new ListTag();
        for (Map.Entry<Integer, ItemStack> entry : pendingSlots.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt("slot", entry.getKey());
            entryTag.put("stack", entry.getValue().save(registries));
            pendingTag.add(entryTag);
        }
        tag.put("pendingSlots", pendingTag);
    }

    @Override
    protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("progress");
        LoadItemList.loadItemsFromTag(internalHandler, tag, registries);

        // 恢复 pendingSlots
        pendingSlots.clear();
        if (tag.contains("pendingSlots", Tag.TAG_LIST)) {
            ListTag pendingTag = tag.getList("pendingSlots", Tag.TAG_COMPOUND);
            for (int i = 0; i < pendingTag.size(); i++) {
                CompoundTag entryTag = pendingTag.getCompound(i);
                int slot = entryTag.getInt("slot");
                ItemStack stack = ItemStack.parse(registries, entryTag.getCompound("stack")).orElse(ItemStack.EMPTY);
                if (!stack.isEmpty()) {
                    pendingSlots.put(slot, stack);
                }
            }
        }
    }

    // ---------- 内部过滤器 ----------
    private static class FilteredItemHandler implements IItemHandler {
        private final IItemHandler delegate;
        private final Predicate<Integer> insertAllowed;
        private final Predicate<Integer> extractAllowed;

        public FilteredItemHandler(IItemHandler delegate, Predicate<Integer> insertAllowed, Predicate<Integer> extractAllowed) {
            this.delegate = delegate;
            this.insertAllowed = insertAllowed;
            this.extractAllowed = extractAllowed;
        }

        @Override
        public int getSlots() { return delegate.getSlots(); }

        @Override
        @Nonnull
        public ItemStack getStackInSlot(int slot) { return delegate.getStackInSlot(slot); }

        @Override
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!insertAllowed.test(slot)) return stack;
            return delegate.insertItem(slot, stack, simulate);
        }

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!extractAllowed.test(slot)) return ItemStack.EMPTY;
            return delegate.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) { return delegate.getSlotLimit(slot); }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (!insertAllowed.test(slot)) return false;
            return delegate.isItemValid(slot, stack);
        }
    }
}