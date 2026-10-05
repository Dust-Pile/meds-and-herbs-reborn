package com.juiceybeans.meds_and_herbs.item;

import com.juiceybeans.meds_and_herbs.effect.EffectCures;
import com.juiceybeans.meds_and_herbs.init.MHEffects;
import com.juiceybeans.meds_and_herbs.init.MHItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Splint extends Item {
    private static final int COOLDOWN_TICKS = 40;
    private static final double REACH_DISTANCE = 4.0;

    public Splint() {
        super(new Properties().stacksTo(16));
    }

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

        performSplint(player, target, hand);
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

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

    private void performSplint(Player player, LivingEntity target, InteractionHand hand) {
        if (player.level().isClientSide) return;

        ItemStack splint = player.getItemInHand(hand);

        if (!target.hasEffect(MHEffects.BONE_FRACTURE.get())) {
            player.displayClientMessage(Component.translatable("message.meds_and_herbs.no_broken_bone"), true);
            return;
        }

        var effectInstance = target.getEffect(MHEffects.BONE_FRACTURE.get());
        int remainingDuration = effectInstance != null ? effectInstance.getDuration() : 0;
        int healDuration = remainingDuration / 2;

        ItemStack otherHand = hand == InteractionHand.MAIN_HAND ? player.getOffhandItem() : player.getMainHandItem();
        if (ForgeRegistries.ITEMS.getKey(otherHand.getItem()).toString().equals("meds_and_herbs:plaster")) {
            otherHand.shrink(1);
            healDuration = healDuration / 2;
        }

        EffectCures.cure(target, new ItemStack(MHItems.SPLINT.get()));
        target.addEffect(new MobEffectInstance(MHEffects.BONE_PATCHED.get(), healDuration, 0));

        if (!player.isCreative()) splint.shrink(1);
        player.swing(hand, true);

        player.displayClientMessage(Component.translatable("message.meds_and_herbs.bone_treated").withStyle(ChatFormatting.GREEN), true);
    }

    @Nullable
    private LivingEntity getTargetEntity(Player player) {
        double reach = REACH_DISTANCE;
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 endPos = eyePos.add(lookVec.scale(reach));
        AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(reach)).inflate(1.0);

        LivingEntity result = null;
        double closestDist = reach + 1.0;

        for (Entity entity : player.level().getEntities(player, searchBox,
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

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        tooltip.add(Component.translatable("tooltip.meds_and_herbs.use").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.meds_and_herbs.sneak").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.meds_and_herbs.splint.desc").withStyle(ChatFormatting.GRAY));
    }
}