package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.Effects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class Laceration extends MedicalEffect {
    public Laceration() {
        super(MobEffectCategory.HARMFUL, 0xCC0000, EffectCures.SEWING);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;

        // 如果有免疫或细菌感染，则不进行额外感染
        if (entity.hasEffect(Effects.IMMUNE) || entity.hasEffect(Effects.BACTERIAL_INFECTION)) {
            return true;
        }

        MobEffectInstance instance = entity.getEffect(Effects.LACERATION);
        if (instance != null && entity.getRandom().nextFloat() < 0.001) {
            entity.addEffect(new MobEffectInstance(Effects.BACTERIAL_INFECTION, 24000, 0));
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
