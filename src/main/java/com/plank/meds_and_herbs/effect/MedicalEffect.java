package com.plank.meds_and_herbs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.EffectCure;

import javax.annotation.Nonnull;
import java.util.Set;

public abstract class MedicalEffect extends MobEffect {
    private EffectCure cure;

    public MedicalEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
    public MedicalEffect(MobEffectCategory category, int color, EffectCure cure) {
        super(category, color);
        setCure(cure);
    }

    protected void setCure(EffectCure cure) {
        this.cure = cure;
    }

    /**
     * 当药水效果结束时调用
     * @param entity 受影响的实体
     * @param amplifier 效果等级
     */
    public void onEffectRemoved(LivingEntity entity, int amplifier) {
        // 默认空实现，子类可覆盖
    }
    @Override
    public void fillEffectCures(@Nonnull Set<EffectCure> cures, @Nonnull MobEffectInstance effectInstance) {
        if (this.cure != null) cures.add(this.cure);
        cures.add(net.neoforged.neoforge.common.EffectCures.PROTECTED_BY_TOTEM);
    }
}