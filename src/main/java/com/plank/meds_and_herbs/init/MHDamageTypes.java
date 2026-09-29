package com.plank.meds_and_herbs.init;

import com.plank.meds_and_herbs.MedsAndHerbs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class MHDamageTypes {
    public static final ResourceKey<DamageType> ADRENALINE = createKey("adrenaline");
    public static final ResourceKey<DamageType> BLEEDING = createKey("bleeding");
    public static final ResourceKey<DamageType> PARASITES = createKey("parasites");
    public static final ResourceKey<DamageType> FRACTURE = createKey("fracture");
    public static final ResourceKey<DamageType> BELLADONNA_POISON = createKey("belladonna_poison");
    public static final ResourceKey<DamageType> BACTERIAL_INFECTION = createKey("bacterial_infection");
    public static final ResourceKey<DamageType> HIGH_POTENCY_POISON = createKey("high_potency_poison");
    public static final ResourceKey<DamageType> METHANOL = createKey("methanol");
    public static final ResourceKey<DamageType> MUSHROOM = createKey("mushroom");
    public static final ResourceKey<DamageType> THROMBOSIS = createKey("thrombosis");

    private static ResourceKey<DamageType> createKey(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE,
                ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, name));
    }
}