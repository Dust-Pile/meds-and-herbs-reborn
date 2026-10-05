package com.juiceybeans.meds_and_herbs.recipe;

import com.google.gson.JsonObject;
import com.juiceybeans.meds_and_herbs.init.MHRecipes;
import com.juiceybeans.meds_and_herbs.init.MHTags;
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

    private static final Ingredient EMPTY_BOTTLE = Ingredient.of(MHTags.Items.EMPTY_BOTTLE);

    private final ResourceLocation id;
    private final Ingredient powder;
    private final int powderCount;
    private final Optional<Ingredient> solvent;
    private final ItemStack output;
    private final ItemStack spillage;

    public ExtractionRecipe(ResourceLocation id,
                            Ingredient powder,
                            int powderCount,
                            Optional<Ingredient> solvent,
                            ItemStack output,
                            ItemStack spillage) {
        this.id = id;
        this.powder = powder;
        this.powderCount = powderCount;
        this.solvent = solvent;
        this.output = output;
        this.spillage = spillage;
    }

    public Ingredient getPowder() { return powder; }
    public int getPowderCount() { return powderCount; }
    public Optional<Ingredient> getSolvent() { return solvent; }
    public Ingredient getEmptyBottle() { return EMPTY_BOTTLE; }
    public ItemStack getOutput() { return output; }
    public ItemStack getSpillage() { return spillage; }

    @Override
    public boolean matches(@NotNull Container container, @NotNull Level level) {
        if (!powder.test(container.getItem(0)) || container.getItem(0).getCount() < powderCount) return false;

        if (solvent.isPresent()) {
            if (!solvent.get().test(container.getItem(1))) return false;
        } else if (!container.getItem(1).isEmpty()) {
            return false;
        }

        if (!EMPTY_BOTTLE.test(container.getItem(2))) return false;

        return true;
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
        solvent.ifPresent(list::add);
        list.add(EMPTY_BOTTLE);
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
            int powderCount = GsonHelper.getAsInt(GsonHelper.getAsJsonObject(json, "powder"), "count", 1);

            Optional<Ingredient> solvent = Optional.empty();
            if (json.has("solvent")) {
                solvent = Optional.of(Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "solvent")));
            }

            ItemStack output = CraftingHelper.getItemStack(
                    GsonHelper.getAsJsonObject(json, "output"), true);

            ItemStack spillage = ItemStack.EMPTY;
            if (json.has("spillage")) {
                spillage = CraftingHelper.getItemStack(
                        GsonHelper.getAsJsonObject(json, "spillage"), true);
            }

            return new ExtractionRecipe(recipeId, powder, powderCount, solvent, output, spillage);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buf, @NotNull ExtractionRecipe recipe) {
            recipe.powder.toNetwork(buf);
            buf.writeVarInt(recipe.powderCount);

            buf.writeBoolean(recipe.solvent.isPresent());
            recipe.solvent.ifPresent(ing -> ing.toNetwork(buf));

            buf.writeItem(recipe.output);
            buf.writeItem(recipe.spillage);
        }

        @Override
        public @NotNull ExtractionRecipe fromNetwork(@NotNull ResourceLocation recipeId,
                                                     @NotNull FriendlyByteBuf buf) {
            Ingredient powder = Ingredient.fromNetwork(buf);
            int powderCount = buf.readVarInt();

            Optional<Ingredient> solvent = Optional.empty();
            if (buf.readBoolean()) {
                solvent = Optional.of(Ingredient.fromNetwork(buf));
            }

            ItemStack output = buf.readItem();
            ItemStack spillage = buf.readItem();

            return new ExtractionRecipe(recipeId, powder, powderCount, solvent, output, spillage);
        }
    }
}