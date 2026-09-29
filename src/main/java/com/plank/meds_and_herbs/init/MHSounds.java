package com.plank.meds_and_herbs.init;

import com.plank.meds_and_herbs.MedsAndHerbs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MHSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY =
            DeferredRegister.create(Registries.SOUND_EVENT, MedsAndHerbs.MODID);

    public static final RegistryObject<SoundEvent> GRINDER = register("grinder");
    public static final RegistryObject<SoundEvent> EXTRACT_APPARATUS = register("extract_apparatus");
    public static final RegistryObject<SoundEvent> DISTILLERY_APPARATUS = register("distillery_apparatus");
    public static final RegistryObject<SoundEvent> FERMENTATION_BARREL = register("fermentation_barrel");
    public static final RegistryObject<SoundEvent> VILLAGER_WORK_HERBALIST = register("villager_work_herbalist");

    private static RegistryObject<SoundEvent> register(String name) {
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, name)));
    }
}
