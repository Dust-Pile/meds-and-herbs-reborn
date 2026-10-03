package com.plank.meds_and_herbs.event;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.command.HealCommand;
import com.plank.meds_and_herbs.data.MedicineTypeLoader;
import com.plank.meds_and_herbs.effect.*;
import com.plank.meds_and_herbs.init.MHDamageTypes;
import com.plank.meds_and_herbs.init.MHEffects;
import com.plank.meds_and_herbs.init.MHEntityTypeTags;
import com.plank.meds_and_herbs.init.MHTags;
import com.plank.meds_and_herbs.util.MHUtils;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = MedsAndHerbs.MODID)
public class CommonEvents {

    @SubscribeEvent
    public static void causeDiseaseFromEating(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem();

        if (entity.level().isClientSide) return;
        if (entity instanceof Player player && player.isCreative()) return;

        if (stack.is(MHTags.Items.RAW_MEAT)) {
            if (entity.hasEffect(MHEffects.PARASITES.get())) {
                MHUtils.hurtWithCustomType(entity, MHDamageTypes.PARASITES, 2.0f);
            } else if (entity.getRandom().nextFloat() < 0.2) {
                entity.addEffect(new MobEffectInstance(MHEffects.PARASITES.get(), 24000, 0, false, false));
            }
        }

        if (stack.is(MHTags.Items.MUSHROOM_STEW) && entity.getRandom().nextFloat() < 0.01f) {
            entity.addEffect(new MobEffectInstance(MHEffects.MUSHROOM_POISONING.get(), 2400, 0, false, false));
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        Holder<DamageType> type = source.typeHolder();

        double av = entity.getPersistentData().getDouble("AV"); //todo what the fuck is this
        float rawAmount = event.getAmount();
        float effectiveDamage = rawAmount * (1 - (float) av);

        if (entity instanceof Player player && player.isCreative()) return;

        // adrenaline
        if (entity.getHealth() < 6.0f && !entity.getType().is(MHEntityTypeTags.UNDEAD)
                && !entity.hasEffect(MHEffects.ADRENALINE.get()) && entity.getRandom().nextFloat() < 0.33f) {
            entity.addEffect(new MobEffectInstance(MHEffects.ADRENALINE.get(), 600, 0));
        }

        if (source.is(MHTags.DamageTypes.PHYSICAL)) {
            // fractures
            if (entity.hasEffect(MHEffects.BONE_PATCHED.get())) {
                entity.removeEffect(MHEffects.BONE_PATCHED.get());
            }

            if (entity.getRandom().nextFloat() < effectiveDamage * 0.012f) {
                entity.addEffect(new MobEffectInstance(
                        MHEffects.BONE_FRACTURE.get(),
                        Math.min(10 * (int) effectiveDamage, 24000),
                        0,
                        false,
                        false
                ));
            }

            // lacerations
            if (effectiveDamage < 5.0f) return;

            float chance = effectiveDamage * 0.05f;
            int duration = Math.min((int) (effectiveDamage * 1200), 24000);

            if (entity.getRandom().nextFloat() < chance) {
                entity.addEffect(new MobEffectInstance(MHEffects.LACERATION.get(), duration, 0));
            }
        }

        // burns
        if (!entity.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            if (type.is(DamageTypes.ON_FIRE)) {
                if (entity.getRandom().nextFloat() < 0.01f) {
                    entity.addEffect(new MobEffectInstance(
                            MHEffects.BURNS.get(),
                            12000, 0, false, false
                    ));
                }
            } else if (type.is(MHTags.DamageTypes.FIRE)) {
                entity.addEffect(new MobEffectInstance(
                        MHEffects.BURNS.get(),
                        24000, 1, false, false
                ));
            }
        }

        if (entity.hasEffect(MHEffects.BURNS.get()) && (type.is(DamageTypes.ON_FIRE) || type.is(MHTags.DamageTypes.FIRE))) {
            event.setAmount(event.getAmount() + entity.getEffect(MHEffects.BURNS.get()).getAmplifier());
        }

        // painkiller damage reduction
        if (entity.hasEffect(MHEffects.PAINKILLER.get())) {
            if (type.is(MHTags.DamageTypes.PHYSICAL)) {
                var data = entity.getPersistentData();
                double current = data.getDouble("PainkillerDamageTaken");
                data.putDouble("PainkillerDamageTaken", current + effectiveDamage);
                event.setCanceled(true);
            }
        }

        float health = entity.getHealth() - effectiveDamage;
        float chance = effectiveDamage * 0.05f;
        int duration = Math.min((int) (effectiveDamage * 1200), 24000);

        // bleeding
        if (health > 6.0f) {
            if (!source.is(MHTags.DamageTypes.PHYSICAL)) return;
            if (entity.getRandom().nextFloat() < chance) {
                entity.addEffect(new MobEffectInstance(MHEffects.BLEEDING.get(), duration / 5, 0));
            }
        } else {
            if (entity.hasEffect(MHEffects.BLEEDING.get())) {
                duration += entity.getEffect(MHEffects.BLEEDING.get()).getDuration();
            }
            entity.addEffect(new MobEffectInstance(MHEffects.BLOOD_LOSS.get(), duration, 0));
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        var entity = event.getEntity();
        if (entity instanceof Player player && player.isCreative()) return;
        if (entity.hasEffect(MHEffects.BLOOD_LOSS.get())) {
            event.setCanceled(true);
        }
        if (entity.hasEffect(MHEffects.PAINKILLER.get())) {
            event.setCanceled(true);
            var data = entity.getPersistentData();
            double current = data.getDouble("PainkillerDamageTaken");
            data.putDouble("PainkillerDamageTaken", current - event.getAmount());
        }
    }

    @SubscribeEvent
    public static void onEffectAdded(MobEffectEvent.Added event) {
        MobEffectInstance instance = event.getEffectInstance();
        if (instance == null) return;

        var entity = event.getEntity();
        if (entity.level().isClientSide) return;
        if (!entity.isAlive()) return;

        var effect = instance.getEffect();
        int amplifier = instance.getAmplifier();

        var id = ForgeRegistries.MOB_EFFECTS.getKey(instance.getEffect());

        switch (id.toString()) {
            case "meds_and_herbs:painkiller" -> Painkiller.onEffectAdded(entity);
        }

    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        MobEffectInstance instance = event.getEffectInstance();
        if (instance == null) return;

        var entity = event.getEntity();
        if (entity.level().isClientSide) return;
        if (!entity.isAlive()) return;

        var effect = instance.getEffect();
        int amplifier = instance.getAmplifier();

        var id = ForgeRegistries.MOB_EFFECTS.getKey(instance.getEffect());

        switch (id.toString()) {
            case "meds_and_herbs:adrenaline" -> Adrenaline.onEffectExpired(entity);
            case "meds_and_herbs:bacterial_infection" -> BacterialInfection.onEffectExpired(entity, instance);
            case "meds_and_herbs:bleeding" -> Bleeding.onEffectExpired(entity);
            case "meds_and_herbs:high_potency_poison" -> HighPotencyPoison.onEffectExpired(entity);
            case "meds_and_herbs:methanol_poisoning" -> MethanolPoisoning.onEffectExpired(entity);
            case "meds_and_herbs:painkiller" -> Painkiller.onEffectExpired(entity);
            case "meds_and_herbs:parasites" -> Parasites.onEffectExpired(entity);
        }
    }


    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(MedicineTypeLoader.INSTANCE);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        HealCommand.register(event.getDispatcher());
    }
}
