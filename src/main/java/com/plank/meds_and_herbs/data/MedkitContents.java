package com.plank.meds_and_herbs.data;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record MedkitContents(List<ItemStack> items) {
    public static final MedkitContents EMPTY = new MedkitContents(List.of());
    public static final int MAX_ITEMS = 16;

    public static final Codec<MedkitContents> CODEC =
            ItemStack.CODEC.listOf().xmap(MedkitContents::new, MedkitContents::items);
    public static final StreamCodec<RegistryFriendlyByteBuf, MedkitContents> STREAM_CODEC =
            ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).map(MedkitContents::new, MedkitContents::items);

    // 外部类实例方法：计算总数量
    public int totalCount() {
        return items.stream().mapToInt(ItemStack::getCount).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public Iterable<ItemStack> itemsCopy() {
        return items.stream().map(ItemStack::copy).toList();
    }
    // 创建 DataComponentType
    public static DataComponentType<MedkitContents> createComponentType() {
        return DataComponentType.<MedkitContents>builder()
                .persistent(CODEC)
                .networkSynchronized(STREAM_CODEC)
                .build();
    }


    // 可变操作类
    public static class Mutable {
        private final List<ItemStack> items;

        public Mutable(MedkitContents contents) {
            this.items = new ArrayList<>(contents.items);
        }

        // 内部计算总数量的方法
        private int totalCount() {
            return items.stream().mapToInt(ItemStack::getCount).sum();
        }

        public boolean tryInsert(ItemStack stack) {
            int newTotal = totalCount() + stack.getCount();
            if (newTotal > MAX_ITEMS) return false;

            ItemStack toInsert = stack.copy();
            stack.shrink(toInsert.getCount());
            items.add(toInsert);
            return true;
        }

        public ItemStack removeOne() {
            if (items.isEmpty()) return ItemStack.EMPTY;
            return items.removeLast().copy();
        }

        public MedkitContents toImmutable() {
            return new MedkitContents(List.copyOf(items));
        }
    }
}