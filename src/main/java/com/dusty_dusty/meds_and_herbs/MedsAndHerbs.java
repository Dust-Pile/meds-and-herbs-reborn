package com.dusty_dusty.meds_and_herbs;

import com.mojang.logging.LogUtils;
import com.dusty_dusty.meds_and_herbs.client.gui.screen.DistilleryApparatusGUIScreen;
import com.dusty_dusty.meds_and_herbs.client.gui.screen.ExtractApparatusGUIScreen;
import com.dusty_dusty.meds_and_herbs.client.gui.screen.IncubatorGUIScreen;
import com.dusty_dusty.meds_and_herbs.init.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod("meds_and_herbs")
public class MedsAndHerbs {
    public static final String MODID = "meds_and_herbs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public MedsAndHerbs() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        MHSounds.REGISTRY.register(bus);
        MHBlocks.REGISTRY.register(bus);
        MHBlockEntities.REGISTRY.register(bus);
        MHRecipes.SERIALIZERS.register(bus);
        MHRecipes.TYPES.register(bus);
        MHItems.REGISTRY.register(bus);
        MHItems.ITEMS.register(bus);
        MHEffects.REGISTRY.register(bus);
        MHVillagerProfessions.POI_TYPES.register(bus);
        MHVillagerProfessions.PROFESSIONS.register(bus);
        MHMenus.REGISTRY.register(bus);
        MHCreativeTabs.REGISTRY.register(bus);
        bus.register(this);
    }

    @SubscribeEvent
    public void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(
                () -> {
                    MenuScreens.register(MHMenus.DISTILLERY_APPARATUS_GUI.get(), DistilleryApparatusGUIScreen::new);
                    MenuScreens.register(MHMenus.EXTRACTION_APPARATUS_GUI.get(), ExtractApparatusGUIScreen::new);
                    MenuScreens.register(MHMenus.INCUBATOR_GUI.get(), IncubatorGUIScreen::new);
                }
        );
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            FlowerPotBlock flowerPot = (FlowerPotBlock) net.minecraft.world.level.block.Blocks.FLOWER_POT;
            flowerPot.addPlant(MHBlocks.VINCA.getId(), MHBlocks.POTTED_VINCA);
            flowerPot.addPlant(MHBlocks.BELLADONNA.getId(), MHBlocks.POTTED_BELLADONNA);
            flowerPot.addPlant(MHBlocks.CHAMOMILE.getId(), MHBlocks.POTTED_CHAMOMILE);
            flowerPot.addPlant(MHBlocks.ARTEMISIA.getId(), MHBlocks.POTTED_ARTEMISIA);
            flowerPot.addPlant(MHBlocks.OPIUM.getId(), MHBlocks.POTTED_OPIUM);
        });
    }
}