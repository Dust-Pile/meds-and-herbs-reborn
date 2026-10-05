package com.juiceybeans.meds_and_herbs.effect;

import com.juiceybeans.meds_and_herbs.init.MHEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class Burns extends MobEffect {

    public Burns() {
        super(MobEffectCategory.HARMFUL, 0xFF4500);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        if (entity.hasEffect(MobEffects.FIRE_RESISTANCE)) return;

        if (entity.isOnFire()) {
            var data = entity.getPersistentData();
            String key = "burns_counter";
            int counter = data.getInt(key) + 1;

            if (counter >= 20) {
                float damage = (amplifier == 0) ? 1.0f : 2.0f;
                entity.hurt(entity.damageSources().onFire(), damage);
                data.putInt(key, 0);
            } else {
                data.putInt(key, counter);
            }
        }

        if (amplifier >= 1) {
            MobEffectInstance inst = entity.getEffect(MHEffects.BURNS.get());
            if (inst != null && inst.getDuration() == 8000) {
                entity.addEffect(new MobEffectInstance(MHEffects.THROMBOSIS.get(), 24000, 0));
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}