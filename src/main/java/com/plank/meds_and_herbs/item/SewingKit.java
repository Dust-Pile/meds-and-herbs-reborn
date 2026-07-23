package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.effect.EffectCures;
import com.plank.meds_and_herbs.init.Effects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
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

public class SewingKit extends Item {
    private static final int COOLDOWN_TICKS = 100; // 5 秒

    public SewingKit() {
        super(new Properties().stacksTo(1).durability(3));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, List<Component> tooltip, @Nonnull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.meds_and_herbs.use").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.meds_and_herbs.sneak").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.meds_and_herbs.sewing_kit.desc").withStyle(ChatFormatting.GRAY));
    }

    // 右键点击实体直接使用
    @Override
    public @Nonnull InteractionResult interactLivingEntity(@Nonnull ItemStack stack, @Nonnull Player player,
                                                           @Nonnull LivingEntity target, @Nonnull InteractionHand hand) {
        if (player.level().isClientSide) return InteractionResult.SUCCESS;
        if (player.getCooldowns().isOnCooldown(this)) return InteractionResult.FAIL;

        performSewing(player, target, hand);
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResult.SUCCESS;
    }

    // 右键空气使用（支持主副手）
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

        performSewing(player, target, hand);
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    // ---------- 核心治疗逻辑 ----------
    private void performSewing(Player player, LivingEntity target, InteractionHand hand) {
        if (player.level().isClientSide) return;

        ItemStack kit = player.getItemInHand(hand);

        // 检查是否有撕裂伤
        if (!target.hasEffect(Effects.LACERATION)) {
            player.displayClientMessage(Component.translatable("message.meds_and_herbs.no_laceration"), true);
            return;
        }

        // 执行治愈
        EffectCures.cure(target, EffectCures.SEWING);
        // 消耗耐久
        kit.hurtAndBreak(1, (ServerLevel) player.level(), (ServerPlayer) player, item -> {});
        // 挥臂动画
        player.swing(hand, true);
        // 消息
        player.displayClientMessage(Component.translatable("message.meds_and_herbs.laceration_healed"), true);
    }

    // ---------- 射线检测目标实体 ----------
    @Nullable
    private LivingEntity getTargetEntity(Player player) {
        double reach = 4.0;
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 endPos = eyePos.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
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
}