package com.plank.meds_and_herbs.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.plank.meds_and_herbs.init.Recipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.List;

public record IncubatorRecipe(
        ItemStack input,
        List<WeightedOutput> outputs,
        int processingTime
) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(@Nonnull SingleRecipeInput inputContainer, @Nonnull Level level) {
        // 只比较物品类型，忽略组件（如 PetriDishData）
        return inputContainer.getItem(0).getItem() == this.input.getItem();
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull SingleRecipeInput inputContainer, @Nonnull HolderLookup.Provider registries) {
        if (outputs.isEmpty()) return ItemStack.EMPTY;
        int totalWeight = outputs.stream().mapToInt(WeightedOutput::weight).sum();
        int rand = RandomSource.create().nextInt(totalWeight);
        int cumulative = 0;
        for (WeightedOutput wo : outputs) {
            cumulative += wo.weight();
            if (rand < cumulative) {
                return wo.itemStack().copy();
            }
        }
        return outputs.getFirst().itemStack().copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return outputs.isEmpty() ? ItemStack.EMPTY : outputs.getFirst().itemStack().copy();
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return Recipes.INCUBATOR_SERIALIZER.get();
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return Recipes.INCUBATOR_TYPE.get();
    }

    public record WeightedOutput(ItemStack itemStack, int weight) {
        public static final Codec<WeightedOutput> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        ItemStack.CODEC.fieldOf("item").forGetter(WeightedOutput::itemStack),
                        Codec.INT.fieldOf("weight").forGetter(WeightedOutput::weight)
                ).apply(inst, WeightedOutput::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, WeightedOutput> STREAM_CODEC =
                StreamCodec.composite(
                        ItemStack.STREAM_CODEC, WeightedOutput::itemStack,
                        ByteBufCodecs.INT, WeightedOutput::weight,
                        WeightedOutput::new
                );
    }

    public static class Serializer implements RecipeSerializer<IncubatorRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        @Nonnull
        public MapCodec<IncubatorRecipe> codec() {
            return RecordCodecBuilder.mapCodec(inst ->
                    inst.group(
                            ItemStack.CODEC.fieldOf("input").forGetter(IncubatorRecipe::input),
                            WeightedOutput.CODEC.listOf().fieldOf("outputs").forGetter(IncubatorRecipe::outputs),
                            Codec.INT.optionalFieldOf("processingTime", 200).forGetter(IncubatorRecipe::processingTime)
                    ).apply(inst, IncubatorRecipe::new)
            );
        }

        @Override
        @Nonnull
        public StreamCodec<RegistryFriendlyByteBuf, IncubatorRecipe> streamCodec() {
            return StreamCodec.composite(
                    ItemStack.STREAM_CODEC, IncubatorRecipe::input,
                    WeightedOutput.STREAM_CODEC.apply(ByteBufCodecs.list()), IncubatorRecipe::outputs,
                    ByteBufCodecs.INT, IncubatorRecipe::processingTime,
                    IncubatorRecipe::new
            );
        }
    }
}