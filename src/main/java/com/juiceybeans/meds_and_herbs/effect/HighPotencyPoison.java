package com.juiceybeans.meds_and_herbs.effect;

import com.juiceybeans.meds_and_herbs.init.MHDamageTypes;
import com.juiceybeans.meds_and_herbs.util.MHUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class HighPotencyPoison extends MobEffect {
    public HighPotencyPoison() {
        super(MobEffectCategory.HARMFUL, 0x000000);
    }

    public static void onEffectExpired(LivingEntity entity) {
        MHUtils.killWithDamageType(entity, MHDamageTypes.HIGH_POTENCY_POISON);
    }
}
