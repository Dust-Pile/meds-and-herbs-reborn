package com.plank.meds_and_herbs.client;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.data.MedicineData;
import com.plank.meds_and_herbs.data.MedicineDefinition;
import com.plank.meds_and_herbs.data.MedicineTypeLoader;
import com.plank.meds_and_herbs.init.Items;
import com.plank.meds_and_herbs.item.Medicine;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = MedsAndHerbs.MODID, value = Dist.CLIENT)
public class ClientModEvents {

    // 1. 注册颜色叠加层（原事件）
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex == 0) return 0xFFFFFFFF;
            else if (tintIndex == 1) {
                MedicineData data = Medicine.getMedicineData(stack);
                MedicineDefinition def = MedicineTypeLoader.get(data.typeId());
                return def != null ? def.color() : 0xFFFFFFFF;
            }
            return 0xFFFFFFFF;
        }, Items.MEDICINE.get());
    }

    // 2. 注册模型覆写属性（用于根据使用次数切换模型）
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> { // 务必用 enqueueWork 确保线程安全
            ItemProperties.register(Items.MEDICINE.get(),
                    ResourceLocation.parse("meds_and_herbs:uses"),
                    (stack, level, entity, seed) -> {
                        MedicineData data = Medicine.getMedicineData(stack);
                        // 返回当前剩余使用次数，需与模型 json 里的 predicate 值匹配
                        return data.uses();
                    }
            );
        });
    }
}