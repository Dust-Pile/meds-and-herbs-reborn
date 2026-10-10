package com.dusty_dusty.meds_and_herbs.init;

import com.dusty_dusty.meds_and_herbs.MedsAndHerbs;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MHSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY =
            DeferredRegister.create(Registries.SOUND_EVENT, MedsAndHerbs.MODID);

    public static final RegistryObject<SoundEvent> GRINDER = register("block.grinder");
    public static final RegistryObject<SoundEvent> EXTRACT_APPARATUS = register("block.extract_apparatus");
    public static final RegistryObject<SoundEvent> DISTILLERY_APPARATUS = register("block.distillery_apparatus");
    public static final RegistryObject<SoundEvent> FERMENTATION_BARREL = register("block.fermentation_barrel");
    public static final RegistryObject<SoundEvent> HERBALIST_WORKING = register("block.herbalist_working");

    public static final RegistryObject<SoundEvent> BANDAGE = register("item.bandage");
    public static final RegistryObject<SoundEvent> INJECT = register("item.inject");
    public static final RegistryObject<SoundEvent> SEW = register("item.sew");

    private static RegistryObject<SoundEvent> register(String name) {
        return REGISTRY.register(name, () -> SoundEvent.createVariableRangeEvent(
                MedsAndHerbs.id(name)));
    }
}
