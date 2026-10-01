package com.plank.meds_and_herbs.recipe;

import com.google.gson.JsonObject;
import com.plank.meds_and_herbs.init.MHRecipes;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ExtractionRecipe implements Recipe<Container> {
    public static final int MAX_PROGRESS = 200;

    private final ResourceLocation id;
    private final Ingredient powder;
    private final Ingredient solvent;
    private final Optional<Ingredient> emptyBottle;
    private final Ingredient filter;
    private final ItemStack output;
    private final ItemStack spillage;

    public ExtractionRecipe(ResourceLocation id,
                            Ingredient powder,
                            Ingredient solvent,
                            Optional<Ingredient> emptyBottle,
                            Ingredient filter,
                            ItemStack output,
                            ItemStack spillage) {
        this.id = id;
        this.powder = powder;
        this.solvent = solvent;
        this.emptyBottle = emptyBottle;
        this.filter = filter;
        this.output = output;
        this.spillage = spillage;
    }

    public Ingredient getPowder() { return powder; }
    public Ingredient getSolvent() { return solvent; }
    public Optional<Ingredient> getEmptyBottle() { return emptyBottle; }
    public Ingredient getFilter() { return filter; }
    public ItemStack getOutput() { return output; }
    public ItemStack getSpillage() { return spillage; }

    @Override
    public boolean matches(@NotNull Container container, @NotNull Level level) {
        if (!powder.test(container.getItem(0))) return false;
        if (!solvent.test(container.getItem(1))) return false;

        ItemStack bottle = container.getItem(2);
        if (emptyBottle.isPresent()) {
            if (!emptyBottle.get().test(bottle)) return false;
        } else if (!bottle.isEmpty()) {
            return false;
        }

        return filter.test(container.getItem(3));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container container, @NotNull RegistryAccess registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registries) {
        return output;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(powder);
        list.add(solvent);
        emptyBottle.ifPresent(list::add);
        list.add(filter);
        return list;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return MHRecipes.EXTRACTION_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return MHRecipes.EXTRACTION_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<ExtractionRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull ExtractionRecipe fromJson(@NotNull ResourceLocation recipeId,
                                                  @NotNull JsonObject json) {
            Ingredient powder = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "powder"));
            Ingredient solvent = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "solvent"));

            Optional<Ingredient> emptyBottle = Optional.empty();
            if (json.has("empty_bottle")) {
                emptyBottle = Optional.of(Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "empty_bottle")));
            }

            Ingredient filter = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "filter"));

            ItemStack output = CraftingHelper.getItemStack(
                    GsonHelper.getAsJsonObject(json, "output"), true);

            ItemStack spillage = ItemStack.EMPTY;
            if (json.has("spillage")) {
                spillage = CraftingHelper.getItemStack(
                        GsonHelper.getAsJsonObject(json, "spillage"), true);
            }

            return new ExtractionRecipe(recipeId, powder, solvent, emptyBottle, filter, output, spillage);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull ExtractionRecipe recipe) {
            recipe.powder.toNetwork(buf);
            recipe.solvent.toNetwork(buf);

            buf.writeBoolean(recipe.emptyBottle.isPresent());
            recipe.emptyBottle.ifPresent(ing -> ing.toNetwork(buf));

            recipe.filter.toNetwork(buf);
            buf.writeItem(recipe.output);
            buf.writeItem(recipe.spillage);
        }

        @Override
        public @NotNull ExtractionRecipe fromNetwork(@NotNull ResourceLocation recipeId,
                                                     @NotNull FriendlyByteBuf buf) {
            Ingredient powder = Ingredient.fromNetwork(buf);
            Ingredient solvent = Ingredient.fromNetwork(buf);

            Optional<Ingredient> emptyBottle = Optional.empty();
            if (buf.readBoolean()) {
                emptyBottle = Optional.of(Ingredient.fromNetwork(buf));
            }

            Ingredient filter = Ingredient.fromNetwork(buf);
            ItemStack output = buf.readItem();
            ItemStack spillage = buf.readItem();

            return new ExtractionRecipe(recipeId, powder, solvent, emptyBottle, filter, output, spillage);
        }
    }
}