package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.effect.EffectCures;
import com.plank.meds_and_herbs.init.Effects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class CottonDressing extends Dressing {
    @Override
    protected boolean healBleeding(LivingEntity target) {
        MobEffectInstance inst = target.getEffect(Effects.BLEEDING);
        if (inst == null) return false;
        else {
            EffectCures.cure(target, EffectCures.DRESSING);
            return true;
        }
    }
}
