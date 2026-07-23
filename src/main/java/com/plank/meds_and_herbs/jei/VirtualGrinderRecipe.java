package com.plank.meds_and_herbs.jei;

import com.plank.meds_and_herbs.init.Recipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * 用于 JEI 展示的虚拟研磨配方，不会实际参与合成。
 */
public record VirtualGrinderRecipe(ItemStack input, ItemStack output) implements Recipe<SingleRecipeInput> {

    @Override
    public boolean matches(@Nonnull SingleRecipeInput inputContainer, @Nonnull Level level) {
        return false; // 永远不会匹配，防止被游戏使用
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull SingleRecipeInput inputContainer, @Nonnull HolderLookup.Provider registries) {
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
        // 将输入 ItemStack 转换为 Ingredient，供 JEI 显示
        return NonNullList.of(Ingredient.EMPTY, Ingredient.of(input));
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        // 返回一个占位序列化器（因为不会被实际序列化）
        return Recipes.GRINDER_SERIALIZER.get(); // 复用研磨序列化器
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return Recipes.GRINDER_TYPE.get();
    }
}