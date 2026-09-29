package com.plank.meds_and_herbs;

import com.mojang.logging.LogUtils;
import com.plank.meds_and_herbs.client.gui.screen.DistilleryApparatusGUIScreen;
import com.plank.meds_and_herbs.client.gui.screen.ExtractApparatusGUIScreen;
import com.plank.meds_and_herbs.client.gui.screen.IncubatorGUIScreen;
import com.plank.meds_and_herbs.init.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod("meds_and_herbs")
public class MedsAndHerbs {
    public static final String MODID = "meds_and_herbs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public MedsAndHerbs(IEventBus bus) {
        Sounds.REGISTRY.register(bus);
//        DataComponents.REGISTRY.register(bus);
        Blocks.REGISTRY.register(bus);
        BlockEntities.REGISTRY.register(bus);
        Recipes.SERIALIZERS.register(bus);
        Recipes.TYPES.register(bus);
        Items.REGISTRY.register(bus);
        Items.ITEMS.register(bus);
        Effects.REGISTRY.register(bus);
        VillagerProfessions.POI_TYPES.register(bus);
        VillagerProfessions.PROFESSIONS.register(bus);
        Menus.REGISTRY.register(bus);
        CreativeTabs.REGISTRY.register(bus);
        bus.register(this);
    }

    @SubscribeEvent
    private void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(
                () -> {
                    MenuScreens.register(Menus.DISTILLERY_APPARATUS_GUI.get(), DistilleryApparatusGUIScreen::new);
                    MenuScreens.register(Menus.EXTRACTION_APPARATUS_GUI.get(), ExtractApparatusGUIScreen::new);
                    MenuScreens.register(Menus.INCUBATOR_GUI.get(), IncubatorGUIScreen::new);
                }
        );
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            FlowerPotBlock flowerPot = (FlowerPotBlock) net.minecraft.world.level.block.Blocks.FLOWER_POT;
            flowerPot.addPlant(Blocks.VINCA.getId(), Blocks.POTTED_VINCA);
            flowerPot.addPlant(Blocks.BELLADONNA.getId(), Blocks.POTTED_BELLADONNA);
            flowerPot.addPlant(Blocks.CHAMOMILE.getId(), Blocks.POTTED_CHAMOMILE);
            flowerPot.addPlant(Blocks.ARTEMISIA.getId(), Blocks.POTTED_ARTEMISIA);
            flowerPot.addPlant(Blocks.OPIUM.getId(), Blocks.POTTED_OPIUM);
        });
    }
}