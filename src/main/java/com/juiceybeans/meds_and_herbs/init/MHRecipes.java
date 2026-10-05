package com.juiceybeans.meds_and_herbs.init;

import com.juiceybeans.meds_and_herbs.MedsAndHerbs;
import com.juiceybeans.meds_and_herbs.recipe.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MHRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MedsAndHerbs.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, MedsAndHerbs.MODID);

    public static final Supplier<RecipeType<DistillingRecipe>> DISTILLING_TYPE =
            TYPES.register("distilling", () -> RecipeType.simple(MedsAndHerbs.id("distilling")));

    public static final Supplier<RecipeSerializer<DistillingRecipe>> DISTILLING_SERIALIZER =
            SERIALIZERS.register("distilling", () -> DistillingRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeType<ExtractionRecipe>> EXTRACTION_TYPE =
            TYPES.register("extraction", () -> RecipeType.simple(MedsAndHerbs.id("extraction")));

    public static final Supplier<RecipeSerializer<ExtractionRecipe>> EXTRACTION_SERIALIZER =
            SERIALIZERS.register("extraction", () -> ExtractionRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeType<FermentationRecipe>> FERMENTATION_TYPE =
            TYPES.register("fermentation", () -> RecipeType.simple(MedsAndHerbs.id("fermentation")));

    public static final Supplier<RecipeSerializer<FermentationRecipe>> FERMENTATION_SERIALIZER =
            SERIALIZERS.register("fermentation", () -> FermentationRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeType<GrinderRecipe>> GRINDER_TYPE =
            TYPES.register("grinder", () -> RecipeType.simple(MedsAndHerbs.id("grinder")));

    public static final Supplier<RecipeSerializer<GrinderRecipe>> GRINDER_SERIALIZER =
            SERIALIZERS.register("grinder", () -> GrinderRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeType<IncubatorRecipe>> INCUBATOR_TYPE =
            TYPES.register("incubator", () -> RecipeType.simple(MedsAndHerbs.id("incubator")));

    public static final Supplier<RecipeSerializer<IncubatorRecipe>> INCUBATOR_SERIALIZER =
            SERIALIZERS.register("incubator", () -> IncubatorRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeSerializer<BouquetRecipe>> BOUQUET_SERIALIZER =
            SERIALIZERS.register("bouquet", () -> BouquetRecipe.Serializer.INSTANCE);

    public static final Supplier<RecipeSerializer<BouquetGrinderRecipe>> BOUQUET_GRINDER_SERIALIZER =
            SERIALIZERS.register("bouquet_grinder", () -> BouquetGrinderRecipe.Serializer.INSTANCE);
}