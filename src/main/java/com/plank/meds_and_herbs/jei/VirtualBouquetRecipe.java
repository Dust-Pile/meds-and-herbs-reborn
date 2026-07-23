package com.plank.meds_and_herbs.jei;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 用于 JEI 展示的虚拟工作台配方，不会实际参与合成。
 * 用于展示特定花束组合的合成配方（4 朵特定花 → 特定花束变体）。
 */
public record VirtualBouquetRecipe(List<ItemStack> ingredients, ItemStack output) implements CraftingRecipe {

    @Override
    public boolean matches(@Nonnull CraftingInput container, @Nonnull Level level) {
        return false; // 永远不会匹配，防止被游戏使用
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull CraftingInput container, @Nonnull HolderLookup.Provider registries) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 2 && height >= 2 && width * height >= 4;
    }

    @Override
    @Nonnull
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        return output;
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        // 将输入 ItemStack 列表转换为 Ingredient 列表
        NonNullList<Ingredient> list = NonNullList.create();
        for (ItemStack stack : ingredients) {
            if (!stack.isEmpty()) {
                list.add(Ingredient.of(stack));
            } else {
                // 空槽位用 Ingredient.EMPTY 占位，但 JEI 会显示为空槽
                list.add(Ingredient.EMPTY);
            }
        }
        // 确保正好 4 个（如果不足则补空）
        while (list.size() < 4) {
            list.add(Ingredient.EMPTY);
        }
        return list;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        // 返回一个占位序列化器（不会被实际序列化）
        return net.minecraft.world.item.crafting.RecipeSerializer.SHAPELESS_RECIPE; // 使用原版无序序列化器
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    @Override
    @Nonnull
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }
}