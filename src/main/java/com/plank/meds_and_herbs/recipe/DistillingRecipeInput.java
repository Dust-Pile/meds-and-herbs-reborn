package com.plank.meds_and_herbs.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import javax.annotation.Nonnull;

public record DistillingRecipeInput(ItemStack inputA, ItemStack inputB) implements RecipeInput {
    @Override
    @Nonnull
    public ItemStack getItem(int index) {
        return switch (index) {
            case 0 -> inputA;
            case 1 -> inputB;
            default -> throw new IllegalArgumentException("No item for index " + index);
        };
    }
    @Override
    public int size() { return 2; }
}