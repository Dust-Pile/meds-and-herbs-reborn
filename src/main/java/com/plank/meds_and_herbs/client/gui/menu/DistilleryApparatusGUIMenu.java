package com.plank.meds_and_herbs.client.gui.menu;

import com.plank.meds_and_herbs.block.entity.DistilleryApparatusBlockEntity;
import com.plank.meds_and_herbs.init.Menus;
import com.plank.meds_and_herbs.init.Tags;
import com.plank.meds_and_herbs.recipe.DistillingRecipe;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class DistilleryApparatusGUIMenu extends AbstractContainerMenu {
    private final DistilleryApparatusBlockEntity blockEntity;
    private final ContainerData data;
    private final ItemStackHandler handler;

    // 服务端构造器
    public DistilleryApparatusGUIMenu(int id, Inventory inv, DistilleryApparatusBlockEntity be) {
        super(Menus.DISTILLERY_APPARATUS_GUI.get(), id);
        this.blockEntity = be;
        this.handler = be.getItemHandler();
        this.data = new SimpleContainerData(2);
        addDataSlots(data);
        addSlots(inv);
    }

    // 客户端降级构造器
    public DistilleryApparatusGUIMenu(int id, Inventory inv) {
        super(Menus.DISTILLERY_APPARATUS_GUI.get(), id);
        this.blockEntity = null;
        this.handler = new ItemStackHandler(4);
        this.data = new SimpleContainerData(2);
        addDataSlots(data);
        addSlots(inv);
    }

    private void addSlots(Inventory inv) {
        // 槽位: 输入A (0), 输入B (1), 主输出 (2), 副产出 (3)
        addSlot(new SlotItemHandler(handler, 0, 44, 17));
        addSlot(new SlotItemHandler(handler, 1, 62, 17));
        addSlot(new SlotItemHandler(handler, 2, 116, 17));
        addSlot(new SlotItemHandler(handler, 3, 53, 62) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return false;
            }
        });

        // 玩家背包
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
        super.broadcastChanges();
        if (blockEntity != null) {
            data.set(0, blockEntity.getProgress());
            data.set(1, blockEntity.isCooking() ? 1 : 0);
        }
    }

    public int getProgress() {
        return data.get(0);
    }

    public int getMaxProgress() {
        return blockEntity != null ? blockEntity.getMaxProgress() : DistillingRecipe.MAX_PROGRESS;
    }

    public boolean isCooking() {
        return data.get(1) == 1;
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        // 如果是机器槽位（0-3），移动到玩家背包
        if (index < 4) {
            if (!moveItemStackTo(stack, 4, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            // ⚠️ 不要提前返回，继续执行下面的逻辑
        } else {
            // 从玩家背包移入机器
            // 1. 如果是空瓶，优先放入槽2（主输出/空瓶输入）
            if (stack.is(Tags.Items.EMPTY_BOTTLE)) {
                if (moveItemStackTo(stack, 2, 3, false)) {
                    slot.setChanged();
                    return original;
                }
            }
            // 2. 尝试放入输入槽（0和1）
            if (moveItemStackTo(stack, 0, 2, false)) {
                slot.setChanged();
                return original;
            }
            // 3. 若所有尝试都失败，则返回空
        }

        // 统一处理槽位更新（与提取装置完全一致）
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return original;
    }

    @Override
    public boolean stillValid(@Nonnull Player player) {
        if (blockEntity != null) {
            return player.distanceToSqr(blockEntity.getBlockPos().getX() + 0.5,
                    blockEntity.getBlockPos().getY() + 0.5,
                    blockEntity.getBlockPos().getZ() + 0.5) <= 64;
        }
        return true;
    }
}