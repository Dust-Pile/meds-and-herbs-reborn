package com.plank.meds_and_herbs.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import javax.annotation.Nonnull;

public record ExtractionRecipeInput(
        ItemStack powder,
        ItemStack solvent,
        ItemStack emptyBottle,
        ItemStack filter
) implements RecipeInput {

    @Override
    @Nonnull
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> powder;
            case 1 -> solvent;
            case 2 -> emptyBottle;
            case 3 -> filter;
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
    }

    @Override
    public int size() {
        return 4;
    }
}