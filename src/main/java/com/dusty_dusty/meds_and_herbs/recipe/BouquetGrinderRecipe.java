package com.dusty_dusty.meds_and_herbs.recipe;

import com.google.gson.JsonObject;
import com.dusty_dusty.meds_and_herbs.MedsAndHerbs;
import com.dusty_dusty.meds_and_herbs.init.MHItems;
import com.dusty_dusty.meds_and_herbs.init.MHRecipes;
import com.dusty_dusty.meds_and_herbs.item.Bouquet;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

public class BouquetGrinderRecipe extends GrinderRecipe {
    public static final BouquetGrinderRecipe INSTANCE = new BouquetGrinderRecipe();
    public static final ResourceLocation ID = MedsAndHerbs.id("bouquet_grinder");

    public BouquetGrinderRecipe() {
        super(ID, Ingredient.of(MHItems.BOUQUET.get()), new ItemStack(MHItems.POWDER_HERBAL.get()));
    }

    @Override
    public boolean matches(@Nonnull Container input, @Nonnull Level level) {
        ItemStack stack = input.getItem(0);
        return !stack.isEmpty() && stack.is(MHItems.BOUQUET.get()) && isValid(stack);
    }

    @Override
    @Nonnull
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        ItemStack bouquet = container.getItem(0);

        if (bouquet.isEmpty() || !isValid(bouquet)) {
            return ItemStack.EMPTY;
        }

        ItemStack powder = new ItemStack(MHItems.POWDER_HERBAL.get());
        powder.setTag(bouquet.getTag().copy());
        return powder;
    }

    private static boolean isValid(ItemStack bouquetStack) {
        // check for tags present
        CompoundTag tag = bouquetStack.getTag();
        if (tag == null || !tag.contains("BouquetFlowers", Tag.TAG_COMPOUND)) {
            return false;
        }
        CompoundTag bouquetTag = tag.getCompound("BouquetFlowers");
        if (!bouquetTag.contains("flowers", Tag.TAG_LIST)) {
            return false;
        }

        // check if 4 flowers
        ListTag flowers = bouquetTag.getList("flowers", Tag.TAG_STRING);
        if (flowers.size() != 4) {
            return false;
        }

        // check if flowers exist
        for (int i = 0; i < flowers.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(flowers.getString(i));
            if (id == null || !ForgeRegistries.ITEMS.containsKey(id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        ItemStack powder = new ItemStack(MHItems.POWDER_HERBAL.get());
        List<ResourceLocation> flowers = Bouquet.createFlowersList(Items.ORANGE_TULIP, Items.DANDELION, Items.CORNFLOWER, Items.LILY_OF_THE_VALLEY);
        Bouquet.setFlowerData(powder, flowers);

        Component hint = Component.translatable("jei.meds_and_herbs.bouquet_grinder.hint")
                .withStyle(ChatFormatting.GOLD);

        CompoundTag display = new CompoundTag();
        ListTag lore = new ListTag();
        lore.add(StringTag.valueOf(Component.Serializer.toJson(hint)));
        display.put("Lore", lore);

        powder.getOrCreateTag().put("display", display);
        return powder;
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        ItemStack bouquet = new ItemStack(MHItems.BOUQUET.get());
        List<ResourceLocation> flowers = Bouquet.createFlowersList(Items.ORANGE_TULIP, Items.DANDELION, Items.CORNFLOWER, Items.LILY_OF_THE_VALLEY);
        Bouquet.setFlowerData(bouquet, flowers);
        
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(Ingredient.of(bouquet));
        return list;
    }

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return MHRecipes.BOUQUET_GRINDER_SERIALIZER.get();
    }

    @Override
    @Nonnull
    public RecipeType<?> getType() {
        return MHRecipes.GRINDER_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<BouquetGrinderRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        private Serializer() {}

        @Override
        @Nonnull
        public BouquetGrinderRecipe fromJson(@Nonnull ResourceLocation recipeId,
                                             @Nonnull JsonObject json) {
            return BouquetGrinderRecipe.INSTANCE;
        }

        @Override
        public BouquetGrinderRecipe fromNetwork(@Nonnull ResourceLocation recipeId,
                                                @Nonnull FriendlyByteBuf buffer) {
            return BouquetGrinderRecipe.INSTANCE;
        }

        @Override
        public void toNetwork(@Nonnull FriendlyByteBuf buffer,
                              @Nonnull BouquetGrinderRecipe recipe) {
        }
    }
}