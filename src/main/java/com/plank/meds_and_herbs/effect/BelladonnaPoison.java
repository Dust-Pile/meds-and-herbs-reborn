package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.MHDamageTypes;
import com.plank.meds_and_herbs.util.MHUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BelladonnaPoison extends MobEffect {

    public BelladonnaPoison() {
        super(MobEffectCategory.HARMFUL, 0xCC00FF);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        if (!entity.isAlive()) return;

        MHUtils.hurtWithCustomType(entity, MHDamageTypes.BELLADONNA_POISON, amplifier * 0.5f);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 10 == 0;
    }
}