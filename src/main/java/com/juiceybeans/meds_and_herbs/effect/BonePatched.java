package com.juiceybeans.meds_and_herbs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class BonePatched extends MobEffect {
    private final String BONE_PATCHED_SPEED = "01ece033-b913-4d83-ac40-7058fc776b42";

    public BonePatched() {
        super(MobEffectCategory.HARMFUL, 0x8B4513);
        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                BONE_PATCHED_SPEED,
                -0.02,
                AttributeModifier.Operation.ADDITION
        );
    }
}
