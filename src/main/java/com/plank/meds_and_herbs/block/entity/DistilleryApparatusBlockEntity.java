package com.plank.meds_and_herbs.block.entity;

import com.plank.meds_and_herbs.client.gui.menu.DistilleryApparatusGUIMenu;
import com.plank.meds_and_herbs.init.BlockEntities;
import com.plank.meds_and_herbs.init.Recipes;
import com.plank.meds_and_herbs.init.Sounds;
import com.plank.meds_and_herbs.init.Tags;
import com.plank.meds_and_herbs.procedures.LoadItemList;
import com.plank.meds_and_herbs.recipe.DistillingRecipe;
import com.plank.meds_and_herbs.recipe.DistillingRecipeInput;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static com.plank.meds_and_herbs.recipe.DistillingRecipe.MAX_PROGRESS;

public class DistilleryApparatusBlockEntity extends BlockEntity implements MenuProvider {

    // 待提取记录：槽位 -> 物品
    private final Map<Integer, ItemStack> pendingSlots = new HashMap<>();

    // 内部物品处理器
    private final ItemStackHandler internalHandler = new ItemStackHandler(4) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return switch (slot) {
                case 0, 1 -> true; // 输入槽
                case 2 -> stack.is(Tags.Items.EMPTY_BOTTLE); // 空瓶槽
                default -> false; // 副产物槽（槽3）不可手动放入
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }

            // 槽位变化时，清理无效的待提取记录
            if (pendingSlots.containsKey(slot)) {
                ItemStack recorded = pendingSlots.get(slot);
                ItemStack current = getStackInSlot(slot);
                if (!ItemStack.isSameItemSameComponents(recorded, current)) {
                    pendingSlots.remove(slot);
                }
            }
        }

        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            // 只允许提取有记录且匹配的物品
            ItemStack recorded = pendingSlots.get(slot);
            if (recorded == null || recorded.isEmpty()) {
                return ItemStack.EMPTY;
            }

            ItemStack current = getStackInSlot(slot);
            if (!ItemStack.isSameItemSameComponents(recorded, current)) {
                pendingSlots.remove(slot);
                return ItemStack.EMPTY;
            }

            ItemStack result = super.extractItem(slot, amount, simulate);
            if (!simulate && !result.isEmpty()) {
                pendingSlots.remove(slot);
            }
            return result;
        }
    };

    // 方向过滤器
    private final IItemHandler upHandler = new FilteredItemHandler(
            internalHandler,
            slot -> slot == 0 || slot == 1, // 允许插入槽0和1
            slot -> false // 禁止提取
    );

    private final IItemHandler sideHandler = new FilteredItemHandler(
            internalHandler,
            slot -> slot == 2, // 允许插入槽2（空瓶）
            slot -> false
    );

    private final IItemHandler downHandler = new FilteredItemHandler(
            internalHandler,
            slot -> false, // 禁止插入
            slot -> slot == 0 || slot == 1 || slot == 2 || slot == 3 // 允许提取所有槽，但受 pendingSlots 限制
    ) {
        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            // 直接调用内部处理器的提取逻辑（它已经检查 pendingSlots）
            return internalHandler.extractItem(slot, amount, simulate);
        }
    };

    private int progress = 0;
    private int soundTimer = 0;
    private static final int SOUND_INTERVAL = 40;

    public DistilleryApparatusBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.DISTILLERY_APPARATUS.get(), pos, state);
    }

    // 供能力注册使用
    public IItemHandler getHandlerForSide(@Nullable Direction side) {
        if (side == null) return internalHandler;
        return switch (side) {
            case UP -> upHandler;
            case DOWN -> downHandler;
            default -> sideHandler; // 水平方向
        };
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        DistillingRecipeInput input = new DistillingRecipeInput(
                internalHandler.getStackInSlot(0),
                internalHandler.getStackInSlot(1)
        );
        Optional<DistillingRecipe> recipeOpt = level.getRecipeManager()
                .getRecipeFor(Recipes.DISTILLING_TYPE.get(), input, level)
                .map(RecipeHolder::value);

        boolean shouldBeWorking = recipeOpt.isPresent() && canStartProcess(recipeOpt.get());
        if (shouldBeWorking) {
            if (progress < MAX_PROGRESS) {
                progress++;
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                if (--soundTimer <= 0) {
                    soundTimer = SOUND_INTERVAL;
                    float pitch = 0.8f + level.random.nextFloat() * 0.4f;
                    level.playSound(null, worldPosition, Sounds.DISTILLERY_APPARATUS.get(),
                            SoundSource.BLOCKS, 0.5f, pitch);
                }
            }
            if (progress >= MAX_PROGRESS) {
                craftRecipe(recipeOpt.get());
                progress = 0;
                setChanged();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                level.playSound(null, worldPosition, Sounds.DISTILLERY_APPARATUS.get(),
                        SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        } else {
            if (progress != 0) change();
        }
    }

    private boolean canStartProcess(DistillingRecipe recipe) {
        ItemStack bottle = internalHandler.getStackInSlot(2);
        if (!recipe.matchesEmptyBottle(bottle)) return false;
        if (!recipe.stillage().isEmpty() && !canFit(recipe.stillage())) return false;
        if (recipe.emptyBottle().isPresent()) {
            return recipe.matchesEmptyBottle(bottle);
        } else {
            return bottle.isEmpty();
        }
    }

    private void craftRecipe(DistillingRecipe recipe) {
        // 清空旧记录
        pendingSlots.clear();

        // 保存输入副本（返还物品用）
        ItemStack inputACopy = internalHandler.getStackInSlot(0).copy();
        ItemStack inputBCopy = internalHandler.getStackInSlot(1).copy();

        // 消耗输入
        if (recipe.inputA().isPresent()) {
            internalHandler.extractItem(0, recipe.inputA().get().count(), false);
        }
        if (recipe.inputB().isPresent()) {
            internalHandler.extractItem(1, recipe.inputB().get().count(), false);
        }

        // 消耗空瓶
        if (recipe.emptyBottle().isPresent()) {
            int needed = recipe.emptyBottle().get().count();
            internalHandler.extractItem(2, needed, false);
            ItemStack leftover = internalHandler.getStackInSlot(2);
            if (!leftover.isEmpty()) {
                dropItem(leftover);
                internalHandler.setStackInSlot(2, ItemStack.EMPTY);
            }
        }

        // 处理返还物品并记录
        processRemainderAndRecord(0, inputACopy);
        processRemainderAndRecord(1, inputBCopy);

        // 放入主产物（槽2）并记录
        insertItemSafeAndRecord(2, recipe.output());

        // 放入副产物（槽3）并记录
        insertItemSafeAndRecord(3, recipe.stillage());

        // 配方完成音效已在外部播放
    }

    private void processRemainderAndRecord(int slot, ItemStack stack) {
        ItemStack remainder = stack.getCraftingRemainingItem();
        if (remainder.isEmpty()) return;

        // 尝试放回原槽
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

    private void insertItemSafeAndRecord(int slot, ItemStack stack) {
        if (stack.isEmpty()) return;
        ItemStack existing = internalHandler.getStackInSlot(slot);
        if (existing.isEmpty()) {
            internalHandler.setStackInSlot(slot, stack);
            pendingSlots.put(slot, stack.copy());
        } else if (ItemStack.isSameItemSameComponents(existing, stack) &&
                existing.getCount() + stack.getCount() <= existing.getMaxStackSize()) {
            existing.grow(stack.getCount());
            internalHandler.setStackInSlot(slot, existing);
            pendingSlots.put(slot, existing.copy());
        } else {
            dropItem(stack);
        }
    }

    private boolean canFit(ItemStack stack) {
        if (stack.isEmpty()) return true;
        ItemStack existing = internalHandler.getStackInSlot(3);
        if (existing.isEmpty()) return true;
        return ItemStack.isSameItemSameComponents(existing, stack) &&
                existing.getCount() + stack.getCount() <= existing.getMaxStackSize();
    }

    private void dropItem(ItemStack stack) {
        if (level != null && !stack.isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
        }
    }

    private void change() {
        progress = 0;
        // 不清除 pendingSlots，保留待提取物品
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public boolean isCooking() {
        if (level == null) return false;
        DistillingRecipeInput input = new DistillingRecipeInput(
                internalHandler.getStackInSlot(0),
                internalHandler.getStackInSlot(1)
        );
        Optional<DistillingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(Recipes.DISTILLING_TYPE.get(), input, level)
                .map(RecipeHolder::value);
        return recipe.isPresent() && canStartProcess(recipe.get());
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

    // ---------- MenuProvider ----------
    @Override
    @Nonnull
    public Component getDisplayName() {
        return Component.translatable("container.meds_and_herbs.distillery_apparatus");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @Nonnull Inventory inv, @Nonnull Player player) {
        return new DistilleryApparatusGUIMenu(id, inv, this);
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