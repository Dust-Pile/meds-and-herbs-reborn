package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.Effects;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class Burns extends MedicalEffect {

    public Burns() {
        super(MobEffectCategory.HARMFUL, 0xFF4500);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;

        // 有防火效果时不造成伤害
        if (entity.hasEffect(MobEffects.FIRE_RESISTANCE)) return true;

        // 只在实体着火时累积计数器
        if (entity.isOnFire()) {
            var data = entity.getPersistentData();
            String key = "burns_counter";
            int counter = data.getInt(key) + 1;

            if (counter >= 20) { // 每秒触发一次
                float damage = (amplifier == 0) ? 1.0f : 2.0f;
                entity.hurt(entity.damageSources().onFire(), damage);
                data.putInt(key, 0);
            } else {
                data.putInt(key, counter);
            }
        }

        // 等级1且剩余时间为8000 tick（400秒）时触发血栓
        if (amplifier >= 1) {
            MobEffectInstance inst = entity.getEffect(Effects.BURNS);
            if (inst != null && inst.getDuration() == 8000) {
                entity.addEffect(new MobEffectInstance(Effects.THROMBOSIS, 24000, 0));
            }
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}