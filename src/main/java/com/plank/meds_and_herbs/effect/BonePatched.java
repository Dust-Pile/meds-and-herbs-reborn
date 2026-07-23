package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.Modifiers;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class BonePatched extends MedicalEffect {
    public BonePatched() {
        super(MobEffectCategory.HARMFUL, 0x8B4513);
        // 添加速度减益：-0.05（基础速度0.1 → 0.05）
        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                Modifiers.BONE_PATCHED_SPEED,
                -0.02,
                AttributeModifier.Operation.ADD_VALUE
        );
    }
}
