package com.plank.meds_and_herbs.effect;


import com.plank.meds_and_herbs.init.Modifiers;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class BeverageDrink extends MedicalEffect {

    public BeverageDrink() {
        super(MobEffectCategory.BENEFICIAL, 0xCCCC00);
        this.addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                Modifiers.DRINK_DAMAGE,
                3.0,
                AttributeModifier.Operation.ADD_VALUE
        );
    }
    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // 每秒触发一次
        return duration % 20 == 0;
    }
}
