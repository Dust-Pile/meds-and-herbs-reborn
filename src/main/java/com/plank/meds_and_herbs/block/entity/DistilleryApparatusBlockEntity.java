package com.plank.meds_and_herbs.block.entity;

import com.plank.meds_and_herbs.client.gui.menu.DistilleryApparatusGUIMenu;
import com.plank.meds_and_herbs.init.*;
import com.plank.meds_and_herbs.procedures.LoadItemList;
import com.plank.meds_and_herbs.recipe.DistillingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static com.plank.meds_and_herbs.recipe.DistillingRecipe.MAX_PROGRESS;

public class DistilleryApparatusBlockEntity extends BlockEntity implements MenuProvider, Container {

    private static final int SOUND_INTERVAL = 40;

    private final Map<Integer, ItemStack> pendingSlots = new HashMap<>();

    private final ItemStackHandler internalHandler = new ItemStackHandler(5) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return switch (slot) {
                case 0, 1 -> true;
                case 2 -> stack.is(MHTags.Items.EMPTY_BOTTLE);
                default -> false;
            };
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }

            ItemStack recorded = pendingSlots.get(slot);
            if (recorded != null && !ItemStack.isSameItemSameTags(recorded, getStackInSlot(slot))) {
                pendingSlots.remove(slot);
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return slot == 2 ? 1 : 64; // allow ONLY 1 bottle
        }
    };

    private final IItemHandler upHandler = new FilteredItemHandler(
            internalHandler,
            slot -> slot == 0 || slot == 1,
            slot -> false
    );

    private final IItemHandler sideHandler = new FilteredItemHandler(
            internalHandler,
            slot -> slot == 2,
            slot -> false
    );

    private final IItemHandler downHandler = new FilteredItemHandler(
            internalHandler,
            slot -> false,
            slot -> true
    ) {
        @Override
        @Nonnull
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack recorded = pendingSlots.get(slot);
            if (recorded == null || recorded.isEmpty()) return ItemStack.EMPTY;

            ItemStack current = internalHandler.getStackInSlot(slot);
            if (!ItemStack.isSameItemSameTags(recorded, current)) {
                pendingSlots.remove(slot);
                return ItemStack.EMPTY;
            }

            ItemStack result = internalHandler.extractItem(slot, amount, simulate);
            if (!simulate && !result.isEmpty()) {
                pendingSlots.remove(slot);
            }
            return result;
        }
    };

    private int progress = 0;
    private boolean isRunning = false;
    private int soundTimer = 0;

    public DistilleryApparatusBlockEntity(BlockPos pos, BlockState state) {
        super(MHBlockEntities.DISTILLERY_APPARATUS.get(), pos, state);
    }


    @Override
    public int getContainerSize() {
        return 5;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < 5; i++) {
            if (!internalHandler.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return internalHandler.getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return internalHandler.extractItem(slot, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        var stack = internalHandler.getStackInSlot(slot);
        internalHandler.setStackInSlot(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        internalHandler.setStackInSlot(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        return player.distanceToSqr(
                worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < 5; i++) {
            internalHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
        pendingSlots.clear();
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        Optional<DistillingRecipe> recipeOpt = level.getRecipeManager()
                .getRecipeFor(MHRecipes.DISTILLING_TYPE.get(), this, level);

        boolean shouldBeWorking = recipeOpt.isPresent() && canStartProcess(recipeOpt.get());
        isRunning = shouldBeWorking;

        if (shouldBeWorking) {
            if (progress < MAX_PROGRESS) {
                progress++;

                if (--soundTimer <= 0) {
                    soundTimer = SOUND_INTERVAL;
                    float pitch = 0.8f + level.random.nextFloat() * 0.4f;
                    level.playSound(null, worldPosition, MHSounds.DISTILLERY_APPARATUS.get(),
                            SoundSource.BLOCKS, 0.5f, pitch);
                }
            }
            if (progress >= MAX_PROGRESS) {
                craftRecipe(recipeOpt.get());
                progress = 0;
                level.playSound(null, worldPosition, MHSounds.DISTILLERY_APPARATUS.get(),
                        SoundSource.BLOCKS, 1.0f, 1.0f);
                setChanged();
            }
        } else if (progress != 0) {
            progress = 0;
        }
    }

    private boolean canStartProcess(DistillingRecipe recipe) {
        ItemStack bottle = internalHandler.getStackInSlot(2);

        // does recipe need bottle
        if (recipe.getEmptyBottle().isPresent()) {
            if (!recipe.matchesEmptyBottle(bottle)) return false; // not am empty bottle
        } else if (!bottle.isEmpty()) {  // recipe doesnt need bottle but bottle is present
            return false;
        }

        if (!canFit(3, recipe.getOutput())) return false;
        if (!recipe.getSpillage().isEmpty() && !canFit(4, recipe.getSpillage())) return false;

        return true;
    }

    private void craftRecipe(DistillingRecipe recipe) {
        pendingSlots.clear();

        ItemStack inputACopy = internalHandler.getStackInSlot(0).copy();
        ItemStack inputBCopy = internalHandler.getStackInSlot(1).copy();

        if (recipe.getInputA().isPresent()) {
            internalHandler.extractItem(0, 1, false);
        }
        if (recipe.getInputB().isPresent()) {
            internalHandler.extractItem(1, 1, false);
        }

        if (recipe.getEmptyBottle().isPresent()) {
            internalHandler.extractItem(2, 1, false);
            ItemStack dirty = new ItemStack(MHItems.DIRTY_MEDICINE_BOTTLE.get());
            internalHandler.setStackInSlot(2, dirty);
            pendingSlots.put(2, dirty.copy());
        }

        processRemainderAndRecord(0, inputACopy);
        processRemainderAndRecord(1, inputBCopy);

        insertItemSafeAndRecord(3, recipe.getOutput());
        insertItemSafeAndRecord(4, recipe.getSpillage());
    }

    private void processRemainderAndRecord(int slot, ItemStack stack) {
        ItemStack remainder = stack.getCraftingRemainingItem();
        if (remainder.isEmpty()) return;

        if (tryMergeStacks(slot, remainder)) return;
        if (tryMergeStacks(2, remainder)) return;
        if (tryMergeStacks(3, remainder)) return;
        if (tryMergeStacks(4, remainder)) return;

        dropItem(remainder);
    }

    private boolean tryMergeStacks(int slot, ItemStack stack) {
        ItemStack existing = internalHandler.getStackInSlot(slot);
        int limit = internalHandler.getSlotLimit(slot);

        if (existing.isEmpty()) {
            ItemStack placed = stack.copy();
            placed.setCount(Math.min(placed.getCount(), limit));
            internalHandler.setStackInSlot(slot, placed);
            pendingSlots.put(slot, placed.copy());
            return true;
        }

        if (ItemStack.isSameItemSameTags(existing, stack)
                && existing.getCount() + stack.getCount() <= existing.getMaxStackSize()
                && existing.getCount() + stack.getCount() <= limit) {
            existing.grow(stack.getCount());
            internalHandler.setStackInSlot(slot, existing);
            pendingSlots.put(slot, existing.copy());
            return true;
        }

        return false;
    }

    private void insertItemSafeAndRecord(int slot, ItemStack stack) {
        if (stack.isEmpty()) return;
        if (!tryMergeStacks(slot, stack)) {
            dropItem(stack);
        }
    }

    private boolean canFit(int slot, ItemStack stack) {
        if (stack.isEmpty()) return true;
        ItemStack existing = internalHandler.getStackInSlot(slot);
        if (existing.isEmpty()) return true;
        return ItemStack.isSameItemSameTags(existing, stack) &&
                existing.getCount() + stack.getCount() <= existing.getMaxStackSize();
    }

    private void dropItem(ItemStack stack) {
        if (level != null && !stack.isEmpty()) {
            Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
        }
    }

    public boolean isRunning() {
        return isRunning;
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

    public IItemHandler getHandlerForSide(@Nullable Direction side) {
        if (side == null) return internalHandler;
        return switch (side) {
            case UP -> upHandler;
            case DOWN -> downHandler;
            default -> sideHandler;
        };
    }

    @Override
    @Nonnull
    public Component getDisplayName() {
        return Component.translatable("container.meds_and_herbs.distillery_apparatus");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @Nonnull Inventory inv, @Nonnull Player player) {
        return new DistilleryApparatusGUIMenu(id, inv, this);
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
        tag.put("inventory", internalHandler.serializeNBT());
        tag.putInt("progress", progress);

        ListTag pendingTag = new ListTag();
        for (Map.Entry<Integer, ItemStack> entry : pendingSlots.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt("slot", entry.getKey());
            entryTag.put("stack", entry.getValue().save(new CompoundTag()));
            pendingTag.add(entryTag);
        }
        tag.put("pendingSlots", pendingTag);
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        progress = tag.getInt("progress");
        LoadItemList.loadItemsFromTag(internalHandler, tag);

        pendingSlots.clear();
        if (tag.contains("pendingSlots", Tag.TAG_LIST)) {
            ListTag pendingTag = tag.getList("pendingSlots", Tag.TAG_COMPOUND);
            for (int i = 0; i < pendingTag.size(); i++) {
                CompoundTag entryTag = pendingTag.getCompound(i);
                int slot = entryTag.getInt("slot");
                ItemStack stack = ItemStack.of(entryTag.getCompound("stack"));
                if (!stack.isEmpty()) {
                    pendingSlots.put(slot, stack);
                }
            }
        }
    }

    private static class FilteredItemHandler implements IItemHandler {
        private final IItemHandler delegate;
        private final Predicate<Integer> insertAllowed;
        private final Predicate<Integer> extractAllowed;

        public FilteredItemHandler(IItemHandler delegate,
                                   Predicate<Integer> insertAllowed,
                                   Predicate<Integer> extractAllowed) {
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
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (!insertAllowed.test(slot)) return false;
            return delegate.isItemValid(slot, stack);
        }

        @Override
        public int getSlotLimit(int slot) { return delegate.getSlotLimit(slot); }
    }
}