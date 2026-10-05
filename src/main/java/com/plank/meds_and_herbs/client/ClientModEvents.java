package com.plank.meds_and_herbs.client;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.data.MedicineDefinition;
import com.plank.meds_and_herbs.data.MedicineTypeLoader;
import com.plank.meds_and_herbs.init.MHItems;
import com.plank.meds_and_herbs.item.Medicine;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = MedsAndHerbs.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex == 0) return 0xFFFFFFFF;
            else if (tintIndex == 1) {
                ResourceLocation typeId = Medicine.getType(stack);
                MedicineDefinition def = MedicineTypeLoader.get(typeId);
                return def != null ? def.color() : 0xFFFFFFFF;
            }
            return 0xFFFFFFFF;
        }, MHItems.MEDICINE.get());
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(MHItems.MEDICINE.get(),
                    ResourceLocation.parse("meds_and_herbs:uses"),
                    (stack, level, entity, seed) -> Medicine.getUses(stack)
            );
        });
    }
}