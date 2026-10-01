package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.init.MHRecipes;
import com.plank.meds_and_herbs.init.MHSounds;
import com.plank.meds_and_herbs.recipe.GrinderRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class Grinder extends Item {
    private static final int USE_DURATION = 20;
    private static final int COOLDOWN_TICKS = 20;

    public Grinder() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public @Nonnull UseAnim getUseAnimation(@Nonnull ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public @Nonnull InteractionResultHolder<ItemStack> use(@Nonnull Level level, @Nonnull Player player, @Nonnull InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        ItemStack otherHandStack = hand == InteractionHand.MAIN_HAND ? player.getOffhandItem() : player.getMainHandItem();
        if (otherHandStack.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.meds_and_herbs.mortar.no_ingredient"), true);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        if (!level.isClientSide) {
            SimpleContainer input = new SimpleContainer(otherHandStack);
            Optional<GrinderRecipe> recipe = level.getRecipeManager()
                    .getRecipeFor(MHRecipes.GRINDER_TYPE.get(), input, level);

            if (recipe.isEmpty()) {
                player.displayClientMessage(Component.translatable("message.meds_and_herbs.mortar.no_recipe"), true);
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
        }

        player.startUsingItem(hand);
        level.playSound(null, player.blockPosition(), MHSounds.GRINDER.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        spawnItemParticles(level, player, otherHandStack);

        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    @Nonnull
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity livingEntity) {
        if (!(livingEntity instanceof Player player)) {
            return stack;
        }

        InteractionHand hand = player.getMainHandItem() == stack ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack otherHandStack = hand == InteractionHand.MAIN_HAND ? player.getOffhandItem() : player.getMainHandItem();

        if (otherHandStack.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.meds_and_herbs.mortar.no_ingredient"), true);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            return stack;
        }

        if (level.isClientSide) {
            return stack;
        }

        SimpleContainer input = new SimpleContainer(otherHandStack);
        Optional<GrinderRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(MHRecipes.GRINDER_TYPE.get(), input, level);

        if (recipe.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.meds_and_herbs.mortar.no_recipe"), true);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            return stack;
        }

        ItemStack output = recipe.get().assemble(input, level.registryAccess());
        otherHandStack.shrink(1);

        if (!player.getInventory().add(output)) {
            player.drop(output, false);
        }

        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        return stack;
    }

    @Override
    public void releaseUsing(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity livingEntity, int timeCharged) {
        // 蓄力未满即松开 → 取消，无任何消耗
    }

    private void spawnItemParticles(Level level, Player player, ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (stack.isEmpty()) return;

        int count = 10;
        double x = player.getX();
        double y = player.getY() + 1.0;
        double z = player.getZ();

        serverLevel.sendParticles(
                new ItemParticleOption(ParticleTypes.ITEM, stack),
                x, y, z,
                count,
                0.05, 0.05, 0.05,
                0.1
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        tooltipComponents.add(Component.translatable("tooltip.meds_and_herbs.charge").withStyle(ChatFormatting.GRAY));
    }
}