package com.plank.meds_and_herbs.block.entity;

import com.plank.meds_and_herbs.init.BlockEntities;
import com.plank.meds_and_herbs.init.Recipes;
import com.plank.meds_and_herbs.init.Sounds;
import com.plank.meds_and_herbs.procedures.LoadItemList;
import com.plank.meds_and_herbs.recipe.FermentationRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class FermentationBarrelBlockEntity extends BlockEntity {
    private static final int MAX_PROGRESS = FermentationRecipe.DEFAULT_COOKING_TIME;
    private boolean isProcessingOutput = false;

    // 待提取记录：槽位 -> 物品
    private final Map<Integer, ItemStack> pendingSlots = new HashMap<>();

    private final ItemStackHandler itemHandler = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                // 只有不在输出处理过程中，才重置配方和进度
                if (!isProcessingOutput) {
                    currentRecipe = Optional.empty();
                    progress = 0;
                }
            }

            // 清理无效的待提取记录
            if (pendingSlots.containsKey(slot)) {
                ItemStack recorded = pendingSlots.get(slot);
                ItemStack current = getStackInSlot(slot);
                if (!ItemStack.isSameItemSameComponents(recorded, current)) {
                    pendingSlots.remove(slot);
                }
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return true;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        // 提取控制：只允许提取有记录且匹配的物品
        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
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

    private Optional<FermentationRecipe> currentRecipe = Optional.empty();
    private int progress = 0;

    public FermentationBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.FERMENTATION_BARREL.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        // 如果没有当前配方，尝试匹配
        if (currentRecipe.isEmpty()) {
            RecipeInput input = new RecipeInput() {
                @Override
                public @NotNull ItemStack getItem(int slot) {
                    return itemHandler.getStackInSlot(slot);
                }
                @Override
                public int size() {
                    return itemHandler.getSlots();
                }
            };

            Optional<RecipeHolder<FermentationRecipe>> holder = level.getRecipeManager()
                    .getRecipeFor(Recipes.FERMENTATION_TYPE.get(), input, level);
            if (holder.isPresent()) {
                currentRecipe = Optional.of(holder.get().value());
                // ✅ 保留现有进度（不重置）
            } else {
                // 无配方，重置进度
                if (progress != 0) change();
                return;
            }
        }

        FermentationRecipe recipe = currentRecipe.get();

        // 验证配方是否仍然匹配（防止物品被替换）
        RecipeInput input = new RecipeInput() {
            @Override
            public @NotNull ItemStack getItem(int slot) {
                return itemHandler.getStackInSlot(slot);
            }
            @Override
            public int size() {
                return itemHandler.getSlots();
            }
        };
        if (!recipe.matches(input, level)) {
            currentRecipe = Optional.empty();
            change();
            return;
        }

        // 增加进度
        if (progress < MAX_PROGRESS) {
            progress++;
            setChanged();
            ((ServerLevel) level).sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    5, 0.5, 0.5, 0.5, 0.0);
            level.sendBlockUpdated(pos, state, state, 3);
        }

        // 配方完成
        if (progress >= MAX_PROGRESS) {
            // 再次验证
            if (!recipe.matches(input, level)) {
                currentRecipe = Optional.empty();
                change();
                return;
            }

            // 复制配方所需物品的 Ingredient 列表（用于消耗）
            List<Ingredient> remainingIngredients = new ArrayList<>(recipe.getIngredients());

            // 遍历所有槽位，无序消耗匹配的物品
            isProcessingOutput = true;
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                ItemStack stack = itemHandler.getStackInSlot(i);
                if (stack.isEmpty()) continue;

                // 查找匹配的剩余 ingredient
                Iterator<Ingredient> it = remainingIngredients.iterator();
                while (it.hasNext()) {
                    Ingredient ing = it.next();
                    if (ing.test(stack)) {
                        // 匹配成功，清空该槽位
                        itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                        it.remove(); // 移除该 ingredient，防止重复消耗
                        break;
                    }
                }
            }

            // 剩余的 ingredient 理论上应为空，如果还有剩余则说明配方不完整，回退
            if (!remainingIngredients.isEmpty()) {
                isProcessingOutput = false;
                change();
                currentRecipe = Optional.empty();
                return;
            }

            // 寻找空槽
            int outputSlot = -1;
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                if (itemHandler.getStackInSlot(i).isEmpty()) {
                    outputSlot = i;
                    break;
                }
            }
            if (outputSlot == -1) {
                // 极端情况，回退
                isProcessingOutput = false;
                change();
                currentRecipe = Optional.empty();
                return;
            }

            // 放置输出
            ItemStack output = recipe.getResultItem(level.registryAccess()).copy();
            itemHandler.setStackInSlot(outputSlot, output);

            // 播放音效
            level.playSound(null, worldPosition, Sounds.FERMENTATION_BARREL.get(), SoundSource.BLOCKS, 1.0f, 1.0f);

            // 记录产物到 pendingSlots
            pendingSlots.clear();
            if (!output.isEmpty()) {
                pendingSlots.put(outputSlot, output.copy());
            }

            isProcessingOutput = false; // 恢复

            // 重置配方和进度
            currentRecipe = Optional.empty();
            change();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    private void change() {
        progress = 0;
        currentRecipe = Optional.empty();
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public int getProgress() {
        return progress;
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public ItemStack getItemInSlot(int slot) {
        return itemHandler.getStackInSlot(slot);
    }

    public void setItemInSlot(int slot, ItemStack stack) {
        itemHandler.setStackInSlot(slot, stack);
    }

    // ---------- 数据同步 ----------
    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    // ---------- NBT ----------
    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
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
    protected void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("progress");
        LoadItemList.loadItemsFromTag(itemHandler, tag, registries);

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

        // 配方会在 tick 中自动重新匹配，不需要手动恢复
        currentRecipe = Optional.empty();
        // 如果进度 > 0 但物品变化导致配方不匹配，tick 会调用 change() 重置
    }
}