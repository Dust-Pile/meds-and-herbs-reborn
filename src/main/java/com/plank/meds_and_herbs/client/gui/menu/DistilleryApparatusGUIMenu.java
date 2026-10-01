package com.plank.meds_and_herbs.client.gui.menu;

import com.plank.meds_and_herbs.block.entity.DistilleryApparatusBlockEntity;
import com.plank.meds_and_herbs.init.MHMenus;
import com.plank.meds_and_herbs.init.MHTags;
import com.plank.meds_and_herbs.recipe.DistillingRecipe;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class DistilleryApparatusGUIMenu extends AbstractContainerMenu {
    private final DistilleryApparatusBlockEntity blockEntity;
    private final ContainerData data;
    private final ItemStackHandler handler;

    public DistilleryApparatusGUIMenu(int id, Inventory inv, DistilleryApparatusBlockEntity be) {
        super(MHMenus.DISTILLERY_APPARATUS_GUI.get(), id);
        this.blockEntity = be;
        this.handler = be.getItemHandler();
        this.data = new SimpleContainerData(2);
        addDataSlots(data);
        addSlots(inv);
    }

    public DistilleryApparatusGUIMenu(int id, Inventory inv) {
        super(MHMenus.DISTILLERY_APPARATUS_GUI.get(), id);
        this.blockEntity = null;
        this.handler = new ItemStackHandler(5);
        this.data = new SimpleContainerData(2);
        addDataSlots(data);
        addSlots(inv);
    }

    private void addSlots(Inventory inv) {
        addSlot(new SlotItemHandler(handler, 0, 44, 17)); // input a
        addSlot(new SlotItemHandler(handler, 1, 62, 17)); // input b
        addSlot(new SlotItemHandler(handler, 2, 116, 17)); // bottle
        addSlot(new SlotItemHandler(handler, 3, 53, 62) { // output
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return false;
            }
        });
        addSlot(new SlotItemHandler(handler, 4, 44, 62) { // spillage
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return false;
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int k = 0; k < 9; ++k) {
            addSlot(new Slot(inv, k, 8 + k * 18, 142));
        }
    }

    @Override
    public void broadcastChanges() {
        if (blockEntity != null) {
            data.set(0, blockEntity.getProgress());
            data.set(1, blockEntity.isRunning() ? 1 : 0);
        }
        super.broadcastChanges();
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return blockEntity != null ? blockEntity.getMaxProgress() : DistillingRecipe.MAX_PROGRESS;
    }

    public boolean isRunning() {
        return data.get(1) == 1;
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < 5) {
            if (!moveItemStackTo(stack, 5, 41, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            boolean moved = false;
            if (stack.is(MHTags.Items.EMPTY_BOTTLE)) {
                moved = moveItemStackTo(stack, 2, 3, false);
            }
            if (!moved) {
                moved = moveItemStackTo(stack, 0, 2, false);
            }
            if (!moved) {
                if (index < 32) {
                    if (!moveItemStackTo(stack, 32, 41, false)) return ItemStack.EMPTY;
                } else {
                    if (!moveItemStackTo(stack, 5, 32, true)) return ItemStack.EMPTY;
                }
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        return blockEntity != null && blockEntity.stillValid(player);
    }
}