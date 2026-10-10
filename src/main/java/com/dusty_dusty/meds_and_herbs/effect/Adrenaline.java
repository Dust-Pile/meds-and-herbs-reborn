package com.dusty_dusty.meds_and_herbs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.UUID;

public class Adrenaline extends MobEffect {
    private final UUID ADRENALINE_DAMAGE = UUID.fromString("b459ea24-d400-4ad9-b670-32d62c958014");
    private final UUID ADRENALINE_SPEED = UUID.fromString("9adabbd9-8ef1-4422-b5a6-bc144cd8318a");
    private final UUID ADRENALINE_ATTACK_SPEED = UUID.fromString("3517d1f6-b062-4d23-b640-4709262df0de");

    public Adrenaline() {
        super(MobEffectCategory.BENEFICIAL, 0xFF0000);
        this.addAttributeModifier(
                Attributes.ATTACK_DAMAGE,
                String.valueOf(ADRENALINE_DAMAGE),
                3.0,
                AttributeModifier.Operation.ADDITION
        );
        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                String.valueOf(ADRENALINE_SPEED),
                0.2,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        this.addAttributeModifier(
                Attributes.ATTACK_SPEED,
                String.valueOf(ADRENALINE_ATTACK_SPEED),
                0.2,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }

    @Override
    public double getAttributeModifierValue(int amplifier, AttributeModifier modifier) {
        double base = modifier.getAmount();

        if (modifier.getId().equals(ADRENALINE_DAMAGE)) {
            return base + amplifier;
        }

        if (modifier.getId().equals(ADRENALINE_SPEED)
                || modifier.getId().equals(ADRENALINE_ATTACK_SPEED)) {
            return base * (amplifier + 1);
        }

        return base;
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    public static void onEffectExpired(LivingEntity entity) {
        entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, 0, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 300, 0, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, 0, false, false));
        entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 2, false, false));
    }
}