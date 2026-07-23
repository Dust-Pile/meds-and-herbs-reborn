package com.plank.meds_and_herbs;

import com.mojang.logging.LogUtils;
import com.plank.meds_and_herbs.client.gui.screen.DistilleryApparatusGUIScreen;
import com.plank.meds_and_herbs.client.gui.screen.ExtractApparatusGUIScreen;
import com.plank.meds_and_herbs.client.gui.screen.IncubatorGUIScreen;
import com.plank.meds_and_herbs.init.*;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;

@Mod("meds_and_herbs")
public class MedsAndHerbs {
    public static final String MODID = "meds_and_herbs";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MedsAndHerbs(IEventBus bus) {
        // 注册所有 DeferredRegister 到 Mod 事件总线
        Sounds.REGISTRY.register(bus);
        DataComponents.REGISTRY.register(bus);
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
        // 注册实例到 Mod 事件总线
        bus.register(this);
    }

    @SubscribeEvent
    public void registerScreens(RegisterMenuScreensEvent event) {
        event.register(Menus.DISTILLERY_APPARATUS_GUI.get(), DistilleryApparatusGUIScreen::new);
        event.register(Menus.EXTRACTION_APPARATUS_GUI.get(), ExtractApparatusGUIScreen::new);
        event.register(Menus.INCUBATOR_GUI.get(), IncubatorGUIScreen::new);
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 获取原版花盆方块实例
            FlowerPotBlock flowerPot = (FlowerPotBlock) net.minecraft.world.level.block.Blocks.FLOWER_POT;
            // 注册你的花朵，使其可被种植
            flowerPot.addPlant(Blocks.VINCA.getId(), Blocks.POTTED_VINCA);
            flowerPot.addPlant(Blocks.BELLADONNA.getId(), Blocks.POTTED_BELLADONNA);
            flowerPot.addPlant(Blocks.CHAMOMILE.getId(), Blocks.POTTED_CHAMOMILE);
            flowerPot.addPlant(Blocks.ARTEMISIA.getId(), Blocks.POTTED_ARTEMISIA);
            flowerPot.addPlant(Blocks.OPIUM.getId(), Blocks.POTTED_OPIUM);
        });
    }
}