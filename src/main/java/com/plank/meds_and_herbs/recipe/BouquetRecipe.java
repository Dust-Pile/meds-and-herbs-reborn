package com.plank.meds_and_herbs.recipe;

import com.mojang.serialization.MapCodec;
import com.plank.meds_and_herbs.data.BouquetFlowers;
import com.plank.meds_and_herbs.init.DataComponents;
import com.plank.meds_and_herbs.init.Items;
import com.plank.meds_and_herbs.init.Recipes;
import com.plank.meds_and_herbs.init.Tags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * 花束合成配方（硬编码）
 * 使用 4 朵任意带有 meds_and_herbs:flowers 标签的花合成花束。
 * 实现 CraftingRecipe 以兼容 JEI 等模组。
 */
public class BouquetRecipe implements CraftingRecipe {

    public static final BouquetRecipe INSTANCE = new BouquetRecipe();

    @Override
    public boolean matches(CraftingInput container, @Nonnull Level level) {
        int flowerCount = 0;
        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && isFlower(stack)) {
                flowerCount++;
            }
        }
        return flowerCount == 4;
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull CraftingInput container, @Nonnull HolderLookup.Provider registries) {
        List<ItemStack> flowers = new ArrayList<>();
        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && isFlower(stack)) {
                flowers.add(stack.copy());
            }
        }
        if (flowers.size() != 4) {
            return ItemStack.EMPTY;
        }
        flowers.sort(Comparator.comparing(this::getSortKey));
        ItemStack bouquet = new ItemStack(Items.BOUQUET.get());
        bouquet.set(DataComponents.BOUQUET_FLOWERS.get(), BouquetFlowers.create(flowers));
        return bouquet;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 4;
    }

    @Override
    @Nonnull
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        ItemStack result = new ItemStack(Items.BOUQUET.get());
        // 添加一条 lore 提示（仅用于 JEI 显示）
        Component hint = Component.translatable("jei.meds_and_herbs.bouquet.hint").withStyle(ChatFormatting.GOLD);
        result.set(net.minecraft.core.component.DataComponents.LORE, new ItemLore(List.of(hint)));
        return result;
    }

    // ✅ 返回 4 个花朵标签输入（供 JEI 显示）
    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        Ingredient flowerIngredient = Ingredient.of(Tags.Items.FLOWERS);
        for (int i = 0; i < 4; i++) {
            list.add(flowerIngredient);
        }
        return list;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return Recipes.BOUQUET_SERIALIZER.get();
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

    // ----- 辅助方法 -----

    private boolean isFlower(ItemStack stack) {
        return stack.is(Tags.Items.FLOWERS);
    }

    private String getSortKey(ItemStack stack) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String path = key.getPath();
        return path.isEmpty() ? "~" : path.substring(0, 1).toLowerCase(Locale.ROOT);
    }

    // ----- 序列化器 -----

    public static class Serializer implements RecipeSerializer<BouquetRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        private Serializer() {}

        @Override
        public @Nonnull MapCodec<BouquetRecipe> codec() {
            return MapCodec.unit(BouquetRecipe.INSTANCE);
        }

        @Override
        public @Nonnull StreamCodec<RegistryFriendlyByteBuf, BouquetRecipe> streamCodec() {
            return StreamCodec.unit(BouquetRecipe.INSTANCE);
        }
    }
}