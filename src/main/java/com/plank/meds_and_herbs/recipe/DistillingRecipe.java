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
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DistillingRecipe implements Recipe<Container> {
    public static final int MAX_PROGRESS = 200;

    private final ResourceLocation id;
    private final Optional<Ingredient> inputA;
    private final Optional<Ingredient> inputB;
    private final Optional<Ingredient> emptyBottle;
    private final ItemStack output;
    private final ItemStack spillage;

    public DistillingRecipe(ResourceLocation id,
                            Optional<Ingredient> inputA,
                            Optional<Ingredient> inputB,
                            Optional<Ingredient> emptyBottle,
                            ItemStack output,
                            ItemStack spillage) {
        this.id = id;
        this.inputA = inputA;
        this.inputB = inputB;
        this.emptyBottle = emptyBottle;
        this.output = output;
        this.spillage = spillage;
    }

    @Override
    public boolean matches(@Nonnull Container container, @Nonnull Level level) {
        ItemStack actualA = container.getItem(0);
        ItemStack actualB = container.getItem(1);

        List<Ingredient> required = new ArrayList<>();
        inputA.ifPresent(required::add);
        inputB.ifPresent(required::add);

        List<ItemStack> actual = new ArrayList<>();
        if (!actualA.isEmpty()) actual.add(actualA);
        if (!actualB.isEmpty()) actual.add(actualB);

        if (required.isEmpty() || required.size() != actual.size()) return false;
        if (required.size() == 1) return required.get(0).test(actual.get(0));

        Ingredient ingA = required.get(0);
        Ingredient ingB = required.get(1);
        ItemStack item1 = actual.get(0);
        ItemStack item2 = actual.get(1);

        return (ingA.test(item1) && ingB.test(item2))
                || (ingA.test(item2) && ingB.test(item1));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return output.copy();
    }

    public boolean matchesEmptyBottle(ItemStack bottle) {
        return emptyBottle.isEmpty() || emptyBottle.get().test(bottle);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output;
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        inputA.ifPresent(list::add);
        inputB.ifPresent(list::add);
        emptyBottle.ifPresent(list::add);
        return list;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return MHRecipes.DISTILLING_SERIALIZER.get();
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return MHRecipes.DISTILLING_TYPE.get();
    }

    public Optional<Ingredient> getInputA() {
        return inputA;
    }

    public Optional<Ingredient> getInputB() {
        return inputB;
    }

    public Optional<Ingredient> getEmptyBottle() {
        return emptyBottle;
    }

    public ItemStack getOutput() {
        return output;
    }

    public ItemStack getSpillage() {
        return spillage;
    }

    public static class Serializer implements RecipeSerializer<DistillingRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        @Nonnull
        public DistillingRecipe fromJson(@Nonnull ResourceLocation id, @Nonnull JsonObject json) {
            Optional<Ingredient> inputA = readJsonIngredient(json, "inputA");
            Optional<Ingredient> inputB = readJsonIngredient(json, "inputB");
            Optional<Ingredient> emptyBottle = readJsonIngredient(json, "emptyBottle");

            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));
            ItemStack spillage = json.has("spillage")
                    ? ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "spillage"))
                    : ItemStack.EMPTY;

            return new DistillingRecipe(id, inputA, inputB, emptyBottle, output, spillage);
        }

        private static Optional<Ingredient> readJsonIngredient(JsonObject json, String key) {
            return json.has(key)
                    ? Optional.of(Ingredient.fromJson(json.get(key)))
                    : Optional.empty();
        }

        @Override
        @Nonnull
        public DistillingRecipe fromNetwork(@Nonnull ResourceLocation id, @Nonnull FriendlyByteBuf buf) {
            Optional<Ingredient> inputA = readOptional(buf);
            Optional<Ingredient> inputB = readOptional(buf);
            Optional<Ingredient> emptyBottle = readOptional(buf);
            ItemStack output = buf.readItem();
            ItemStack spillage = buf.readItem();
            return new DistillingRecipe(id, inputA, inputB, emptyBottle, output, spillage);
        }

        private static Optional<Ingredient> readOptional(FriendlyByteBuf buf) {
            return buf.readBoolean()
                    ? Optional.of(Ingredient.fromNetwork(buf))
                    : Optional.empty();
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buf, @Nonnull DistillingRecipe recipe) {
            writeOptional(buf, recipe.inputA);
            writeOptional(buf, recipe.inputB);
            writeOptional(buf, recipe.emptyBottle);
            buf.writeItem(recipe.output);
            buf.writeItem(recipe.spillage);
        }

        private static void writeOptional(FriendlyByteBuf buf, Optional<Ingredient> opt) {
            if (opt.isPresent()) {
                buf.writeBoolean(true);
                opt.get().toNetwork(buf);
            } else {
                buf.writeBoolean(false);
            }
        }
    }
}