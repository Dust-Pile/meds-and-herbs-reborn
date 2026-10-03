package com.plank.meds_and_herbs.effect;


import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class BeverageDrink extends MobEffect {
    private final String DRINK_DAMAGE = "5adf762d-9a1f-4b9b-8b13-91b6ffb06734";

    public BeverageDrink() {
        super(MobEffectCategory.BENEFICIAL, 0xCCCC00);
        this.addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                DRINK_DAMAGE,
                3.0,
                AttributeModifier.Operation.ADDITION
        );
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}
