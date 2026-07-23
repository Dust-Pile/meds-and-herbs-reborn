package com.plank.meds_and_herbs.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.plank.meds_and_herbs.init.Recipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 蒸馏配方（支持组件匹配，允许一个输入为空，主输出槽可放空瓶）
 */
public record DistillingRecipe(
        Optional<DistillingIngredient> inputA,      // 输入A（可选）
        Optional<DistillingIngredient> inputB,      // 输入B（可选）
        Optional<DistillingIngredient> emptyBottle, // 空瓶输入（可选，放入主输出槽）
        ItemStack output,                           // 主产物（放入主输出槽）
        ItemStack stillage                          // 残留物（放入副产物槽）
) implements Recipe<DistillingRecipeInput> {
    public static final int MAX_PROGRESS = 200;

    @Override
    public boolean matches(DistillingRecipeInput inv, @Nonnull Level level) {
        ItemStack actualA = inv.inputA();
        ItemStack actualB = inv.inputB();

        // 收集需要的非空输入
        List<DistillingIngredient> required = new ArrayList<>();
        inputA.ifPresent(required::add);
        inputB.ifPresent(required::add);

        // 收集实际非空物品
        List<ItemStack> actual = new ArrayList<>();
        if (!actualA.isEmpty()) actual.add(actualA);
        if (!actualB.isEmpty()) actual.add(actualB);

        // 数量必须一致
        if (required.size() != actual.size()) return false;

        if (required.isEmpty()) return false;

        // 只有一个输入的情况
        if (required.size() == 1) {
            return required.getFirst().test(actual.getFirst());
        }

        // 两个输入的情况：检查两种互换顺序
        DistillingIngredient ingA = required.get(0);
        DistillingIngredient ingB = required.get(1);
        ItemStack item1 = actual.get(0);
        ItemStack item2 = actual.get(1);

        // 顺序匹配
        if (ingA.test(item1) && ingB.test(item2)) return true;
        // 互换匹配
        return ingA.test(item2) && ingB.test(item1);
    }

    /**
     * 检查空瓶是否匹配（在 BlockEntity 中调用）
     */
    public boolean matchesEmptyBottle(ItemStack bottle) {
        return emptyBottle.isEmpty() || emptyBottle.get().test(bottle);
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull DistillingRecipeInput inv, @Nonnull HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return output;
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        inputA.ifPresent(ing -> list.add(ing.ingredient()));
        inputB.ifPresent(ing -> list.add(ing.ingredient()));
        emptyBottle.ifPresent(ing -> list.add(ing.ingredient()));
        return list;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return Recipes.DISTILLING_SERIALIZER.get();
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return Recipes.DISTILLING_TYPE.get();
    }

    // ----- 序列化器 -----
    public static class Serializer implements RecipeSerializer<DistillingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        @Nonnull
        public MapCodec<DistillingRecipe> codec() {
            return RecordCodecBuilder.mapCodec(inst -> inst.group(
                    DistillingIngredient.CODEC.optionalFieldOf("inputA").forGetter(r -> r.inputA),
                    DistillingIngredient.CODEC.optionalFieldOf("inputB").forGetter(r -> r.inputB),
                    DistillingIngredient.CODEC.optionalFieldOf("emptyBottle").forGetter(r -> r.emptyBottle),
                    ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output),
                    ItemStack.CODEC.optionalFieldOf("stillage", ItemStack.EMPTY).forGetter(r -> r.stillage)
            ).apply(inst, DistillingRecipe::new));
        }

        @Override
        @Nonnull
        public StreamCodec<RegistryFriendlyByteBuf, DistillingRecipe> streamCodec() {
            return StreamCodec.of(
                    (buf, recipe) -> {
                        ByteBufCodecs.optional(DistillingIngredient.STREAM_CODEC).encode(buf, recipe.inputA);
                        ByteBufCodecs.optional(DistillingIngredient.STREAM_CODEC).encode(buf, recipe.inputB);
                        ByteBufCodecs.optional(DistillingIngredient.STREAM_CODEC).encode(buf, recipe.emptyBottle);
                        ItemStack.STREAM_CODEC.encode(buf, recipe.output);
                        ByteBufCodecs.optional(ItemStack.STREAM_CODEC)
                                .encode(buf, Optional.of(recipe.stillage).filter(s -> !s.isEmpty()));
                    },
                    buf -> {
                        Optional<DistillingIngredient> inputA =
                                ByteBufCodecs.optional(DistillingIngredient.STREAM_CODEC).decode(buf);
                        Optional<DistillingIngredient> inputB =
                                ByteBufCodecs.optional(DistillingIngredient.STREAM_CODEC).decode(buf);
                        Optional<DistillingIngredient> emptyBottle =
                                ByteBufCodecs.optional(DistillingIngredient.STREAM_CODEC).decode(buf);
                        ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                        ItemStack stillage = ByteBufCodecs.optional(ItemStack.STREAM_CODEC)
                                .decode(buf).orElse(ItemStack.EMPTY);
                        return new DistillingRecipe(inputA, inputB, emptyBottle, output, stillage);
                    }
            );
        }
    }
}