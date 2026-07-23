package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.DamageTypes;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class MushroomPoisoning extends MedicalEffect {
    public MushroomPoisoning() {
        super(MobEffectCategory.HARMFUL, 0x8B4513);
    }
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;

        // 每秒 10% 概率触发
        if (entity.getRandom().nextFloat() < 0.1f) {
            int choice = entity.getRandom().nextInt(5); // 0-4
            Holder<MobEffect> effect = switch (choice) {
                case 0 -> MobEffects.HUNGER;
                case 1 -> MobEffects.CONFUSION;
                case 2 -> MobEffects.WEAKNESS;
                case 3 -> MobEffects.MOVEMENT_SLOWDOWN;
                default -> null; // 造成伤害
            };

            if (effect != null) {
                entity.addEffect(new MobEffectInstance(effect, 200, choice == 0 ? 1 : 0, false, false));
            } else {
                entity.hurt(entity.damageSources().source(DamageTypes.MUSHROOM), 3.0f);
            }
        }

        return true;
    }
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
