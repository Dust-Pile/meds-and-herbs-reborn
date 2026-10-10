package com.dusty_dusty.meds_and_herbs.recipe;

import com.google.gson.JsonObject;
import com.dusty_dusty.meds_and_herbs.init.MHRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class GrinderRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient input;
    private final ItemStack output;

    public GrinderRecipe(ResourceLocation id, Ingredient input, ItemStack output) {
        this.id = id;
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(@Nonnull Container container, @Nonnull Level level) {
        return input.test(container.getItem(0));
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull Container container,
                              @Nonnull RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getResultItem(@Nonnull RegistryAccess registryAccess) {
        return output;
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, input);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return MHRecipes.GRINDER_SERIALIZER.get();
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return MHRecipes.GRINDER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<GrinderRecipe> {

        public static final Serializer INSTANCE = new Serializer();

        @Override
        @Nonnull
        public GrinderRecipe fromJson(@Nonnull ResourceLocation recipeId,
                                      @Nonnull JsonObject json) {
            Ingredient input = Ingredient.fromJson(
                    GsonHelper.getAsJsonObject(json, "input")
            );

            ItemStack output = ShapedRecipe.itemStackFromJson(
                    GsonHelper.getAsJsonObject(json, "output")
            );

            return new GrinderRecipe(recipeId, input, output);
        }

        @Override
        public GrinderRecipe fromNetwork(@Nonnull ResourceLocation recipeId,
                                         @Nonnull FriendlyByteBuf buffer) {
            var id = buffer.readResourceLocation();
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack output = buffer.readItem();

            return new GrinderRecipe(id, input, output);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buffer,
                              @Nonnull GrinderRecipe recipe) {
            recipe.input.toNetwork(buffer);
            buffer.writeItem(recipe.output);
        }
    }
}
