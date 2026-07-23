package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.effect.EffectCures;
import com.plank.meds_and_herbs.init.Effects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class PlantagoDressing extends Dressing {
    @Override
    protected boolean healBleeding(LivingEntity target) {
        MobEffectInstance inst = target.getEffect(Effects.BLEEDING);
        if (inst == null) return false;
        else {
            int duration = inst.getDuration() - 2400;
            EffectCures.cure(target, EffectCures.DRESSING);
            if (duration > 0) target.addEffect(new MobEffectInstance(Effects.BLEEDING, duration, 0));
            return true;
        }
    }
}
