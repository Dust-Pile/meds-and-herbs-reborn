package com.plank.meds_and_herbs.jei;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;

public final class JeiHelper {
    private JeiHelper() {}

    /**
     * 从 Ingredient 提取组件补丁（递归支持 CompoundIngredient）
     */
    private static DataComponentPatch extractComponentPatch(Ingredient ingredient) {
        if (ingredient.getCustomIngredient() instanceof DataComponentIngredient dataComponentIngredient) {
            return dataComponentIngredient.components().asPatch();
        }
        if (ingredient.getCustomIngredient() instanceof CompoundIngredient(java.util.List<Ingredient> children)) {
            for (Ingredient child : children) {
                DataComponentPatch patch = extractComponentPatch(child);
                if (!patch.isEmpty()) {
                    return patch;
                }
            }
        }
        return DataComponentPatch.EMPTY;
    }

    /**
     * 构建带组件的 ItemStack，用于 JEI 显示
     */
    public static ItemStack createItemStackWithComponents(Ingredient ingredient, int count) {
        if (ingredient == null) return ItemStack.EMPTY;
        ItemStack[] items = ingredient.getItems();
        if (items.length == 0) return ItemStack.EMPTY;

        ItemStack stack = items[0].copy();
        stack.setCount(count);

        // 如果 ingredient 是 DataComponentIngredient，其 getItems() 已经包含组件，
        // 但为了安全，我们仍尝试提取补丁并应用（防止某些版本不包含）
        DataComponentPatch patch = extractComponentPatch(ingredient);
        if (!patch.isEmpty()) {
            stack.applyComponents(patch);
        }
        return stack;
    }
}