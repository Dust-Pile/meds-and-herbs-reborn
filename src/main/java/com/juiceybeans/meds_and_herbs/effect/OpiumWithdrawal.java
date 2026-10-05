package com.juiceybeans.meds_and_herbs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class OpiumWithdrawal extends MobEffect {
    public OpiumWithdrawal() {
        super(MobEffectCategory.HARMFUL, 0x8B4513); //todo no EffectCures on 1.20.1. addiction had no cures in reborn
    }
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;

        if (entity.getRandom().nextFloat() < 0.001) {
            int choice = entity.getRandom().nextInt(4);
            MobEffect effect = switch (choice) {
                case 0 -> MobEffects.CONFUSION;
                case 1 -> MobEffects.WEAKNESS;
                case 2 -> MobEffects.MOVEMENT_SLOWDOWN;
                case 3 -> MobEffects.DIG_SLOWDOWN;
                default -> null;
            };

            if (effect == null) return;
            entity.addEffect(new MobEffectInstance(effect, choice == 0 ? 100 : 20, 0, false, false));
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
