package com.juiceybeans.meds_and_herbs.effect;

import com.juiceybeans.meds_and_herbs.init.MHDamageTypes;
import com.juiceybeans.meds_and_herbs.util.MHUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class MushroomPoisoning extends MobEffect {
    public MushroomPoisoning() {
        super(MobEffectCategory.HARMFUL, 0x8B4513);
    }
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;

        if (entity.getRandom().nextFloat() < 0.1f) {
            int choice = entity.getRandom().nextInt(5);
            MobEffect effect = switch (choice) {
                case 0 -> MobEffects.HUNGER;
                case 1 -> MobEffects.CONFUSION;
                case 2 -> MobEffects.WEAKNESS;
                case 3 -> MobEffects.MOVEMENT_SLOWDOWN;
                default -> null;
            };

            if (effect != null) {
                entity.addEffect(new MobEffectInstance(effect, 200, choice == 0 ? 1 : 0, false, false));
            } else {
                MHUtils.hurtWithCustomType(entity, MHDamageTypes.MUSHROOM, 3.0f);
            }
        }
    }
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
