package com.plank.meds_and_herbs.procedures;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public interface LoadItemList {
    static void loadItemsFromTag(ItemStackHandler itemHandler, CompoundTag tag, HolderLookup.Provider registries) {
        // 清空所有槽位
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }

        // 优先从标准的 "inventory" 标签解析
        if (tag.contains("inventory", Tag.TAG_COMPOUND)) {
            CompoundTag invTag = tag.getCompound("inventory");
            if (invTag.contains("Items", Tag.TAG_LIST)) {
                load(itemHandler, invTag.getList("Items", Tag.TAG_COMPOUND), registries);
                return; // 已解析，直接返回
            }
        }

        // 兼容旧格式：直接包含 "Items" 标签
        if (tag.contains("Items", Tag.TAG_LIST)) {
            load(itemHandler, tag.getList("Items", Tag.TAG_COMPOUND), registries);
        }
    }
    static void load(ItemStackHandler itemHandler, ListTag list, HolderLookup.Provider registries) {
        for (int i = 0; i < list.size(); i++) {
            CompoundTag itemTag = list.getCompound(i);
            int slot = itemTag.getInt("Slot");
            if (slot >= 0 && slot < itemHandler.getSlots()) {
                ItemStack stack = ItemStack.parse(registries, itemTag).orElse(ItemStack.EMPTY);
                itemHandler.setStackInSlot(slot, stack);
            }
        }
    }
}
