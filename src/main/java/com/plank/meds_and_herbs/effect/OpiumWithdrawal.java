package com.plank.meds_and_herbs.effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class OpiumWithdrawal extends MedicalEffect {
    public OpiumWithdrawal() {
        super(MobEffectCategory.HARMFUL, 0x8B4513, EffectCures.ADDICTION);
    }
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;
        // 每 tick 0.1% 概率随机触发一种负面效果
        if (entity.getRandom().nextFloat() < 0.001) {
            int choice = entity.getRandom().nextInt(4);
            Holder<MobEffect> effect = switch (choice) {
                case 0 -> MobEffects.CONFUSION;
                case 1 -> MobEffects.WEAKNESS;
                case 2 -> MobEffects.MOVEMENT_SLOWDOWN;
                case 3 -> MobEffects.DIG_SLOWDOWN;
                default -> null;
            };
            if (effect == null) return false;
            entity.addEffect(new MobEffectInstance(effect, choice == 0 ? 100 : 20, 0, false, false));
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }
}
