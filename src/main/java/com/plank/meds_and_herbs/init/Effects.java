package com.plank.meds_and_herbs.init;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.effect.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Effects {
    // 创建DeferredRegister
    public static final DeferredRegister<MobEffect> REGISTRY =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, MedsAndHerbs.MODID);

    // === 植物/食物效果 ===
    public static final Holder<MobEffect> BELLADONNA_BERRY = register("belladonna_poison", BelladonnaPoison::new);
    public static final Holder<MobEffect> BEVERAGE_DRINK = register("beverage_drink", BeverageDrink::new);

    // === 感染/疾病类 ===
    public static final Holder<MobEffect> PARASITES = register("parasites", Parasites::new);
    public static final Holder<MobEffect> BACTERIAL_INFECTION = register("bacterial_infection", BacterialInfection::new);
    public static final Holder<MobEffect> MUSHROOM_POISONING = register("mushroom_poisoning", MushroomPoisoning::new);
    public static final Holder<MobEffect> METHANOL_POISONING = register("methanol_poisoning", MethanolPoisoning::new);

    // === 出血与损伤 ===
    public static final Holder<MobEffect> BLEEDING = register("bleeding", Bleeding::new);
    public static final Holder<MobEffect> INTERNAL_BLEEDING = register("internal_bleeding", InternalBleeding::new);
    public static final Holder<MobEffect> BLOOD_LOSS = register("blood_loss", BloodLoss::new);
    public static final Holder<MobEffect> BURNS = register("burns", Burns::new);
    public static final Holder<MobEffect> LACERATION = register("laceration", Laceration::new);
    public static final Holder<MobEffect> BONE_FRACTURE = register("bone_fracture", BoneFracture::new);
    public static final Holder<MobEffect> BONE_PATCHED = register("bone_patched", BonePatched::new);

    // === 循环系统/血液 ===
    public static final Holder<MobEffect> THROMBOSIS = register("thrombosis", Thrombosis::new);
    public static final Holder<MobEffect> HIGH_POTENCY_ANTIDOTE = register("high_potency_antidote", HighPotencyAntidote::new);
    public static final Holder<MobEffect> HIGH_POTENCY_POISON = register("high_potency_poison", HighPotencyPoison::new);

    // === 药物效果 ===
    public static final Holder<MobEffect> ADRENALINE = register("adrenaline", Adrenaline::new);
    public static final Holder<MobEffect> PAINKILLER = register("painkiller", Painkiller::new);
    public static final Holder<MobEffect> IMMUNE = register("immune", Immune::new);

    // === 成瘾/戒断 ===
    public static final Holder<MobEffect> OPIUM_ADDICTION = register("opium_addiction", OpiumAddiction::new);
    public static final Holder<MobEffect> OPIUM_WITHDRAWAL = register("opium_withdrawal", OpiumWithdrawal::new);

    // 辅助注册方法，使用DeferredRegister
    private static Holder<MobEffect> register(String name, Supplier<? extends MobEffect> supplier) {
        return REGISTRY.register(name, supplier);
    }
}