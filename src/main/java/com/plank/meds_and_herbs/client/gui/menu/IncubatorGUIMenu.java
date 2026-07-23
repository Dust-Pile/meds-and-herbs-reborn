package com.plank.meds_and_herbs.client.gui.menu;

import com.plank.meds_and_herbs.block.entity.IncubatorBlockEntity;
import com.plank.meds_and_herbs.init.Menus;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class IncubatorGUIMenu extends AbstractContainerMenu {
    private final IncubatorBlockEntity blockEntity;
    private final ItemStackHandler handler;

    // 服务端使用的构造器（传入 BlockEntity）
    public IncubatorGUIMenu(int id, Inventory inv, IncubatorBlockEntity be) {
        super(Menus.INCUBATOR_GUI.get(), id);
        this.blockEntity = be;
        this.handler = be.getItemHandler();
        addSlots(inv);
    }

    // 客户端降级构造器（不抛出异常，使用空容器）
    public IncubatorGUIMenu(int id, Inventory inv) {
        super(Menus.INCUBATOR_GUI.get(), id);
        // 使用一个空的 8 槽容器作为降级
        this.blockEntity = null;
        this.handler = new ItemStackHandler(8);
        addSlots(inv);
    }

    private void addSlots(Inventory inv) {
        // 培养皿槽位：4列 x 2行
        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 4; col++) {
                int index = row * 4 + col;
                int x = 53 + col * 18;
                int y = 26 + row * 18;
                addSlot(new SlotItemHandler(handler, index, x, y));
            }
        }

        // 玩家背包
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inv, 9 + col + row * 9, 8 + col * 18, 84 + row * 18));
            }
        }
        // 快捷栏
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inv, col, 8 + col * 18, 142));
        }
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index < 8) {
            if (!this.moveItemStackTo(stack, 8, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(stack, 0, 8, false)) {
                return ItemStack.EMPTY;
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
        if (blockEntity == null) {
            // 降级模式下，总是有效（避免报错）
            return true;
        }
        return player.distanceToSqr(
                blockEntity.getBlockPos().getX() + 0.5,
                blockEntity.getBlockPos().getY() + 0.5,
                blockEntity.getBlockPos().getZ() + 0.5
        ) <= 64.0;
    }
}
