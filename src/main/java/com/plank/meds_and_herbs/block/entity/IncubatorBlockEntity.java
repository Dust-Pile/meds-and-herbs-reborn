package com.plank.meds_and_herbs.block.entity;

import com.plank.meds_and_herbs.client.gui.menu.IncubatorGUIMenu;
import com.plank.meds_and_herbs.data.PetriDishData;
import com.plank.meds_and_herbs.init.BlockEntities;
import com.plank.meds_and_herbs.init.DataComponents;
import com.plank.meds_and_herbs.init.Recipes;
import com.plank.meds_and_herbs.recipe.IncubatorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class IncubatorBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(8) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (level == null) return false;
            SingleRecipeInput input = new SingleRecipeInput(stack);
            return level.getRecipeManager()
                    .getRecipeFor(Recipes.INCUBATOR_TYPE.get(), input, level)
                    .isPresent();
        }

        // 🔥 核心修改：检测物品是否有孵化器配方，有则禁止提取，无则允许提取
        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack stack = getStackInSlot(slot);
            if (stack.isEmpty() || level == null) {
                return ItemStack.EMPTY;
            }

            // 检查物品是否匹配任何孵化器配方（即是否为原料）
            SingleRecipeInput input = new SingleRecipeInput(stack);
            boolean hasRecipe = level.getRecipeManager()
                    .getRecipeFor(Recipes.INCUBATOR_TYPE.get(), input, level)
                    .isPresent();

            if (hasRecipe) {
                // 有配方 → 原料，不允许提取
                return ItemStack.EMPTY;
            }

            // 没有配方 → 产物或无关物品，允许提取
            return super.extractItem(slot, amount, simulate);
        }
    };

    private final IncubatorRecipe[] currentRecipe = new IncubatorRecipe[8];
    private final ItemStack[] lastStacks = new ItemStack[8];
    private final ContainerData data = new SimpleContainerData(16) {
        @Override
        public void set(int index, int value) {
            // 只读，客户端不能修改
        }
    };

    public IncubatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.INCUBATOR.get(), pos, state);
        for (int i = 0; i < 8; i++) {
            lastStacks[i] = ItemStack.EMPTY;
        }
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        boolean dirty = false;
        for (int slot = 0; slot < 8; slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);

            if (stack.isEmpty()) {
                if (currentRecipe[slot] != null) {
                    currentRecipe[slot] = null;
                    dirty = true;
                }
                lastStacks[slot] = ItemStack.EMPTY;
                continue;
            }

            // 检测物品是否变化（只比较物品类型，忽略组件）
            if (stack.getItem() != lastStacks[slot].getItem()) {
                currentRecipe[slot] = null;
                lastStacks[slot] = stack.copy();
                dirty = true;
            }

            if (currentRecipe[slot] == null) {
                SingleRecipeInput input = new SingleRecipeInput(stack);
                Optional<IncubatorRecipe> recipeOpt = level.getRecipeManager()
                        .getRecipeFor(Recipes.INCUBATOR_TYPE.get(), input, level)
                        .map(RecipeHolder::value);
                if (recipeOpt.isPresent()) {
                    currentRecipe[slot] = recipeOpt.get();
                } else {
                    continue;
                }
            }

            IncubatorRecipe recipe = currentRecipe[slot];
            PetriDishData dishData = stack.get(DataComponents.PETRI_DISH_DATA.get());

            if (dishData == null) {
                dishData = new PetriDishData(0, recipe.processingTime());
                stack.set(DataComponents.PETRI_DISH_DATA.get(), dishData);
                itemHandler.setStackInSlot(slot, stack);
                dirty = true;
            } else {
                if (dishData.maxProgress() != recipe.processingTime()) {
                    dishData = new PetriDishData(0, recipe.processingTime());
                    stack.set(DataComponents.PETRI_DISH_DATA.get(), dishData);
                    itemHandler.setStackInSlot(slot, stack);
                    dirty = true;
                }
            }

            if (dishData.isComplete()) {
                // 完成处理：手动清空槽位（不通过 extractItem，避免被拦截）
                // 先保存输入用于配方匹配（但此处不需要，因为已经匹配了）
                ItemStack consumed = stack.copy();
                itemHandler.setStackInSlot(slot, ItemStack.EMPTY); // 清空

                SingleRecipeInput input = new SingleRecipeInput(consumed);
                ItemStack output = recipe.assemble(input, level.registryAccess());
                if (!output.isEmpty()) {
                    // 产物放入槽位（产物通常没有 PetriDishData 组件）
                    itemHandler.setStackInSlot(slot, output);
                }

                currentRecipe[slot] = null;
                lastStacks[slot] = ItemStack.EMPTY;
                dirty = true;
                continue;
            }

            // 增加进度
            if (level.random.nextFloat() < 0.05f) {
                int newProgress = Math.min(dishData.progress() + 1, dishData.maxProgress());
                PetriDishData newData = new PetriDishData(newProgress, dishData.maxProgress());
                stack.set(DataComponents.PETRI_DISH_DATA.get(), newData);
                itemHandler.setStackInSlot(slot, stack);
                dirty = true;
            }
        }

        if (dirty) {
            setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            updateContainerData();
        }
    }

    public void updateContainerData() {
        for (int i = 0; i < 8; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty() || !stack.has(DataComponents.PETRI_DISH_DATA.get())) {
                data.set(i, 0);
                data.set(i + 8, 0);
            } else {
                PetriDishData dishData = stack.get(DataComponents.PETRI_DISH_DATA.get());
                if (dishData != null) {
                    data.set(i, dishData.progress());
                    data.set(i + 8, dishData.maxProgress());
                }
            }
        }
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public ItemStack getItemInSlot(int slot) {
        return itemHandler.getStackInSlot(slot);
    }

    @Override
    @Nonnull
    public Component getDisplayName() {
        return Component.translatable("container.meds_and_herbs.incubator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @Nonnull Inventory inv, @Nonnull Player player) {
        return new IncubatorGUIMenu(id, inv, this);
    }

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

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", itemHandler.serializeNBT(registries));
        CompoundTag lastTag = new CompoundTag();
        for (int i = 0; i < 8; i++) {
            if (!lastStacks[i].isEmpty()) {
                lastTag.put("slot_" + i, lastStacks[i].save(registries));
            }
        }
        if (!lastTag.isEmpty()) {
            tag.put("lastStacks", lastTag);
        }
    }

    @Override
    protected void loadAdditional(@Nonnull CompoundTag tag, @Nonnull HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        if (tag.contains("lastStacks")) {
            CompoundTag lastTag = tag.getCompound("lastStacks");
            for (int i = 0; i < 8; i++) {
                if (lastTag.contains("slot_" + i)) {
                    lastStacks[i] = ItemStack.parse(registries, lastTag.getCompound("slot_" + i)).orElse(ItemStack.EMPTY);
                } else {
                    lastStacks[i] = ItemStack.EMPTY;
                }
            }
        }
        for (int i = 0; i < 8; i++) {
            currentRecipe[i] = null;
        }
        updateContainerData();
    }
}