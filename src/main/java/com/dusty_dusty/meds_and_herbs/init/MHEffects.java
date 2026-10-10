package com.dusty_dusty.meds_and_herbs.init;

import com.dusty_dusty.meds_and_herbs.MedsAndHerbs;
import com.dusty_dusty.meds_and_herbs.effect.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class MHEffects {
    public static final DeferredRegister<MobEffect> REGISTRY =
            DeferredRegister.create(Registries.MOB_EFFECT, MedsAndHerbs.MODID);

    public static final RegistryObject<MobEffect> BELLADONNA_BERRY = register("belladonna_poison", BelladonnaPoison::new);
    public static final RegistryObject<MobEffect> BEVERAGE_DRINK = register("beverage_drink", BeverageDrink::new);

    public static final RegistryObject<MobEffect> PARASITES = register("parasites", Parasites::new);
    public static final RegistryObject<MobEffect> BACTERIAL_INFECTION = register("bacterial_infection", BacterialInfection::new);
    public static final RegistryObject<MobEffect> MUSHROOM_POISONING = register("mushroom_poisoning", MushroomPoisoning::new);
    public static final RegistryObject<MobEffect> METHANOL_POISONING = register("methanol_poisoning", MethanolPoisoning::new);

    public static final RegistryObject<MobEffect> BLEEDING = register("bleeding", Bleeding::new);
    public static final RegistryObject<MobEffect> INTERNAL_BLEEDING = register("internal_bleeding", InternalBleeding::new);
    public static final RegistryObject<MobEffect> BLOOD_LOSS = register("blood_loss", BloodLoss::new);
    public static final RegistryObject<MobEffect> BURNS = register("burns", Burns::new);
    public static final RegistryObject<MobEffect> LACERATION = register("laceration", Laceration::new);
    public static final RegistryObject<MobEffect> BONE_FRACTURE = register("bone_fracture", BoneFracture::new);
    public static final RegistryObject<MobEffect> BONE_PATCHED = register("bone_patched", BonePatched::new);

    public static final RegistryObject<MobEffect> THROMBOSIS = register("thrombosis", Thrombosis::new);
    public static final RegistryObject<MobEffect> HIGH_POTENCY_ANTIDOTE = register("high_potency_antidote", HighPotencyAntidote::new);
    public static final RegistryObject<MobEffect> HIGH_POTENCY_POISON = register("high_potency_poison", HighPotencyPoison::new);

    public static final RegistryObject<MobEffect> ADRENALINE = register("adrenaline", Adrenaline::new);
    public static final RegistryObject<MobEffect> PAINKILLER = register("painkiller", Painkiller::new);
    public static final RegistryObject<MobEffect> IMMUNE = register("immune", Immune::new);

    public static final RegistryObject<MobEffect> OPIUM_ADDICTION = register("opium_addiction", OpiumAddiction::new);
    public static final RegistryObject<MobEffect> OPIUM_WITHDRAWAL = register("opium_withdrawal", OpiumWithdrawal::new);

    private static RegistryObject<MobEffect> register(String name, Supplier<? extends MobEffect> supplier) {
        return REGISTRY.register(name, supplier);
    }
}