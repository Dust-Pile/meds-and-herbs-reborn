package com.plank.meds_and_herbs.effect;


import com.plank.meds_and_herbs.init.DamageTypes;
import com.plank.meds_and_herbs.init.Effects;
import com.plank.meds_and_herbs.procedures.Kill;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class Bleeding extends MedicalEffect {

    public Bleeding() {
        super(MobEffectCategory.HARMFUL, 0xCC0000, EffectCures.DRESSING);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;

        // 如果有止痛药，跳过伤害（可根据需要调整）
        if (entity.hasEffect(Effects.PAINKILLER) || entity.hasEffect(Effects.BLOOD_LOSS)) {
            return true;
        }

        // 判断是否有内出血效果
        boolean hasInternal = entity.hasEffect(Effects.INTERNAL_BLEEDING);

        float damage;
        var damageSource = entity.damageSources().source(DamageTypes.BLEEDING);

        damage = hasInternal ? 3.0f : 1.0f;

        entity.hurt(damageSource, damage);

        return true;
    }

    @Override
    public void onEffectRemoved(LivingEntity entity, int amplifier) {
        if(entity.hasEffect(Effects.BLOOD_LOSS)) Kill.kill(entity, DamageTypes.BLEEDING);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // 每秒触发一次（原逻辑计数器累积 20 ticks）
        return duration % 20 == 0;
    }
}
