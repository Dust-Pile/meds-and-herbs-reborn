package com.plank.meds_and_herbs.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public record BouquetFlowers(List<ResourceLocation> flowers) {

    public static final int REQUIRED_COUNT = 4;
    public static final BouquetFlowers EMPTY = new BouquetFlowers(List.of());

    // Codec
    public static final Codec<BouquetFlowers> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.listOf().optionalFieldOf("flowers", List.of()).forGetter(BouquetFlowers::flowers)
            ).apply(instance, BouquetFlowers::new)
    );

    // StreamCodec
    public static final StreamCodec<RegistryFriendlyByteBuf, BouquetFlowers> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), BouquetFlowers::flowers,
            BouquetFlowers::new
    );

    // 创建 DataComponentType
    public static DataComponentType<BouquetFlowers> createComponentType() {
        return DataComponentType.<BouquetFlowers>builder()
                .persistent(CODEC)
                .networkSynchronized(STREAM_CODEC)
                .build();
    }

    // 验证是否有效（恰好4个有效的物品ID）
    public boolean isValid() {
        if (flowers == null || flowers.size() != REQUIRED_COUNT) return false;
        return flowers.stream().allMatch(BuiltInRegistries.ITEM::containsKey);
    }

    // 获取 ItemStack 列表（用于工具提示显示）
    public List<ItemStack> getFlowerItems() {
        return flowers.stream()
                .map(id -> new ItemStack(BuiltInRegistries.ITEM.get(id), 1))
                .collect(Collectors.toList());
    }

    // 创建构造器 - 从 ItemStack 列表生成
    public static BouquetFlowers create(List<ItemStack> stacks) {
        if (stacks == null || stacks.size() != REQUIRED_COUNT) {
            return EMPTY;
        }
        List<ResourceLocation> ids = stacks.stream()
                .filter(s -> !s.isEmpty())
                .map(s -> BuiltInRegistries.ITEM.getKey(s.getItem()))
                .toList();
        if (ids.size() != REQUIRED_COUNT) {
            return EMPTY;
        }
        return new BouquetFlowers(List.copyOf(ids));
    }

    // 从 ResourceLocation 列表直接创建（可选）
    public static BouquetFlowers of(List<ResourceLocation> ids) {
        if (ids == null || ids.size() != REQUIRED_COUNT) {
            return EMPTY;
        }
        return new BouquetFlowers(List.copyOf(ids));
    }
}