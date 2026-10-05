package com.juiceybeans.meds_and_herbs.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.juiceybeans.meds_and_herbs.init.MHRecipes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class IncubatorRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final ItemStack input;
    private final List<WeightedOutput> outputs;
    private final int processingTime;

    public IncubatorRecipe(ResourceLocation id,
                           ItemStack input,
                           List<WeightedOutput> outputs,
                           int processingTime) {
        this.id = id;
        this.input = input;
        this.outputs = outputs;
        this.processingTime = processingTime;
    }

    public ItemStack getInput() { return input; }
    public List<WeightedOutput> getOutputs() { return outputs; }
    public int getProcessingTime() { return processingTime; }

    @Override
    public boolean matches(@Nonnull Container container, @Nonnull Level level) {
        ItemStack stack = container.getItem(0);
        if (stack.isEmpty()) return false;
        return ItemStack.isSameItem(stack, input) && stack.getCount() >= input.getCount();
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull Container container, @Nonnull RegistryAccess registries) {
        if (outputs.isEmpty()) return ItemStack.EMPTY;

        int totalWeight = outputs.stream().mapToInt(WeightedOutput::weight).sum();
        if (totalWeight <= 0) return outputs.get(0).itemStack().copy();

        int roll = RandomSource.create().nextInt(totalWeight);
        int cumulative = 0;
        for (WeightedOutput wo : outputs) {
            cumulative += wo.weight();
            if (roll < cumulative) return wo.itemStack().copy();
        }
        return outputs.get(0).itemStack().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getResultItem(@Nonnull RegistryAccess registryAccess) {
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.get(0).itemStack().copy();
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return id;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return MHRecipes.INCUBATOR_SERIALIZER.get();
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return MHRecipes.INCUBATOR_TYPE.get();
    }

    public record WeightedOutput(ItemStack itemStack, int weight) {}

    public static class Serializer implements RecipeSerializer<IncubatorRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        @Nonnull
        public IncubatorRecipe fromJson(@Nonnull ResourceLocation recipeId, @Nonnull JsonObject json) {
            ItemStack input = CraftingHelper.getItemStack(
                    GsonHelper.getAsJsonObject(json, "input"), true);

            JsonArray outputsArray = GsonHelper.getAsJsonArray(json, "outputs");
            List<WeightedOutput> outputs = new ArrayList<>();
            for (JsonElement element : outputsArray) {
                JsonObject obj = element.getAsJsonObject();
                ItemStack stack = CraftingHelper.getItemStack(
                        GsonHelper.getAsJsonObject(obj, "item"), true);
                int weight = GsonHelper.getAsInt(obj, "weight", 1);
                outputs.add(new WeightedOutput(stack, weight));
            }

            int processingTime = GsonHelper.getAsInt(json, "processingTime", 200);

            return new IncubatorRecipe(recipeId, input, outputs, processingTime);
        }

        @Override
        @Nullable
        public IncubatorRecipe fromNetwork(@Nonnull ResourceLocation recipeId, @Nonnull FriendlyByteBuf buf) {
            ItemStack input = buf.readItem();
            int size = buf.readVarInt();
            List<WeightedOutput> outputs = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ItemStack stack = buf.readItem();
                int weight = buf.readVarInt();
                outputs.add(new WeightedOutput(stack, weight));
            }
            int processingTime = buf.readVarInt();
            return new IncubatorRecipe(recipeId, input, outputs, processingTime);
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buf, @Nonnull IncubatorRecipe recipe) {
            buf.writeItem(recipe.input);
            buf.writeVarInt(recipe.outputs.size());
            for (WeightedOutput wo : recipe.outputs) {
                buf.writeItem(wo.itemStack());
                buf.writeVarInt(wo.weight());
            }
            buf.writeVarInt(recipe.processingTime);
        }
    }
}