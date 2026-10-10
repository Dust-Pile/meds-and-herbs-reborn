package com.dusty_dusty.meds_and_herbs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class Painkiller extends MobEffect {
    public Painkiller() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFFFF);
    } // todo cured by addiction cures

    public static void onEffectAdded(LivingEntity entity) {
        var data = entity.getPersistentData();
        data.putDouble("PainkillerDamageTaken", 0.0);
    }

    public static void onEffectExpired(LivingEntity entity) {
        var data = entity.getPersistentData();
        double taken = data.getDouble("PainkillerDamageTaken");
        entity.hurt(entity.level().damageSources().generic(), (float) taken);
        data.remove("PainkillerDamageTaken");
    }
}