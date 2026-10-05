package com.juiceybeans.meds_and_herbs.jei;

import com.juiceybeans.meds_and_herbs.MedsAndHerbs;
import com.juiceybeans.meds_and_herbs.init.MHBlocks;
import com.juiceybeans.meds_and_herbs.init.MHItems;
import com.juiceybeans.meds_and_herbs.init.MHRecipes;
import com.juiceybeans.meds_and_herbs.item.Bouquet;
import com.juiceybeans.meds_and_herbs.item.Medicine;
import com.juiceybeans.meds_and_herbs.recipe.*;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;

import javax.annotation.Nonnull;
import java.util.List;

@JeiPlugin
public class MedsAndHerbsJEIPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_UID =
            MedsAndHerbs.id("jei_plugin");

    public static final RecipeType<ExtractionRecipe> EXTRACTION_TYPE =
            RecipeType.create(MedsAndHerbs.MODID, "extraction", ExtractionRecipe.class);

    public static final RecipeType<DistillingRecipe> DISTILLING_TYPE =
            RecipeType.create(MedsAndHerbs.MODID, "distilling", DistillingRecipe.class);

    public static final RecipeType<FermentationRecipe> FERMENTATION_TYPE =
            RecipeType.create(MedsAndHerbs.MODID, "fermentation", FermentationRecipe.class);

    public static final RecipeType<IncubatorRecipe> INCUBATOR_TYPE =
            RecipeType.create(MedsAndHerbs.MODID, "incubator", IncubatorRecipe.class);

    public static final RecipeType<GrinderRecipe> GRINDER_TYPE =
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
            List<ExtractionRecipe> extractionRecipes = recipeManager
                    .getAllRecipesFor(MHRecipes.EXTRACTION_TYPE.get());

//            Set<BouquetFlowers> uniqueFlowers = new HashSet<>();
//            for (ExtractionRecipe recipe : extractionRecipes) {
//                ItemStack powderStack = JeiHelper.createItemStackWithComponents(recipe.powder().ingredient(), 1);
//                BouquetFlowers flowers = powderStack.get(MHDataComponents.BOUQUET_FLOWERS.get());
//                if (flowers != null && flowers.isValid()) {
//                    uniqueFlowers.add(flowers);
//                }
//            }

            registration.addRecipes(EXTRACTION_TYPE, extractionRecipes);

            List<DistillingRecipe> distillingRecipes = recipeManager
                    .getAllRecipesFor(MHRecipes.DISTILLING_TYPE.get());
            registration.addRecipes(DISTILLING_TYPE, distillingRecipes);

            List<FermentationRecipe> fermentationRecipes = recipeManager
                    .getAllRecipesFor(MHRecipes.FERMENTATION_TYPE.get());
            registration.addRecipes(FERMENTATION_TYPE, fermentationRecipes);

            List<IncubatorRecipe> incubatorRecipes = recipeManager
                    .getAllRecipesFor(MHRecipes.INCUBATOR_TYPE.get());
            registration.addRecipes(INCUBATOR_TYPE, incubatorRecipes);

            List<GrinderRecipe> grinderRecipes = recipeManager
                    .getAllRecipesFor(MHRecipes.GRINDER_TYPE.get());
//            grinderRecipes.add(BouquetGrinderRecipe.INSTANCE);
//
//            for (BouquetFlowers flowers : uniqueFlowers) {
//                ItemStack bouquet = new ItemStack(MHItems.BOUQUET.get());
//                bouquet.set(MHDataComponents.BOUQUET_FLOWERS.get(), flowers);
//                ItemStack powder = new ItemStack(MHItems.POWDER_HERBAL.get());
//                powder.set(MHDataComponents.BOUQUET_FLOWERS.get(), flowers);
//                grinderRecipes.add(new VirtualGrinderRecipe(bouquet, powder));
//            }
            registration.addRecipes(GRINDER_TYPE, grinderRecipes);

//            RecipeType<RecipeHolder<CraftingRecipe>> craftingType = RecipeType.createFromVanilla(net.minecraft.world.item.crafting.RecipeType.CRAFTING);
//            List<RecipeHolder<CraftingRecipe>> bouquetHolders = new ArrayList<>();
//
//            bouquetHolders.add(new RecipeHolder<>(
//                    MedsAndHerbs.id("bouquet"),
//                    BouquetRecipe.INSTANCE
//            ));
//
//            for (BouquetFlowers flowers : uniqueFlowers) {
//                List<ItemStack> flowerItems = new ArrayList<>();
//                for (ResourceLocation flowerId : flowers.flowers()) {
//                    ItemStack flowerStack = new ItemStack(BuiltInRegistries.ITEM.get(flowerId), 1);
//                    if (!flowerStack.isEmpty()) {
//                        flowerItems.add(flowerStack);
//                    }
//                }
//                while (flowerItems.size() < 4) {
//                    flowerItems.add(ItemStack.EMPTY);
//                }
//                ItemStack bouquet = new ItemStack(MHItems.BOUQUET.get());
//                bouquet.set(MHDataComponents.BOUQUET_FLOWERS.get(), flowers);
//                String id = flowers.flowers().stream()
//                        .map(ResourceLocation::getPath)
//                        .sorted()
//                        .reduce((a, b) -> a + "_" + b)
//                        .orElse("bouquet");
//                ResourceLocation recipeId = MedsAndHerbs.id("bouquet_display_" + id);
//                bouquetHolders.add(new RecipeHolder<>(recipeId, new VirtualBouquetRecipe(flowerItems, bouquet)));
//            }
//            registration.addRecipes(craftingType, bouquetHolders);
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(MHBlocks.EXTRACTION_APPARATUS.get()), EXTRACTION_TYPE);
        registration.addRecipeCatalyst(new ItemStack(MHBlocks.DISTILLERY_APPARATUS.get()), DISTILLING_TYPE);
        registration.addRecipeCatalyst(new ItemStack(MHBlocks.FERMENTATION_BARREL.get()), FERMENTATION_TYPE);
        registration.addRecipeCatalyst(new ItemStack(MHBlocks.INCUBATOR.get()), INCUBATOR_TYPE);
        registration.addRecipeCatalyst(new ItemStack(MHItems.GRINDER.get()), GRINDER_TYPE);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(
                MHItems.MEDICINE.get(),
                new ISubtypeInterpreter<>() {
                    @Override
                    public Object getSubtypeData(ItemStack stack, @Nonnull UidContext context) {
                        return "medicine_" + Medicine.getType(stack).getPath();
                    }

                    @Override
                    @Nonnull
                    public String getLegacyStringSubtypeInfo(@Nonnull ItemStack stack, @Nonnull UidContext context) {
                        return "medicine_" + Medicine.getType(stack).getPath();
                    }
                }
        );

        registration.registerSubtypeInterpreter(
                MHItems.POWDER_HERBAL.get(),
                new ISubtypeInterpreter<>() {
                    @Override
                    public Object getSubtypeData(ItemStack stack, @Nonnull UidContext context) {
                        return "powder_" + flowerKey(stack);
                    }

                    @Override
                    @Nonnull
                    public String getLegacyStringSubtypeInfo(@Nonnull ItemStack stack, @Nonnull UidContext context) {
                        return "powder_" + flowerKey(stack);
                    }
                }
        );

        registration.registerSubtypeInterpreter(
                MHItems.BOUQUET.get(),
                new ISubtypeInterpreter<>() {
                    @Override
                    public Object getSubtypeData(ItemStack stack, @Nonnull UidContext context) {
                        return "bouquet_" + flowerKey(stack);
                    }

                    @Override
                    @Nonnull
                    public String getLegacyStringSubtypeInfo(@Nonnull ItemStack stack, @Nonnull UidContext context) {
                        return "bouquet_" + flowerKey(stack);
                    }
                }
        );
    }

    private static String flowerKey(ItemStack stack) {
        List<ResourceLocation> flowers = Bouquet.getFlowers(stack);
        if (flowers.isEmpty()) return "default";
        return flowers.stream()
                .map(ResourceLocation::getPath)
                .sorted()
                .reduce((a, b) -> a + "_" + b)
                .orElse("default");
    }
}