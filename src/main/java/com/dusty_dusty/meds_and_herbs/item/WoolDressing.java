package com.dusty_dusty.meds_and_herbs.item;

import com.dusty_dusty.meds_and_herbs.effect.EffectCures;
import com.dusty_dusty.meds_and_herbs.init.MHEffects;
import com.dusty_dusty.meds_and_herbs.init.MHItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class WoolDressing extends Dressing {
    @Override
    protected boolean healBleeding(LivingEntity target) {
        MobEffectInstance inst = target.getEffect(MHEffects.BLEEDING.get());
        if (inst == null) return false;
        else {
            int duration = inst.getDuration();
            if (duration >= 600) duration = duration / 2;
            else duration = 0;
            EffectCures.cure(target, new ItemStack(MHItems.WOOL_DRESSING.get()));
            target.addEffect(new MobEffectInstance(MHEffects.BLEEDING.get() , duration, 0));
            return true;
        }
    }
}
