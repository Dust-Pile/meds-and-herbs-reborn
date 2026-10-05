package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.MHDamageTypes;
import com.plank.meds_and_herbs.init.MHEffects;
import com.plank.meds_and_herbs.util.MHUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class BacterialInfection extends MobEffect {

    public BacterialInfection() {
        super(MobEffectCategory.NEUTRAL, 0x8B0000);
    }

    public static void onEffectExpired(LivingEntity entity, MobEffectInstance effect) {
        if (effect.getAmplifier() == 0) {
            if (entity.getRandom().nextFloat() < 0.2f) {
                entity.addEffect(new MobEffectInstance(
                        MHEffects.BACTERIAL_INFECTION.get(), 24000, 1, false, false));
            } else {
                entity.addEffect(new MobEffectInstance(
                        MHEffects.IMMUNE.get(), 24000, 0, false, false));
            }
        } else {
            MHUtils.killWithDamageType(entity, MHDamageTypes.BACTERIAL_INFECTION);
        }
    }
}