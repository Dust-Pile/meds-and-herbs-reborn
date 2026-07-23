package com.plank.meds_and_herbs.recipe;

import com.plank.meds_and_herbs.MedsAndHerbs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = MedsAndHerbs.MODID)
public class RecipeRegistration {

    @SubscribeEvent
    public static void onServerAboutToStart(ServerAboutToStartEvent event) {
        MinecraftServer server = event.getServer();
        RecipeManager recipeManager = server.getRecipeManager();

        // 获取所有现有配方
        List<RecipeHolder<?>> allRecipes = new ArrayList<>(recipeManager.getRecipes());

        // ----- 注册花束配方 -----
        ResourceLocation bouquetId = ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, "bouquet");
        RecipeHolder<BouquetRecipe> bouquetHolder = new RecipeHolder<>(bouquetId, BouquetRecipe.INSTANCE);
        addIfAbsent(allRecipes, bouquetHolder);

        // ----- 注册研磨配方（花束→草药粉） -----
        ResourceLocation grinderId = ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, "bouquet_grinder");
        RecipeHolder<BouquetGrinderRecipe> grinderHolder = new RecipeHolder<>(grinderId, BouquetGrinderRecipe.INSTANCE);
        addIfAbsent(allRecipes, grinderHolder);

        // 替换配方表
        recipeManager.replaceRecipes(allRecipes);
    }

    // 辅助方法：如果配方ID不存在则添加
    private static void addIfAbsent(List<RecipeHolder<?>> recipes, RecipeHolder<?> newHolder) {
        boolean exists = recipes.stream().anyMatch(h -> h.id().equals(newHolder.id()));
        if (!exists) {
            recipes.add(newHolder);
        }
    }
}