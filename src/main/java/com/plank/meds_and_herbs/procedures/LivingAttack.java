package com.plank.meds_and_herbs.procedures;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.init.Effects;
import com.plank.meds_and_herbs.init.Tags;
import net.minecraft.core.Holder;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

import java.util.Objects;

@EventBusSubscriber(modid = MedsAndHerbs.MODID)
public class LivingAttack {
    @SubscribeEvent
    public static void onLivingAttack(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        Holder<DamageType> type = source.typeHolder();
        // 计算实际伤害（考虑自定义减伤 AV）
        double av = entity.getPersistentData().getDouble("AV"); // 伤害减免比例
        float rawAmount = event.getAmount();
        float effectiveDamage = rawAmount * (1 - (float) av);
        if (entity instanceof Player player && player.isCreative()) return;
        //肾上腺素
        {
            if (entity.getHealth() < 6.0f && !entity.getType().is(EntityTypeTags.UNDEAD)
                    && !entity.hasEffect(Effects.ADRENALINE) && entity.getRandom().nextFloat() < 0.33f) {
                entity.addEffect(new MobEffectInstance(Effects.ADRENALINE, 600, 0));
            }
        }
        //骨折
        {
            if (source.is(Tags.DamageTypes.PHYSICAL)) {

                if (entity.hasEffect(Effects.BONE_PATCHED)) {
                    entity.removeEffect(Effects.BONE_PATCHED);
                }

                if (entity.getRandom().nextFloat() < effectiveDamage * 0.012f) {
                    entity.addEffect(new MobEffectInstance(
                            Effects.BONE_FRACTURE,
                            Math.min(10 * (int) effectiveDamage, 24000),
                            0,
                            false,
                            false
                    ));
                }
            }
        }
        //烧伤
        {
            if (!entity.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                if (type.is(DamageTypes.ON_FIRE)) {
                    // 持续火焰伤害，1% 概率给予 0 级烧伤（12000 ticks）
                    if (entity.getRandom().nextFloat() < 0.01f) {
                        entity.addEffect(new MobEffectInstance(
                                Effects.BURNS,
                                12000, 0, false, false
                        ));
                    }
                } else if (type.is(Tags.DamageTypes.FIRE)) {
                    // 着火伤害，100% 概率给予 1 级烧伤（24000 ticks）
                    entity.addEffect(new MobEffectInstance(
                            Effects.BURNS,
                            24000, 1, false, false
                    ));
                }
            }
        }
        //烧伤（执行）
        {
            if (entity.hasEffect(Effects.BURNS) && (type.is(DamageTypes.ON_FIRE) || type.is(Tags.DamageTypes.FIRE))) {
                event.setAmount(event.getAmount() + Objects.requireNonNull(entity.getEffect(Effects.BURNS)).getAmplifier());
            }
        }
        //止痛（执行）
        {
            if (entity.hasEffect(Effects.PAINKILLER)) {
                // 如果伤害类型在可拦截列表中，则取消事件并记录伤害
                if (type.is(net.neoforged.neoforge.common.Tags.DamageTypes.IS_PHYSICAL)) {
                    var data = entity.getPersistentData();
                    double current = data.getDouble("PainkillerDamageTaken");
                    data.putDouble("PainkillerDamageTaken", current + effectiveDamage);
                    event.setCanceled(true);
                }
            }
        }
        //裂伤
        {
            if(source.is(Tags.DamageTypes.PHYSICAL)) {

                // 伤害阈值：至少 5 点伤害才可能触发撕裂
                if (effectiveDamage < 5.0f) return;

                float chance = effectiveDamage * 0.05f;

                int duration = Math.min((int) (effectiveDamage * 1200), 24000);

                // 随机判定
                if (entity.getRandom().nextFloat() < chance) {
                    entity.addEffect(new MobEffectInstance(Effects.LACERATION, duration, 0));
                }
            }
        }
        //流血、失血
        {
            float health = entity.getHealth() - effectiveDamage;
            float chance = effectiveDamage * 0.05f;
            int duration = Math.min((int) (effectiveDamage * 1200), 24000);
            if (health > 6.0f) {
                if(!source.is(Tags.DamageTypes.PHYSICAL)) return;
                if (entity.getRandom().nextFloat() < chance) {
                    entity.addEffect(new MobEffectInstance(Effects.BLEEDING, duration / 5, 0));
                }
            } else {
                if(entity.hasEffect(Effects.BLEEDING)) {
                    duration += entity.getEffect(Effects.BLEEDING).getDuration();
                }
                entity.addEffect(new MobEffectInstance(Effects.BLOOD_LOSS, duration, 0));
            }
        }
    }
}
