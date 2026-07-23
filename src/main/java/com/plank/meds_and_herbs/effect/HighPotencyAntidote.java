package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.data.MedsType;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HighPotencyAntidote extends MedicalEffect {
    public HighPotencyAntidote() {
        super(MobEffectCategory.BENEFICIAL, 0xffffff);
    }
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;
        EffectCures.cure(entity, MedsType.HIGH_POTENCY_ANTIDOTE);
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
