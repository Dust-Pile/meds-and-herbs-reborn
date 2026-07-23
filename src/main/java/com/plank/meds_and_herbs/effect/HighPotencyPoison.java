package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.DamageTypes;
import com.plank.meds_and_herbs.procedures.Kill;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HighPotencyPoison extends MedicalEffect {
    public HighPotencyPoison() {
        super(MobEffectCategory.HARMFUL, 0x000000);
    }
    @Override
    public void onEffectRemoved(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        Kill.kill(entity, DamageTypes.HIGH_POTENCY_POISON);
    }
}
