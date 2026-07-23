package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.DamageTypes;
import com.plank.meds_and_herbs.init.Effects;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class MethanolPoisoning extends MedicalEffect {
    public MethanolPoisoning() {
        super(MobEffectCategory.HARMFUL, 0xAAA185);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;

        var instance = entity.getEffect(Effects.METHANOL_POISONING);
        if (instance == null) return true;

        int duration = instance.getDuration();

        if (duration > 3000) {
            if (entity.getRandom().nextFloat() < 0.1) {
                int choice = entity.getRandom().nextInt(5);
                Holder<MobEffect> effect = switch (choice) {
                    case 0 -> MobEffects.CONFUSION;
                    case 1 -> MobEffects.BLINDNESS;
                    case 2 -> MobEffects.MOVEMENT_SLOWDOWN;
                    case 3 -> MobEffects.WEAKNESS;
                    default -> MobEffects.DIG_SLOWDOWN;
                };
                entity.addEffect(new MobEffectInstance(effect, 20, 0));
            }
        }
        else{
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 200, 0));
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
    @Override
    public void onEffectRemoved(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 3000, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 3000, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 3000, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 3000, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 3000, 0));
        entity.hurt(entity.damageSources().source(DamageTypes.METHANOL), 5.0f);
    }
}
