package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.DamageTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class Thrombosis extends MedicalEffect {
    public Thrombosis() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;

        // 每 tick 0.5% 概率触发四种效果之一（独立判断，可能同时触发多个）
        if (entity.getRandom().nextFloat() < 0.1f) {
            int choice = entity.getRandom().nextInt(4);
            Holder<MobEffect> effect = switch (choice) {
                case 0 -> MobEffects.DIG_SLOWDOWN;
                case 1 -> MobEffects.WEAKNESS;
                case 2 -> MobEffects.MOVEMENT_SLOWDOWN;
                default -> null; // 造成伤害
            };

            if (effect != null) {
                entity.addEffect(new MobEffectInstance(effect, 100, 1));
            } else {
                entity.hurt(entity.damageSources().source(DamageTypes.THROMBOSIS), 1.0f);
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
