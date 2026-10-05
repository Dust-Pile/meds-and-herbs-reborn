package com.juiceybeans.meds_and_herbs.effect;

import com.juiceybeans.meds_and_herbs.init.MHDamageTypes;
import com.juiceybeans.meds_and_herbs.init.MHEffects;
import com.juiceybeans.meds_and_herbs.util.MHUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class MethanolPoisoning extends MobEffect {
    public MethanolPoisoning() {
        super(MobEffectCategory.HARMFUL, 0xAAA185);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;

        var instance = entity.getEffect(MHEffects.METHANOL_POISONING.get());
        if (instance == null) return;

        int duration = instance.getDuration();

        if (duration > 3000) {
            if (entity.getRandom().nextFloat() < 0.1) {
                int choice = entity.getRandom().nextInt(5);
                MobEffect effect = switch (choice) {
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
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
    
    public static void onEffectExpired(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 3000, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 3000, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 3000, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 3000, 0));
        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 3000, 0));
        MHUtils.hurtWithCustomType(entity, MHDamageTypes.METHANOL, 5.0f);
    }
}
