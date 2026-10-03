package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.MHDamageTypes;
import com.plank.meds_and_herbs.procedures.Kill;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HighPotencyPoison extends MobEffect {
    public HighPotencyPoison() {
        super(MobEffectCategory.HARMFUL, 0x000000);
    }

    public static void onEffectExpired(LivingEntity entity) {
        if (entity.level().isClientSide) return;
        Kill.kill(entity, MHDamageTypes.HIGH_POTENCY_POISON);
    }
}
