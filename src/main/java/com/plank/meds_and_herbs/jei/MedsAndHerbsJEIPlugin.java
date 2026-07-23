package com.plank.meds_and_herbs.jei;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.data.BouquetFlowers;
import com.plank.meds_and_herbs.data.MedicineData;
import com.plank.meds_and_herbs.init.Blocks;
import com.plank.meds_and_herbs.init.DataComponents;
import com.plank.meds_and_herbs.init.Items;
import com.plank.meds_and_herbs.init.Recipes;
import com.plank.meds_and_herbs.recipe.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JeiPlugin
public class MedsAndHerbsJEIPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_UID =
            ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, "jei_plugin");

    public static final RecipeType<ExtractionRecipe> EXTRACTION_TYPE =
            RecipeType.create(MedsAndHerbs.MODID, "extraction", ExtractionRecipe.class);

    public static final RecipeType<DistillingRecipe> DISTILLING_TYPE =
            RecipeType.create(MedsAndHerbs.MODID, "distilling", DistillingRecipe.class);

    public static final RecipeType<FermentationRecipe> FERMENTATION_TYPE =
            RecipeType.create(MedsAndHerbs.MODID, "fermentation", FermentationRecipe.class);

    public static final RecipeType<IncubatorRecipe> INCUBATOR_TYPE =
            RecipeType.create(MedsAndHerbs.MODID, "incubator", IncubatorRecipe.class);

    public static final RecipeType<Recipe<SingleRecipeInput>> GRINDER_TYPE =
            RecipeType.create(MedsAndHerbs.MODID, "grinder", GrinderRecipe.class);

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new ExtractionRecipeCategory(guiHelper),
                new DistillingRecipeCategory(guiHelper),
                new FermentationRecipeCategory(guiHelper),
                new IncubatorRecipeCategory(guiHelper),
                new GrinderRecipeCategory(guiHelper)
        );
    }

    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration registration) {
        if (Minecraft.getInstance().level != null) {
            RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
            // --- 1. 收集提取配方和花束组合 ---
            List<ExtractionRecipe> extractionRecipes = recipeManager
                    .getAllRecipesFor(Recipes.EXTRACTION_TYPE.get())
                    .stream()
                    .map(RecipeHolder::value)
                    .toList();

            Set<BouquetFlowers> uniqueFlowers = new HashSet<>();
            for (ExtractionRecipe recipe : extractionRecipes) {
                ItemStack powderStack = JeiHelper.createItemStackWithComponents(recipe.powder().ingredient(), 1);
                BouquetFlowers flowers = powderStack.get(DataComponents.BOUQUET_FLOWERS.get());
                if (flowers != null && flowers.isValid()) {
                    uniqueFlowers.add(flowers);
                }
            }

            // --- 2. 提取配方 ---
            registration.addRecipes(EXTRACTION_TYPE, extractionRecipes);

            // 蒸馏配方
            List<DistillingRecipe> distillingRecipes = recipeManager
                    .getAllRecipesFor(Recipes.DISTILLING_TYPE.get())
                    .stream()
                    .map(RecipeHolder::value)
                    .toList();
            registration.addRecipes(DISTILLING_TYPE, distillingRecipes);

            // 发酵配方
            List<FermentationRecipe> fermentationRecipes = recipeManager
                    .getAllRecipesFor(Recipes.FERMENTATION_TYPE.get())
                    .stream()
                    .map(RecipeHolder::value)
                    .toList();
            registration.addRecipes(FERMENTATION_TYPE, fermentationRecipes);

            // 孵化器配方
            List<IncubatorRecipe> incubatorRecipes = recipeManager
                    .getAllRecipesFor(Recipes.INCUBATOR_TYPE.get())
                    .stream()
                    .map(RecipeHolder::value)
                    .toList();
            registration.addRecipes(INCUBATOR_TYPE, incubatorRecipes);


            // 普通研磨配方
            List<Recipe<SingleRecipeInput>> grinderRecipes = recipeManager.getAllRecipesFor(Recipes.GRINDER_TYPE.get())
                    .stream()
                    .map(RecipeHolder::value).collect(Collectors.toList());
            grinderRecipes.add(BouquetGrinderRecipe.INSTANCE);

            for (BouquetFlowers flowers : uniqueFlowers) {
                ItemStack bouquet = new ItemStack(Items.BOUQUET.get());
                bouquet.set(DataComponents.BOUQUET_FLOWERS.get(), flowers);
                ItemStack powder = new ItemStack(Items.POWDER_HERBAL.get());
                powder.set(DataComponents.BOUQUET_FLOWERS.get(), flowers);
                grinderRecipes.add(new VirtualGrinderRecipe(bouquet, powder));
            }
            registration.addRecipes(GRINDER_TYPE, grinderRecipes);

            // --- 5. 花束合成配方（工作台） ---
            RecipeType<RecipeHolder<CraftingRecipe>> craftingType = RecipeType.createFromVanilla(net.minecraft.world.item.crafting.RecipeType.CRAFTING);
            List<RecipeHolder<CraftingRecipe>> bouquetHolders = new ArrayList<>();

            // 通用花束合成（4 朵标签花 → 花束）
            bouquetHolders.add(new RecipeHolder<>(
                    ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, "bouquet"),
                    BouquetRecipe.INSTANCE
            ));

            // 具体花束组合的虚拟合成（4 朵具体花 → 特定花束变体）
            for (BouquetFlowers flowers : uniqueFlowers) {
                List<ItemStack> flowerItems = new ArrayList<>();
                for (ResourceLocation flowerId : flowers.flowers()) {
                    ItemStack flowerStack = new ItemStack(BuiltInRegistries.ITEM.get(flowerId), 1);
                    if (!flowerStack.isEmpty()) {
                        flowerItems.add(flowerStack);
                    }
                }
                while (flowerItems.size() < 4) {
                    flowerItems.add(ItemStack.EMPTY);
                }
                ItemStack bouquet = new ItemStack(Items.BOUQUET.get());
                bouquet.set(DataComponents.BOUQUET_FLOWERS.get(), flowers);
                String id = flowers.flowers().stream()
                        .map(ResourceLocation::getPath)
                        .sorted()
                        .reduce((a, b) -> a + "_" + b)
                        .orElse("bouquet");
                ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, "bouquet_display_" + id);
                bouquetHolders.add(new RecipeHolder<>(recipeId, new VirtualBouquetRecipe(flowerItems, bouquet)));
            }
            registration.addRecipes(craftingType, bouquetHolders);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.EXTRACTION_APPARATUS.get()), EXTRACTION_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Blocks.DISTILLERY_APPARATUS.get()), DISTILLING_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Blocks.FERMENTATION_BARREL.get()), FERMENTATION_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Blocks.INCUBATOR.get()), INCUBATOR_TYPE);
        registration.addRecipeCatalyst(new ItemStack(Items.GRINDER.get()), GRINDER_TYPE);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        // 药品子类型
        registration.registerSubtypeInterpreter(
                Items.MEDICINE.get(),
                new ISubtypeInterpreter<>() {
                    @Override
                    public Object getSubtypeData(ItemStack stack, @Nonnull UidContext context) {
                        MedicineData data = stack.get(DataComponents.MEDICINE_DATA.get());
                        if (data != null) {
                            // 返回唯一标识，例如 "medicine_artemisia"
                            return "medicine_" + data.typeId().getPath();
                        }
                        return "medicine_default";
                    }

                    @Override
                    @Nonnull
                    public String getLegacyStringSubtypeInfo(@Nonnull ItemStack stack, @Nonnull UidContext context) {
                        return (String) getSubtypeData(stack, context);
                    }
                }
        );
        // ---- 草药粉子类型（根据花束组件） ----
        registration.registerSubtypeInterpreter(
                Items.POWDER_HERBAL.get(),
                new ISubtypeInterpreter<>() {
                    @Override
                    public Object getSubtypeData(ItemStack stack, @Nonnull UidContext context) {
                        BouquetFlowers flowers = stack.get(DataComponents.BOUQUET_FLOWERS.get());
                        if (flowers != null && flowers.isValid()) {
                            // 使用排序后的花束 ID 拼接
                            String key = flowers.flowers().stream()
                                    .map(ResourceLocation::getPath)
                                    .sorted()
                                    .reduce((a, b) -> a + "_" + b)
                                    .orElse("default");
                            return "powder_" + key;
                        }
                        return "powder_default";
                    }

                    @Override
                    @Nonnull
                    public String getLegacyStringSubtypeInfo(@Nonnull ItemStack stack, @Nonnull UidContext context) {
                        return (String) getSubtypeData(stack, context);
                    }
                }
        );
        // 花束子类型
        registration.registerSubtypeInterpreter(
                Items.BOUQUET.get(),
                new ISubtypeInterpreter<>() {
                    @Override
                    public Object getSubtypeData(ItemStack stack, @Nonnull UidContext context) {
                        BouquetFlowers flowers = stack.get(DataComponents.BOUQUET_FLOWERS.get());
                        if (flowers != null && flowers.isValid()) {
                            String key = flowers.flowers().stream()
                                    .map(ResourceLocation::getPath)
                                    .sorted()
                                    .reduce((a, b) -> a + "_" + b)
                                    .orElse("default");
                            return "bouquet_" + key;
                        }
                        return "bouquet_default";
                    }

                    @Override
                    @Nonnull
                    public String getLegacyStringSubtypeInfo(@Nonnull ItemStack stack, @Nonnull UidContext context) {
                        return (String) getSubtypeData(stack, context);
                    }
                }
        );
    }
}