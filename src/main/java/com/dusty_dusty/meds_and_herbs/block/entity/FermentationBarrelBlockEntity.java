package com.dusty_dusty.meds_and_herbs.block.entity;

import com.dusty_dusty.meds_and_herbs.init.MHBlockEntities;
import com.dusty_dusty.meds_and_herbs.init.MHRecipes;
import com.dusty_dusty.meds_and_herbs.init.MHSounds;
import com.dusty_dusty.meds_and_herbs.recipe.FermentationRecipe;
import com.dusty_dusty.meds_and_herbs.util.MHUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.antlr.v4.runtime.misc.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FermentationBarrelBlockEntity extends BlockEntity implements Container {

    private static final int MAX_PROGRESS = FermentationRecipe.DEFAULT_COOKING_TIME;

    private final Map<Integer, ItemStack> pendingSlots = new HashMap<>();

    private final ItemStackHandler itemHandler = new ItemStackHandler(10) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }

            if (slot < 9) {
                progress = 0;
                currentRecipe = null;
            }

            ItemStack recorded = pendingSlots.get(slot);
            if (recorded != null && !ItemStack.isSameItemSameTags(recorded, getStackInSlot(slot))) {
                pendingSlots.remove(slot);
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot < 9;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    };

    @Nullable
    private FermentationRecipe currentRecipe = null;
    private int progress = 0;

    public FermentationBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(MHBlockEntities.FERMENTATION_BARREL.get(), pos, state);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.of(this::getItemHandler).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override public int getContainerSize() { return 9; }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < 9; i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override @Nonnull public ItemStack getItem(int slot) { return itemHandler.getStackInSlot(slot); }

    @Override @Nonnull
    public ItemStack removeItem(int slot, int amount) {
        if (slot < 0 || slot >= 9) return ItemStack.EMPTY;
        return itemHandler.extractItem(slot, amount, false);
    }

    @Override @Nonnull
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot < 0 || slot >= 9) return ItemStack.EMPTY;
        ItemStack stack = itemHandler.getStackInSlot(slot);
        itemHandler.setStackInSlot(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, @Nonnull ItemStack stack) {
        if (slot < 0 || slot >= 9) return;
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
        for (int i = 0; i < 10; i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
        pendingSlots.clear();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) return;

        if (currentRecipe == null) {
            currentRecipe = level.getRecipeManager()
                    .getRecipeFor(MHRecipes.FERMENTATION_TYPE.get(), this, level)
                    .orElse(null);
            if (currentRecipe == null) {
                if (progress != 0) {
                    progress = 0;
                    setChanged();
                }
                return;
            }
        }

        FermentationRecipe cachedRecipe = currentRecipe;

        if (!cachedRecipe.matches(this, level)) {
            progress = 0;
            currentRecipe = null;
            setChanged();
            return;
        }

        if (progress < MAX_PROGRESS) {
            progress++;
            setChanged();
            if (progress % 40 == 0 && level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                        5, 0.5, 0.5, 0.5, 0.0);
            }
            return;
        }

        if (!itemHandler.getStackInSlot(9).isEmpty()) {
            return;
        }

        List<Ingredient> remaining = new ArrayList<>(cachedRecipe.getIngredients());
        for (int i = 0; i < 9; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            Iterator<Ingredient> it = remaining.iterator();
            while (it.hasNext()) {
                if (it.next().test(stack)) {
                    itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                    it.remove();
                    break;
                }
            }
        }

        RegistryAccess access = level.registryAccess();
        ItemStack output = cachedRecipe.getResultItem(access).copy();
        itemHandler.setStackInSlot(9, output);

        pendingSlots.clear();
        pendingSlots.put(9, output.copy());

        level.playSound(null, worldPosition, MHSounds.FERMENTATION_BARREL.get(),
                SoundSource.BLOCKS, 1.0f, 1.0f);

        progress = 0;
        cachedRecipe = null;
        setChanged();
        level.sendBlockUpdated(pos, state, state, 3);
    }

    public int getProgress() { return progress; }
    public ItemStackHandler getItemHandler() { return itemHandler; }
    public ItemStack getItemInSlot(int slot) { return itemHandler.getStackInSlot(slot); }
    public void setItemInSlot(int slot, ItemStack stack) { itemHandler.setStackInSlot(slot, stack); }
    public boolean isRunning() { return currentRecipe != null && progress > 0; }

    public void dropContents(Level level, BlockPos pos) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
    }

    @Override @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override @Nonnull
    public CompoundTag getUpdateTag() { return saveWithoutMetadata(); }

    @Override
    public void handleUpdateTag(@Nonnull CompoundTag tag) { load(tag); }

    @Override
    protected void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
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
        MHUtils.loadItemsFromTag(itemHandler, tag);

        pendingSlots.clear();
        if (tag.contains("pendingSlots", Tag.TAG_LIST)) {
            ListTag pendingTag = tag.getList("pendingSlots", Tag.TAG_COMPOUND);
            for (int i = 0; i < pendingTag.size(); i++) {
                CompoundTag entryTag = pendingTag.getCompound(i);
                int slot = entryTag.getInt("slot");
                ItemStack stack = ItemStack.of(entryTag.getCompound("stack"));
                if (!stack.isEmpty()) pendingSlots.put(slot, stack);
            }
        }
        currentRecipe = null;
    }
}