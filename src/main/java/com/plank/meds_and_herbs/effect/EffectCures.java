package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.data.MedicineTypeLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;

public class EffectCures {
    public static final EffectCure ADDICTION =  EffectCure.get("addiction");
    public static final EffectCure DRESSING = EffectCure.get("dressing");
    public static final EffectCure SEWING = EffectCure.get("sewing");
    public static final EffectCure SPLINT = EffectCure.get("splint");
    public static void cure(LivingEntity entity, ResourceLocation id) {
        for (MobEffectInstance effectInstance : entity.getActiveEffects()) {
            ResourceLocation effectId = BuiltInRegistries.MOB_EFFECT.getKey(effectInstance.getEffect().value());
            if (MedicineTypeLoader.get(id).cures().contains(effectId)) effectInstance.getCures().add(EffectCure.get(id.toString()));
        }
        cure(entity, EffectCure.get(id.toString()));
    }
    public static void cure(LivingEntity entity, EffectCure cure) {
        entity.getPersistentData().putBoolean("meds_and_herbs:curing", true);
        try {
            entity.removeEffectsCuredBy(cure);
        } finally {
            entity.getPersistentData().remove("meds_and_herbs:curing");
        }
    }
}
