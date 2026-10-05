package com.juiceybeans.meds_and_herbs.item;

import com.juiceybeans.meds_and_herbs.effect.EffectCures;
import com.juiceybeans.meds_and_herbs.init.MHEffects;
import com.juiceybeans.meds_and_herbs.init.MHItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class PlantagoDressing extends Dressing {
    @Override
    protected boolean healBleeding(LivingEntity target) {
        MobEffectInstance inst = target.getEffect(MHEffects.BLEEDING.get());
        if (inst == null) return false;
        else {
            int duration = inst.getDuration() - 2400;
            EffectCures.cure(target, new ItemStack(MHItems.PLANTAGO_DRESSING.get()));
            if (duration > 0) target.addEffect(new MobEffectInstance(MHEffects.BLEEDING.get(), duration, 0));
            return true;
        }
    }
}
