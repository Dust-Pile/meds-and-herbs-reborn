package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.Modifiers;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Adrenaline extends MedicalEffect {

    public Adrenaline() {
        super(MobEffectCategory.BENEFICIAL, 0xFF0000);
        this.addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                Modifiers.ADRENALINE_DAMAGE,
                AttributeModifier.Operation.ADD_VALUE,
                (amplifier) -> 3 + amplifier
        );
        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                Modifiers.ADRENALINE_SPEED,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                (amplifier) -> amplifier * 0.2
        );
        this.addAttributeModifier(
                Attributes.ATTACK_SPEED,
                Modifiers.ADRENALINE_ATTACK_SPEED,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
                (amplifier) -> amplifier * 0.2
        );
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // 每秒触发一次
        return duration % 20 == 0;
    }
    @Override
    public void onEffectRemoved(LivingEntity entity, int amplifier) {
        // 效果结束时给予负面效果
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, 0, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 300, 0, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 0, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 2, false, false));
    }
}
