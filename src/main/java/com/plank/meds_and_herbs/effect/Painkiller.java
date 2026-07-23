package com.plank.meds_and_herbs.effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class Painkiller extends MedicalEffect {
    public Painkiller() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFFFF, EffectCures.ADDICTION);
    }
    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        // 效果开始时重置计数器
        var data = entity.getPersistentData();
        data.putDouble("PainkillerDamageTaken", 0.0);
    }
    @Override
    public void onEffectRemoved(LivingEntity entity, int amplifier) {
        var data = entity.getPersistentData();
        double taken = data.getDouble("PainkillerDamageTaken");
        entity.setHealth(entity.getHealth() - (float) taken);
        data.remove("PainkillerDamageTaken");
    }
}