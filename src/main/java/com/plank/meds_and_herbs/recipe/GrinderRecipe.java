package com.plank.meds_and_herbs.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.plank.meds_and_herbs.init.Recipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public record GrinderRecipe(Ingredient input, ItemStack output) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(SingleRecipeInput inv, @Nonnull Level level) {
        return input.test(inv.item());
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull SingleRecipeInput inv, @Nonnull HolderLookup.Provider registries) {
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
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return Recipes.GRINDER_SERIALIZER.get();
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return Recipes.GRINDER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<GrinderRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<GrinderRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(r -> r.input),
                        ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output)
                ).apply(inst, GrinderRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, GrinderRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input);
                    ItemStack.STREAM_CODEC.encode(buf, recipe.output);
                },
                buf -> {
                    Ingredient input = Ingredient.CONTENTS_STREAM_CODEC.decode(buf);
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                    return new GrinderRecipe(input, output);
                }
        );
        @Nonnull
        @Override
        public MapCodec<GrinderRecipe> codec() { return CODEC; }
        @Nonnull
        @Override
        public StreamCodec<RegistryFriendlyByteBuf, GrinderRecipe> streamCodec() { return STREAM_CODEC; }
    }
}