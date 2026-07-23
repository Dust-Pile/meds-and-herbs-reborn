package com.plank.meds_and_herbs.init;

import com.plank.meds_and_herbs.MedsAndHerbs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Sounds {
    public static final DeferredRegister<SoundEvent> REGISTRY =
            DeferredRegister.create(Registries.SOUND_EVENT, MedsAndHerbs.MODID);

    // 所有字段现在都是 DeferredHolder，而不是 SoundEvent
    public static final DeferredHolder<SoundEvent, SoundEvent> GRINDER = register("grinder");
    public static final DeferredHolder<SoundEvent, SoundEvent> EXTRACT_APPARATUS = register("extract_apparatus");
    public static final DeferredHolder<SoundEvent, SoundEvent> DISTILLERY_APPARATUS = register("distillery_apparatus");
    public static final DeferredHolder<SoundEvent, SoundEvent> FERMENTATION_BARREL = register("fermentation_barrel");
    public static final DeferredHolder<SoundEvent, SoundEvent> VILLAGER_WORK_HERBALIST = register("villager_work_herbalist");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        // 只注册，不调用 .get()
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, name)));
    }
}
