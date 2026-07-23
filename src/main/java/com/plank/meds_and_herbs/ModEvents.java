package com.plank.meds_and_herbs;

import com.plank.meds_and_herbs.block.entity.DistilleryApparatusBlockEntity;
import com.plank.meds_and_herbs.block.entity.ExtractionApparatusBlockEntity;
import com.plank.meds_and_herbs.command.HealCommand;
import com.plank.meds_and_herbs.data.MedicineTypeLoader;
import com.plank.meds_and_herbs.init.BlockEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = MedsAndHerbs.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(MedicineTypeLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BlockEntities.EXTRACTION_APPARATUS.get(),
                ExtractionApparatusBlockEntity::getHandlerForSide
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BlockEntities.DISTILLERY_APPARATUS.get(),
                DistilleryApparatusBlockEntity::getHandlerForSide
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BlockEntities.INCUBATOR.get(),
                (be, side) -> be.getItemHandler()
        );
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                BlockEntities.FERMENTATION_BARREL.get(),
                (be, side) -> be.getItemHandler()
        );
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        HealCommand.register(event.getDispatcher());
    }
}
