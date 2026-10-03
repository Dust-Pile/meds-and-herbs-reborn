package com.plank.meds_and_herbs.procedures;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public interface LoadItemList {
    static void loadItemsFromTag(ItemStackHandler itemHandler, CompoundTag tag) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }

        if (tag.contains("inventory", Tag.TAG_COMPOUND)) {
            CompoundTag invTag = tag.getCompound("inventory");
            if (invTag.contains("Items", Tag.TAG_LIST)) {
                load(itemHandler, invTag.getList("Items", Tag.TAG_COMPOUND));
                return;
            }
        }

        if (tag.contains("Items", Tag.TAG_LIST)) {
            load(itemHandler, tag.getList("Items", Tag.TAG_COMPOUND));
        }
    }

    static void load(ItemStackHandler itemHandler, ListTag list) {
        for (int i = 0; i < list.size(); i++) {
            CompoundTag itemTag = list.getCompound(i);
            int slot = itemTag.getInt("Slot");
            if (slot >= 0 && slot < itemHandler.getSlots()) {
                ItemStack stack = ItemStack.of(itemTag);
                itemHandler.setStackInSlot(slot, stack);
            }
        }
    }
}
