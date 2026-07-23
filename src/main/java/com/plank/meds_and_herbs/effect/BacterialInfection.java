package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.DamageTypes;
import com.plank.meds_and_herbs.init.Effects;
import com.plank.meds_and_herbs.procedures.Kill;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.Random;

public class BacterialInfection extends MedicalEffect {

    private static final Random RANDOM = new Random();

    public BacterialInfection() {
        super(MobEffectCategory.NEUTRAL, 0x8B0000);
    }

    @Override
    public void onEffectRemoved(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;

        // 根据效果等级执行结束逻辑
        if (amplifier == 0) {
            // 初级感染：20% 概率升级为 1 级，否则获得免疫
            if (RANDOM.nextFloat() < 0.2f) {
                entity.addEffect(new MobEffectInstance(Effects.BACTERIAL_INFECTION, 24000, 1, false, false));
            } else {
                entity.addEffect(new MobEffectInstance(Effects.IMMUNE, 24000, 0, false, false));
            }
        } else {
            Kill.kill(entity, DamageTypes.BACTERIAL_INFECTION);
        }
    }
}