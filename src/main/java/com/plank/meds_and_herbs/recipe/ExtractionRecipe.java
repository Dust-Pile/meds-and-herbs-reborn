package com.plank.meds_and_herbs.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.plank.meds_and_herbs.init.Items;
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
import java.util.Optional;

public record ExtractionRecipe(
        DistillingIngredient powder,
        DistillingIngredient solvent,
        Optional<DistillingIngredient> emptyBottle,
        ItemStack output,
        ItemStack stillage
) implements Recipe<ExtractionRecipeInput> {
    public static final int MAX_PROGRESS = 200;

    @Override
    public boolean matches(ExtractionRecipeInput inv, @Nonnull Level level) {
        // 1. 检查粉末
        if (!powder.test(inv.powder())) return false;

        // 2. 检查溶剂
        if (!solvent.test(inv.solvent())) return false;

        // 3. 检查空瓶（如果配方需要）
        if (emptyBottle.isPresent()) {
            if (!emptyBottle.get().test(inv.emptyBottle())) return false;
        } else {
            // 配方不需要空瓶，输入槽必须为空
            if (!inv.emptyBottle().isEmpty()) return false;
        }

        // 4. 检查过滤棉
        return inv.filter().is(Items.COTTON_FILTER.get());
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull ExtractionRecipeInput inv, @Nonnull HolderLookup.Provider registries) {
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
        list.add(powder.ingredient());
        list.add(solvent.ingredient());
        emptyBottle.ifPresent(ing -> list.add(ing.ingredient()));
        return list;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return Recipes.EXTRACTION_SERIALIZER.get();
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return Recipes.EXTRACTION_TYPE.get();
    }

    // ----- 序列化器 -----
    public static class Serializer implements RecipeSerializer<ExtractionRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        @Nonnull
        public MapCodec<ExtractionRecipe> codec() {
            return RecordCodecBuilder.mapCodec(inst -> inst.group(
                    DistillingIngredient.CODEC.fieldOf("powder").forGetter(r -> r.powder),
                    DistillingIngredient.CODEC.fieldOf("solvent").forGetter(r -> r.solvent),
                    DistillingIngredient.CODEC.optionalFieldOf("emptyBottle").forGetter(r -> r.emptyBottle),
                    ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output),
                    ItemStack.CODEC.optionalFieldOf("stillage", ItemStack.EMPTY).forGetter(r -> r.stillage)
            ).apply(inst, ExtractionRecipe::new));
        }

        @Override
        @Nonnull
        public StreamCodec<RegistryFriendlyByteBuf, ExtractionRecipe> streamCodec() {
            return StreamCodec.of(
                    (buf, recipe) -> {
                        DistillingIngredient.STREAM_CODEC.encode(buf, recipe.powder);
                        DistillingIngredient.STREAM_CODEC.encode(buf, recipe.solvent);
                        ByteBufCodecs.optional(DistillingIngredient.STREAM_CODEC)
                                .encode(buf, recipe.emptyBottle);
                        ItemStack.STREAM_CODEC.encode(buf, recipe.output);
                        ByteBufCodecs.optional(ItemStack.STREAM_CODEC)
                                .encode(buf, Optional.of(recipe.stillage).filter(s -> !s.isEmpty()));
                    },
                    buf -> {
                        DistillingIngredient powder = DistillingIngredient.STREAM_CODEC.decode(buf);
                        DistillingIngredient solvent = DistillingIngredient.STREAM_CODEC.decode(buf);
                        Optional<DistillingIngredient> emptyBottle =
                                ByteBufCodecs.optional(DistillingIngredient.STREAM_CODEC).decode(buf);
                        ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                        ItemStack stillage = ByteBufCodecs.optional(ItemStack.STREAM_CODEC)
                                .decode(buf).orElse(ItemStack.EMPTY);
                        return new ExtractionRecipe(powder, solvent, emptyBottle, output, stillage);
                    }
            );
        }
    }
}