package com.juiceybeans.meds_and_herbs.effect;

import com.juiceybeans.meds_and_herbs.util.MHUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HighPotencyAntidote extends MobEffect {
    public HighPotencyAntidote() {
        super(MobEffectCategory.BENEFICIAL, 0xffffff);
    }
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        EffectCures.cure(entity, MHUtils.getMedicineStackFromType("high_potency_antidote")); //todo high potency antidote
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
