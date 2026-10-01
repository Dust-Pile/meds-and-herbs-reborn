package com.plank.meds_and_herbs.client.gui.menu;

import com.plank.meds_and_herbs.block.entity.ExtractionApparatusBlockEntity;
import com.plank.meds_and_herbs.init.MHMenus;
import com.plank.meds_and_herbs.init.MHTags;
import com.plank.meds_and_herbs.recipe.ExtractionRecipe;
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

public class ExtractApparatusGUIMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final ExtractionApparatusBlockEntity blockEntity;
    private final ItemStackHandler handler;

    public ExtractApparatusGUIMenu(int id, Inventory inv, ExtractionApparatusBlockEntity be) {
        super(MHMenus.EXTRACTION_APPARATUS_GUI.get(), id);
        this.blockEntity = be;
        this.handler = be.getItemHandler();
        // 容量改为 2：索引0=进度，索引1=是否在烹饪
        this.data = new SimpleContainerData(2);
        addDataSlots(data);
        addSlots(inv);
    }

    // 安全降级构造器
    public ExtractApparatusGUIMenu(int id, Inventory inv) {
        super(MHMenus.EXTRACTION_APPARATUS_GUI.get(), id);
        this.blockEntity = null;
        this.handler = new ItemStackHandler(5);
        this.data = new SimpleContainerData(2);
        addDataSlots(data);
        addSlots(inv);
    }

    private void addSlots(Inventory inv) {
        addSlot(new SlotItemHandler(handler, 0, 116, 26));
        addSlot(new SlotItemHandler(handler, 1, 116, 44));
        addSlot(new SlotItemHandler(handler, 2, 44, 62) );
        addSlot(new SlotItemHandler(handler, 3, 152, 62) {
            @Override public boolean mayPlace(@Nonnull ItemStack stack) { return false; }
        });
        addSlot(new SlotItemHandler(handler, 4, 44, 44));

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int hotbar = 0; hotbar < 9; ++hotbar) {
            addSlot(new Slot(inv, hotbar, 8 + hotbar * 18, 142));
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
        return blockEntity != null ? blockEntity.getMaxProgress() : ExtractionRecipe.MAX_PROGRESS;
    }

    /** 返回当前是否正在烹饪（满足所有条件且有匹配配方） */
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

        // 如果是容器槽位（0-4），移动到玩家背包
        if (index < 5) {
            if (!moveItemStackTo(stack, 5, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // 从玩家背包移动，根据物品类型匹配到对应槽位
            if (stack.is(MHTags.Items.POWDERS)) {
                moveToSlot(stack, 0);
            } else if (stack.is(MHTags.Items.EMPTY_BOTTLE)) {
                moveToSlot(stack, 2);
            } else if (stack.is(MHTags.Items.MEDICINE)) {
                moveToSlot(stack, 1);
            } else if (stack.is(MHTags.Items.FILTER)) {
                moveToSlot(stack, 4);
            } else {
                // 默认尝试放入槽2（输出/空瓶槽）
                if (!moveToSlot(stack, 2)) {
                    // 如果都放不下，尝试放入任意可用的输入槽
                    for (int i = 0; i < 5; i++) {
                        if (i != 3 && slots.get(i).mayPlace(stack)) {
                            if (moveItemStackTo(stack, i, i + 1, false)) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, stack);
        return original;
    }

    // 辅助方法：将物品移动到指定槽位
    private boolean moveToSlot(ItemStack stack, int slotIndex) {
        if (!slots.get(slotIndex).mayPlace(stack)) return false;
        if (slots.get(slotIndex).hasItem()) {
            ItemStack existing = slots.get(slotIndex).getItem();
            if (ItemStack.isSameItemSameTags(existing, stack) &&
                    existing.getCount() + stack.getCount() <= existing.getMaxStackSize()) {
                existing.grow(stack.getCount());
                stack.setCount(0);
                return true;
            }
            return false;
        } else {
            slots.get(slotIndex).set(stack.copy());
            stack.setCount(0);
            return true;
        }
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