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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public record FermentationRecipe(NonNullList<Ingredient> ingredients, ItemStack output) implements Recipe<RecipeInput> {
    public static final int DEFAULT_COOKING_TIME = 1200;

    @Override
    public boolean matches(RecipeInput input, @Nonnull Level level) {
        // 收集所有非空物品
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }
        // 非空物品数量必须与 ingredients 数量一致
        if (items.size() != ingredients.size()) {
            return false;
        }
        // 复制 ingredients 列表
        List<Ingredient> remaining = new ArrayList<>(ingredients);
        for (ItemStack stack : items) {
            boolean matched = false;
            Iterator<Ingredient> it = remaining.iterator();
            while (it.hasNext()) {
                Ingredient ing = it.next();
                if (ing.test(stack)) {
                    it.remove();
                    matched = true;
                    break;
                }
            }
            if (!matched) {
                return false;
            }
        }
        return remaining.isEmpty();
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull RecipeInput input, @Nonnull HolderLookup.Provider registries) {
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
        return ingredients;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return Recipes.FERMENTATION_SERIALIZER.get();
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return Recipes.FERMENTATION_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<FermentationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final MapCodec<FermentationRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients")
                                .xmap(NonNullList::copyOf, NonNullList::copyOf).forGetter(r -> r.ingredients),
                        ItemStack.CODEC.fieldOf("output").forGetter(r -> r.output)
                ).apply(inst, FermentationRecipe::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, FermentationRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeInt(recipe.ingredients.size());
                    for (Ingredient ing : recipe.ingredients) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ing);
                    }
                    ItemStack.STREAM_CODEC.encode(buf, recipe.output);
                },
                buf -> {
                    int size = buf.readInt();
                    NonNullList<Ingredient> ingredients = NonNullList.create();
                    for (int i = 0; i < size; i++) {
                        ingredients.add(Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                    }
                    ItemStack output = ItemStack.STREAM_CODEC.decode(buf);
                    return new FermentationRecipe(ingredients, output);
                }
        );

        @Override
        @Nonnull
        public MapCodec<FermentationRecipe> codec() {
            return CODEC;
        }

        @Override
        @Nonnull
        public StreamCodec<RegistryFriendlyByteBuf, FermentationRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}