package com.plank.meds_and_herbs.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public record DistillingIngredient(Ingredient ingredient, int count) {

    /**
     * 检查给定的 ItemStack 是否匹配此 Ingredient（物品 + 数量）。
     */
    public boolean test(ItemStack stack) {
        return ingredient.test(stack) && stack.getCount() >= count;
    }

    // ----- Codec -----
    public static final Codec<DistillingIngredient> CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(DistillingIngredient::ingredient),
                    Codec.INT.optionalFieldOf("count", 1).forGetter(DistillingIngredient::count)
            ).apply(inst, DistillingIngredient::new)
    );

    // ----- StreamCodec（用于网络同步） -----
    public static final StreamCodec<RegistryFriendlyByteBuf, DistillingIngredient> STREAM_CODEC =
            StreamCodec.composite(
                    Ingredient.CONTENTS_STREAM_CODEC, DistillingIngredient::ingredient,
                    ByteBufCodecs.INT, DistillingIngredient::count,
                    DistillingIngredient::new
            );
}