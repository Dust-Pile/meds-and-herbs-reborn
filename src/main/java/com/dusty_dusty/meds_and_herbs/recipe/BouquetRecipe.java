package com.dusty_dusty.meds_and_herbs.recipe;

import com.google.gson.JsonObject;
import com.dusty_dusty.meds_and_herbs.MedsAndHerbs;
import com.dusty_dusty.meds_and_herbs.init.MHItems;
import com.dusty_dusty.meds_and_herbs.init.MHRecipes;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class BouquetRecipe implements CraftingRecipe {

    public static final BouquetRecipe INSTANCE = new BouquetRecipe();
    public static final ResourceLocation ID = MedsAndHerbs.id("bouquet");

    public static final String NBT_KEY = "BouquetFlowers";
    public static final String FLOWERS_KEY = "flowers";
    public static final int REQUIRED_COUNT = 4;

    @Override
    public boolean matches(@Nonnull CraftingContainer container, @Nonnull Level level) {
        int flowerCount = 0;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && isFlower(stack)) {
                flowerCount++;
            }
        }
        return flowerCount == REQUIRED_COUNT;
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull CraftingContainer container,
                              @Nonnull RegistryAccess registries) {
        List<ItemStack> flowers = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty() && isFlower(stack)) {
                flowers.add(stack.copy());
            }
        }
        if (flowers.size() != REQUIRED_COUNT) {
            return ItemStack.EMPTY;
        }
        flowers.sort(Comparator.comparing(this::getSortKey));

        ItemStack bouquet = new ItemStack(MHItems.BOUQUET.get());

        ListTag listTag = new ListTag();
        for (ItemStack flower : flowers) {
            if (flower.isEmpty()) return ItemStack.EMPTY;
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(flower.getItem());
            listTag.add(StringTag.valueOf(id.toString()));
        }

        CompoundTag sub = new CompoundTag();
        sub.put(FLOWERS_KEY, listTag);
        bouquet.getOrCreateTag().put(NBT_KEY, sub);

        return bouquet;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= REQUIRED_COUNT;
    }

    @Override
    @Nonnull
    public ItemStack getResultItem(@Nonnull RegistryAccess registries) {
        ItemStack result = new ItemStack(MHItems.BOUQUET.get());

        Component hint = Component.translatable("jei.meds_and_herbs.bouquet.hint")
                .withStyle(ChatFormatting.GOLD);

        CompoundTag display = new CompoundTag();
        ListTag lore = new ListTag();
        lore.add(StringTag.valueOf(Component.Serializer.toJson(hint)));
        display.put("Lore", lore);

        result.getOrCreateTag().put("display", display);
        return result;
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        Ingredient flowerIngredient = Ingredient.of(ItemTags.FLOWERS);
        for (int i = 0; i < REQUIRED_COUNT; i++) {
            list.add(flowerIngredient);
        }
        return list;
    }

    public ResourceLocation getId() {
        return ID;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return MHRecipes.BOUQUET_SERIALIZER.get();
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

    private boolean isFlower(ItemStack stack) {
        return stack.is(ItemTags.FLOWERS);
    }

    private String getSortKey(ItemStack stack) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        String path = key.getPath();
        return path.isEmpty() ? "~" : path.substring(0, 1).toLowerCase(Locale.ROOT);
    }

    public static class Serializer implements RecipeSerializer<BouquetRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private Serializer() {}

        @Override
        @Nonnull
        public BouquetRecipe fromJson(@Nonnull ResourceLocation id,
                                      @Nonnull JsonObject json) {
            return BouquetRecipe.INSTANCE;
        }

        @Override
        public BouquetRecipe fromNetwork(@Nonnull ResourceLocation id,
                                         @Nonnull FriendlyByteBuf buf) {
            return BouquetRecipe.INSTANCE;
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buf,
                              @Nonnull BouquetRecipe recipe) {
        }
    }
}