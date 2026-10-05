package com.juiceybeans.meds_and_herbs.effect;

import com.juiceybeans.meds_and_herbs.init.MHDamageTypes;
import com.juiceybeans.meds_and_herbs.util.MHUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class Thrombosis extends MobEffect {
    public Thrombosis() {
        super(MobEffectCategory.HARMFUL, 0x8B0000);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;

        if (entity.getRandom().nextFloat() < 0.1f) {
            int choice = entity.getRandom().nextInt(4);
            MobEffect effect = switch (choice) {
                case 0 -> MobEffects.DIG_SLOWDOWN;
                case 1 -> MobEffects.WEAKNESS;
                case 2 -> MobEffects.MOVEMENT_SLOWDOWN;
                default -> null;
            };

            if (effect != null) {
                entity.addEffect(new MobEffectInstance(effect, 100, 1));
            } else {
                MHUtils.hurtWithCustomType(entity, MHDamageTypes.THROMBOSIS, 1.0f);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
