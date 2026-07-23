package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.DamageTypes;
import com.plank.meds_and_herbs.init.Effects;
import com.plank.meds_and_herbs.procedures.Kill;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class Parasites extends MedicalEffect {
    public Parasites() {
        super(MobEffectCategory.HARMFUL, 0xA58A7D);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;

        if (entity.getRandom().nextFloat() < 0.1f) {
            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 0, false, false));
        }
        if (entity.getRandom().nextFloat() < 0.1f) {
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false));
        }

        // 获取当前效果实例以检查剩余时间
        MobEffectInstance instance = entity.getEffect(Effects.PARASITES);
        if (instance != null && instance.getDuration() < 200) {
            entity.hurt(entity.damageSources().source(DamageTypes.PARASITES), 4.0f);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 10 == 0;
    }

    @Override
    public void onEffectRemoved(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        Kill.kill(entity, DamageTypes.PARASITES);
    }
}
