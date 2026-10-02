package com.plank.meds_and_herbs.block.entity;

import com.plank.meds_and_herbs.client.gui.menu.IncubatorGUIMenu;
import com.plank.meds_and_herbs.init.MHBlockEntities;
import com.plank.meds_and_herbs.init.MHRecipes;
import com.plank.meds_and_herbs.recipe.IncubatorRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class IncubatorBlockEntity extends BlockEntity implements MenuProvider, Container {

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
            updateContainerData();
        }
    };

    private final IncubatorRecipe[] currentRecipe = new IncubatorRecipe[8];
    private final ItemStack[] lastStacks = new ItemStack[8];

    private final ContainerData data = new SimpleContainerData(6);

    public IncubatorBlockEntity(BlockPos pos, BlockState state) {
        super(MHBlockEntities.INCUBATOR.get(), pos, state);
        for (int i = 0; i < 8; i++) {
            lastStacks[i] = ItemStack.EMPTY;
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.of(this::getItemHandler).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public int getContainerSize() {
        return 8;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < 8; i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getItem(int slot) {
        return itemHandler.getStackInSlot(slot);
    }

    @Override
    @Nonnull
    public ItemStack removeItem(int slot, int amount) {
        return itemHandler.extractItem(slot, amount, false);
    }

    @Override
    @Nonnull
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = itemHandler.getStackInSlot(slot);
        itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, @Nonnull ItemStack stack) {
        itemHandler.setStackInSlot(slot, stack);
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < 8; i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
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

            if (!ItemStack.isSameItemSameTags(stack, lastStacks[slot])) {
                currentRecipe[slot] = null;
                lastStacks[slot] = stack.copy();
                dirty = true;
            }

            if (currentRecipe[slot] == null) {
                currentRecipe[slot] = findRecipe(level, stack).orElse(null);
                if (currentRecipe[slot] == null) continue;
            }

            IncubatorRecipe recipe = currentRecipe[slot];
            int progress = -1;
            int maxProgress = 0;

            var tag = stack.getTag();
            if (tag != null && tag.contains("PetriDishData", CompoundTag.TAG_COMPOUND)) {
                progress = tag.getCompound("PetriDishData").getInt("progress");
                maxProgress = tag.getCompound("PetriDishData").getInt("maxProgress");
            }

            if (progress < 0 || maxProgress != recipe.getProcessingTime()) {
                progress = 0;
                maxProgress = recipe.getProcessingTime();

                CompoundTag petri = new CompoundTag();
                petri.putInt("progress", progress);
                petri.putInt("maxProgress", maxProgress);
                stack.getOrCreateTag().put("PetriDishData", petri);

                itemHandler.setStackInSlot(slot, stack);
                dirty = true;
            }

            if (progress >= maxProgress) {
                ItemStack output = recipe.assemble(this, level.registryAccess());
                itemHandler.setStackInSlot(slot, output);

                currentRecipe[slot] = null;
                lastStacks[slot] = ItemStack.EMPTY;
                dirty = true;
                continue;
            }

            CompoundTag petri = new CompoundTag();
            petri.putInt("progress", progress + 1);
            petri.putInt("maxProgress", maxProgress);
            stack.getOrCreateTag().put("PetriDishData", petri);

            itemHandler.setStackInSlot(slot, stack);
            dirty = true;
        }

        if (dirty) {
            setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            updateContainerData();
        }
    }

    private Optional<IncubatorRecipe> findRecipe(Level level, ItemStack stack) {
        SimpleContainer container = new SimpleContainer(stack);
        return level.getRecipeManager()
                .getRecipeFor(MHRecipes.INCUBATOR_TYPE.get(), container, level);
    }

    public void updateContainerData() {
        for (int i = 0; i < 8; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty()) {
                data.set(i, 0);
                data.set(i + 8, 0);
                continue;
            }

            int progress = -1;
            int maxProgress = 0;

            var tag = stack.getTag();
            if (tag != null && tag.contains("PetriDishData", CompoundTag.TAG_COMPOUND)) {
                progress = tag.getCompound("PetriDishData").getInt("progress");
                maxProgress = tag.getCompound("PetriDishData").getInt("maxProgress");
            }

            if (progress < 0) {
                data.set(i, 0);
                data.set(i + 8, 0);
            } else {
                data.set(i, progress);
                data.set(i + 8, maxProgress);
            }
        }
    }

    public ContainerData getContainerData() {
        return data;
    }

    public ItemStackHandler getItemHandler() { return itemHandler; }
    public ItemStack getItemInSlot(int slot) { return itemHandler.getStackInSlot(slot); }

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
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void handleUpdateTag(@Nonnull CompoundTag tag) {
        load(tag);
    }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        for (int i = 0; i < 8; i++) {
            currentRecipe[i] = null;
            lastStacks[i] = ItemStack.EMPTY;
        }
        updateContainerData();
    }
}