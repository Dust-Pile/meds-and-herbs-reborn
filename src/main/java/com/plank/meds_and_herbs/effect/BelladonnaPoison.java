package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.DamageTypes;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BelladonnaPoison extends MedicalEffect {

    public BelladonnaPoison() {
        super(MobEffectCategory.HARMFUL, 0xCC00FF);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;
        if (entity.isAlive()) {
            entity.hurt(entity.damageSources().source(DamageTypes.BELLADONNA_POISON), amplifier * 0.5f);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 10 == 0;
    }
}
