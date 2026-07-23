package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.Effects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class OpiumAddiction extends MedicalEffect {
    public OpiumAddiction() {
        super(MobEffectCategory.HARMFUL, 0x8B4513);
    }
    @Override
    public void onEffectRemoved(LivingEntity entity, int amplifier){
        if (entity.level().isClientSide) return;
        entity.addEffect(new MobEffectInstance(Effects.OPIUM_WITHDRAWAL, 12000, 0));
    }
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;
        if (entity.hasEffect(Effects.PAINKILLER)) {
            MobEffectInstance effectInstance = entity.getEffect(Effects.PAINKILLER);
            if (effectInstance != null && effectInstance.getEffect().value() instanceof MedicalEffect medicalEffect) {
                medicalEffect.onEffectRemoved(entity, effectInstance.getAmplifier());
            }
            EffectCures.cure(entity, EffectCures.ADDICTION);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
