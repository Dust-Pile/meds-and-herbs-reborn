package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.DamageTypes;
import com.plank.meds_and_herbs.init.Effects;
import com.plank.meds_and_herbs.init.Modifiers;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class BoneFracture extends MedicalEffect {
    public BoneFracture() {
        super(MobEffectCategory.HARMFUL, 0x8B4513, EffectCures.SPLINT); // 棕色，代表骨骼
        // 添加速度减益：-0.05（基础速度0.1 → 0.05）
        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                Modifiers.BONE_FRACTURE_SPEED,
                -0.05,
                AttributeModifier.Operation.ADD_VALUE
        );
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;

        // 生命值≤2时不造成伤害（原逻辑）
        if (entity.getHealth() <= 2.0f) return true;

        // 有止痛药时跳过伤害
        if (entity.hasEffect(Effects.PAINKILLER)) return true;

        // 只在冲刺时造成伤害
        if (!entity.isSprinting()) return true;

        // 根据是否有肾上腺素决定伤害频率
        boolean hasAdrenaline = entity.hasEffect(Effects.ADRENALINE);

        double threshold = hasAdrenaline ? 1 : 0.5; // 有肾上腺素时每2秒，否则每秒

        if (entity.getRandom().nextFloat() < threshold) {
            entity.hurt(entity.damageSources().source(DamageTypes.FRACTURE), 1.0f);
        }

        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}