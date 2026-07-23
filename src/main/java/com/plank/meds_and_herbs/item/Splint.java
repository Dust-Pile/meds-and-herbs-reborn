package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.effect.EffectCures;
import com.plank.meds_and_herbs.init.Effects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
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
import java.util.List;

public class Splint extends Item {
    private static final int COOLDOWN_TICKS = 40; // 2 秒
    private static final double REACH_DISTANCE = 4.0;

    public Splint() {
        super(new Properties().stacksTo(16)); // 允许堆叠，使用后消耗
    }

    // ---------- 右键使用（主手/副手，潜行自我治疗） ----------
    @Override
    public @Nonnull InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (level.isClientSide) {
            return InteractionResultHolder.consume(player.getItemInHand(hand));
        }
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

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

        // 执行治疗
        performSplint(player, target, hand);
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    // ---------- 右键点击实体（直接治疗该实体） ----------
    @Override
    public @Nonnull InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player,
                                                           @Nonnull LivingEntity target, @Nonnull InteractionHand hand) {
        if (player.level().isClientSide) return InteractionResult.SUCCESS;
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.FAIL;
        }

        performSplint(player, target, hand);
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResult.SUCCESS;
    }

    // ---------- 核心治疗逻辑 ----------
    private void performSplint(Player player, LivingEntity target, InteractionHand hand) {
        if (player.level().isClientSide) return;

        ItemStack splint = player.getItemInHand(hand);

        // 检查目标是否有骨折
        if (!target.hasEffect(Effects.BONE_FRACTURE)) {
            player.displayClientMessage(Component.translatable("message.meds_and_herbs.no_broken_bone"), true);
            return;
        }

        // 获取骨折剩余时间
        var effectInstance = target.getEffect(Effects.BONE_FRACTURE);
        int remainingDuration = effectInstance != null ? effectInstance.getDuration() : 0;
        int healDuration = remainingDuration / 2; // 基础治疗时间为剩余时间的一半

        // 检查另一只手是否有石膏（plaster）
        ItemStack otherHand = hand == InteractionHand.MAIN_HAND ? player.getOffhandItem() : player.getMainHandItem();
        if (BuiltInRegistries.ITEM.getKey(otherHand.getItem()).toString().equals("meds_and_herbs:plaster")) {
            otherHand.shrink(1);
            healDuration = healDuration / 2; // 使用石膏可将治疗时间再减半
        }

        // 移除骨折效果
        EffectCures.cure(target, EffectCures.SPLINT);
        // 添加骨愈合效果
        target.addEffect(new MobEffectInstance(Effects.BONE_PATCHED, healDuration, 0));

        // 消耗夹板
        if (!player.isCreative()) splint.shrink(1);
        // 动画（挥动手臂）
        player.swing(hand, true);

        // 提示成功
        player.displayClientMessage(Component.translatable("message.meds_and_herbs.bone_treated").withStyle(ChatFormatting.GREEN), true);
    }

    // ---------- 射线检测目标实体 ----------
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

    // ---------- 工具提示 ----------
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context,
                                @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.meds_and_herbs.use").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.meds_and_herbs.sneak").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.meds_and_herbs.splint.desc").withStyle(ChatFormatting.GRAY));
    }
}