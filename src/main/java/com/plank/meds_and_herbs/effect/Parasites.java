package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.MHDamageTypes;
import com.plank.meds_and_herbs.init.MHEffects;
import com.plank.meds_and_herbs.procedures.Kill;
import com.plank.meds_and_herbs.util.MHUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class Parasites extends MobEffect {
    public Parasites() {
        super(MobEffectCategory.HARMFUL, 0xA58A7D);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;

        if (entity.getRandom().nextFloat() < 0.1f) {
            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 0, false, false));
        }
        if (entity.getRandom().nextFloat() < 0.1f) {
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, false, false));
        }

        MobEffectInstance instance = entity.getEffect(MHEffects.PARASITES.get());
        if (instance != null && instance.getDuration() < 200) {
            MHUtils.hurtWithCustomType(entity, MHDamageTypes.PARASITES, 4.0f);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 10 == 0;
    }

    public static void onEffectExpired(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        Kill.kill(entity, MHDamageTypes.PARASITES);
    }
}
