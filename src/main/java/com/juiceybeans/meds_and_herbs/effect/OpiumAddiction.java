package com.juiceybeans.meds_and_herbs.effect;

import com.juiceybeans.meds_and_herbs.init.MHEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class OpiumAddiction extends MobEffect {

    public OpiumAddiction() {
        super(MobEffectCategory.HARMFUL, 0x8B4513);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        if (entity.hasEffect(MHEffects.PAINKILLER.get())) {
            entity.removeEffect(MHEffects.PAINKILLER.get());
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    public static void onEffectExpired(LivingEntity entity){
        if (entity.level().isClientSide) return;
        entity.addEffect(new MobEffectInstance(MHEffects.OPIUM_WITHDRAWAL.get(), 12000, 0));
    }
}
