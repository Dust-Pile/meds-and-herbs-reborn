package com.plank.meds_and_herbs.recipe;

import com.mojang.serialization.MapCodec;
import com.plank.meds_and_herbs.data.BouquetFlowers;
import com.plank.meds_and_herbs.init.DataComponents;
import com.plank.meds_and_herbs.init.Items;
import com.plank.meds_and_herbs.init.Recipes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.List;

public class BouquetGrinderRecipe implements Recipe<SingleRecipeInput> {
    public static final BouquetGrinderRecipe INSTANCE = new BouquetGrinderRecipe();

    @Override
    public boolean matches(@Nonnull SingleRecipeInput input, @Nonnull Level level) {
        ItemStack stack = input.getItem(0);
        if (stack.isEmpty() || !stack.is(Items.BOUQUET.get())) return false;
        BouquetFlowers flowers = stack.get(DataComponents.BOUQUET_FLOWERS.get());
        return flowers != null && flowers.isValid();
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull SingleRecipeInput input, @Nonnull HolderLookup.Provider registries) {
        ItemStack bouquet = input.getItem(0);
        BouquetFlowers flowers = bouquet.get(DataComponents.BOUQUET_FLOWERS.get());
        if (flowers == null || !flowers.isValid()) {
            return ItemStack.EMPTY;
        }
        ItemStack powder = new ItemStack(Items.POWDER_HERBAL.get());
        powder.set(DataComponents.BOUQUET_FLOWERS.get(), flowers);
        return powder;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    // ✅ JEI 显示用的结果：带提示
    @Override
    @Nonnull
    public ItemStack getResultItem(@Nonnull HolderLookup.Provider registries) {
        ItemStack result = new ItemStack(Items.POWDER_HERBAL.get());
        // 添加提示：保留花朵类型
        Component hint = Component.translatable("jei.meds_and_herbs.bouquet_grinder.hint")
                .withStyle(ChatFormatting.GOLD);
        result.set(net.minecraft.core.component.DataComponents.LORE, new ItemLore(List.of(hint)));
        return result;
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(Ingredient.of(Items.BOUQUET.get()));
        return list;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return Recipes.BOUQUET_GRINDER_SERIALIZER.get();
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return Recipes.GRINDER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BouquetGrinderRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private Serializer() {}

        @Override
        public @Nonnull MapCodec<BouquetGrinderRecipe> codec() {
            return MapCodec.unit(BouquetGrinderRecipe.INSTANCE);
        }

        @Override
        public @Nonnull StreamCodec<RegistryFriendlyByteBuf, BouquetGrinderRecipe> streamCodec() {
            return StreamCodec.unit(BouquetGrinderRecipe.INSTANCE);
        }
    }
}