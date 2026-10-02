package com.plank.meds_and_herbs.client.gui.menu;

import com.plank.meds_and_herbs.block.entity.IncubatorBlockEntity;
import com.plank.meds_and_herbs.init.MHMenus;
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

public class IncubatorGUIMenu extends AbstractContainerMenu {

    private final IncubatorBlockEntity blockEntity;
    private final ItemStackHandler handler;
    private final ContainerData data;

    public IncubatorGUIMenu(int id, Inventory inv, IncubatorBlockEntity be) {
        super(MHMenus.INCUBATOR_GUI.get(), id);
        this.blockEntity = be;
        this.handler = be.getItemHandler();
        this.data = be.getContainerData();
        addDataSlots(data);
        addSlots(inv);
    }

    public IncubatorGUIMenu(int id, Inventory inv) {
        super(MHMenus.INCUBATOR_GUI.get(), id);
        this.blockEntity = null;
        this.handler = new ItemStackHandler(8);
        this.data = new SimpleContainerData(16);
        addDataSlots(data);
        addSlots(inv);
    }

    private void addSlots(Inventory inv) {
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 4; col++) {
                int index = row * 4 + col;
                int x = 53 + col * 18;
                int y = 26 + row * 18;
                addSlot(new SlotItemHandler(handler, index, x, y));
            }
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, 9 + col + row * 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }

    public int getProgress(int index) {
        return data.get(index);
    }

    public int getMaxProgress(int index) {
        return data.get(index + 8);
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < 8) {
            if (!moveItemStackTo(stack, 8, 44, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            boolean moved = moveItemStackTo(stack, 0, 8, false);
            if (!moved) {
                if (index < 35) {
                    if (!moveItemStackTo(stack, 35, 44, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!moveItemStackTo(stack, 8, 35, false)) {
                        return ItemStack.EMPTY;
                    }
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