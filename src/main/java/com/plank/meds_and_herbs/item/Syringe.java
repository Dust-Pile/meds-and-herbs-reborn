package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.data.*;
import com.plank.meds_and_herbs.init.DamageTypes;
import com.plank.meds_and_herbs.init.DataComponents;
import com.plank.meds_and_herbs.init.Effects;
import com.plank.meds_and_herbs.init.Items;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Syringe extends Item {
    private static final double REACH_DISTANCE = 5.0;
    private static final int COOLDOWN_TICKS = 20;

    public Syringe() {
        super(new Properties().stacksTo(1).durability(16));
    }

    @Override
    public @Nonnull InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player,
                                                           @Nonnull LivingEntity target, @Nonnull InteractionHand hand) {
        if (player.level().isClientSide) return InteractionResult.SUCCESS;
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResult.FAIL;
        performInjection(player, target, hand);
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nonnull InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (level.isClientSide) return InteractionResultHolder.consume(player.getItemInHand(hand));
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResultHolder.fail(player.getItemInHand(hand));

        LivingEntity target;
        if (player.isShiftKeyDown()) {
            target = player;
        } else {
            target = getTargetEntity(player);
            if (target == null) {
                player.displayClientMessage(Component.translatable("message.meds_and_herbs.no_target"), true);
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
        }

        performInjection(player, target, hand);
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    // ---------- 辅助：获取效果的 Holder ----------
    private static Optional<Holder.Reference<MobEffect>> getEffectHolder(ResourceLocation id) {
        return BuiltInRegistries.MOB_EFFECT.getHolder(id);
    }

    private static boolean hasEffect(LivingEntity target, Holder<MobEffect> effect) {
        if(effect.getKey() != null) return getEffectHolder(effect.getKey().location())
                .map(target::hasEffect)
                .orElse(false);
        else return false;
    }

    // ---------- 核心注射逻辑 ----------
    private void performInjection(Player player, LivingEntity target, InteractionHand hand) {
        if (player.level().isClientSide) return;

        ItemStack syringe = player.getItemInHand(hand);
        ItemStack otherHand = hand == InteractionHand.MAIN_HAND ? player.getOffhandItem() : player.getMainHandItem();

        // 1. 副手是医疗箱
        if (otherHand.getItem() instanceof Medkit) {
            if (!tryAutoTreatFromMedkit(player, target, syringe, otherHand)) {
                player.displayClientMessage(Component.translatable("message.meds_and_herbs.no_medicine_in_kit"), true);
            }
            return;
        }

        // 2. 副手是空瓶 → 抽血
        if (otherHand.getItem() == Items.MEDICINE_BOTTLE.get()) {
            // 收集可用的血液类型（使用辅助方法检查效果）
            List<ResourceLocation> availableBloodTypes = new ArrayList<>();
            if (hasEffect(target, Effects.ADRENALINE)) {
                availableBloodTypes.add(MedsType.ADRENALINE_BLOOD);
            }
            if (hasEffect(target, MobEffects.POISON)) {
                availableBloodTypes.add(MedsType.POISON_BLOOD);
            }
            if (hasEffect(target, Effects.BELLADONNA_BERRY)) {
                availableBloodTypes.add(MedsType.BELLADONNA_POISON_BLOOD);
            }
            if (hasEffect(target, Effects.HIGH_POTENCY_POISON)) {
                availableBloodTypes.add(MedsType.HIGH_POTENCY_POISON_BLOOD);
            }

            ResourceLocation bloodType = availableBloodTypes.isEmpty()
                    ? MedsType.BLOOD
                    : availableBloodTypes.get(player.getRandom().nextInt(availableBloodTypes.size()));

            ItemStack bloodBottle = Medicine.create(bloodType, 3);
            player.setItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, bloodBottle);
            player.displayClientMessage(Component.translatable("message.meds_and_herbs.converted_to_blood"), true);

            DamageSource source = target.level().damageSources().source(DamageTypes.BLEEDING, player);
            target.hurt(source, 0.001f);
            target.setHealth(target.getHealth() - 6);
            syringe.hurtAndBreak(1, (ServerLevel) player.level(), (ServerPlayer) player, item -> {});
            return;
        }

        // 3. 副手是药瓶（含药品）→ 注射
        if (Medicine.isMedicineBottle(otherHand)) {
            MedicineData bottleData = Medicine.getMedicineData(otherHand);
            if (bottleData.uses() <= 0) {
                player.displayClientMessage(Component.translatable("message.meds_and_herbs.bottle_empty"), true);
                return;
            }

            ResourceLocation typeId = bottleData.typeId();
            if (!UseMedicine.isInternalMedicine(typeId)) {
                player.displayClientMessage(Component.translatable("message.meds_and_herbs.external_meds"), true);
                return;
            }

            // 直接注射：不检查 cures，直接使用
            UseMedicine.use(target, typeId);
            player.setItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND,
                    Medicine.consume(otherHand, player));
            syringe.hurtAndBreak(1, (ServerLevel) player.level(), (ServerPlayer) player, item -> {});

            Component medicineName = Medicine.name(otherHand);
            player.displayClientMessage(Component.translatable("message.meds_and_herbs.injected",
                    target.getDisplayName(), medicineName), true);
            return;
        }

        // 4. 其他情况
        player.displayClientMessage(Component.translatable("message.meds_and_herbs.need_bottle_or_medkit"), true);
    }

    // ---------- 医疗箱自动取药 ----------
    private boolean tryAutoTreatFromMedkit(Player player, LivingEntity target, ItemStack syringe, ItemStack medkitStack) {
        MedkitContents contents = medkitStack.get(DataComponents.MEDKIT_CONTENTS);
        if (contents == null || contents.isEmpty()) return false;

        List<ItemStack> items = new ArrayList<>();
        contents.itemsCopy().forEach(items::add);

        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (!Medicine.isMedicineBottle(stack)) continue;

            MedicineData data = Medicine.getMedicineData(stack);
            if (data.uses() <= 0) continue;

            ResourceLocation typeId = data.typeId();

            // 只取内用药物
            if (!UseMedicine.isInternalMedicine(typeId)) continue;

            MedicineDefinition def = MedicineTypeLoader.get(typeId);
            if (def == null) continue;

            // 检查是否可治愈
            boolean canCure = def.cures().stream()
                    .map(id -> getEffectHolder(id).orElse(null))
                    .filter(Objects::nonNull)
                    .anyMatch(target::hasEffect);
            if (!canCure) continue;

            UseMedicine.use(target, typeId);
            items.set(i, Medicine.consume(stack, player));
            medkitStack.set(DataComponents.MEDKIT_CONTENTS, new MedkitContents(items));
            syringe.hurtAndBreak(1, (ServerLevel) player.level(), (ServerPlayer) player, item -> {});

            Component medicineName = Medicine.name(stack);
            player.displayClientMessage(Component.translatable("message.meds_and_herbs.auto_treat",
                    target.getDisplayName(), medicineName), true);
            return true;
        }
        return false;
    }

    // ---------- 射线检测 ----------
    @Nullable
    private LivingEntity getTargetEntity(Player player) {
        double reach = REACH_DISTANCE;
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 endPos = eyePos.add(lookVec.scale(reach));
        AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(reach)).inflate(1.0);

        LivingEntity result = null;
        double closestDist = reach + 1.0;

        for (net.minecraft.world.entity.Entity entity : player.level().getEntities(player, searchBox,
                e -> e instanceof LivingEntity && e != player && e.isAlive())) {
            AABB entityBox = entity.getBoundingBox().inflate(0.3);
            var hit = entityBox.clip(eyePos, endPos);
            if (hit.isPresent()) {
                double dist = eyePos.distanceTo(hit.get());
                if (dist < closestDist) {
                    closestDist = dist;
                    result = (LivingEntity) entity;
                }
            }
        }
        return result;
    }

    // ---------- 提示 ----------
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context,
                                @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.meds_and_herbs.use").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.meds_and_herbs.sneak").withStyle(ChatFormatting.GRAY));
    }
}