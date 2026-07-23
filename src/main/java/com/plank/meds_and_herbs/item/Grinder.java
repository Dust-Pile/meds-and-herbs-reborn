package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.init.Recipes;
import com.plank.meds_and_herbs.init.Sounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class Grinder extends Item {
    private static final int USE_DURATION = 20; // 蓄力时间（tick），1秒 = 20 tick
    private static final int COOLDOWN_TICKS = 20; // 1秒冷却

    public Grinder() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public int getUseDuration(@Nonnull ItemStack stack, @Nonnull LivingEntity entity) {
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
            SingleRecipeInput input = new SingleRecipeInput(otherHandStack);
            // ✅ 修改：使用通用 Recipe 类型
            Optional<? extends Recipe<SingleRecipeInput>> recipe = level.getRecipeManager()
                    .getRecipeFor(Recipes.GRINDER_TYPE.get(), input, level)
                    .map(RecipeHolder::value);
            if (recipe.isEmpty()) {
                player.displayClientMessage(Component.translatable("message.meds_and_herbs.mortar.no_recipe"), true);
                player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
                return InteractionResultHolder.fail(player.getItemInHand(hand));
            }
        }

        player.startUsingItem(hand);
        level.playSound(null, player.blockPosition(), Sounds.GRINDER.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
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

        SingleRecipeInput input = new SingleRecipeInput(otherHandStack);
        Optional<? extends Recipe<SingleRecipeInput>> recipe = level.getRecipeManager()
                .getRecipeFor(Recipes.GRINDER_TYPE.get(), input, level)
                .map(RecipeHolder::value);

        if (recipe.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.meds_and_herbs.mortar.no_recipe"), true);
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
            return stack;
        }

        // 使用 assemble 代替 getResultItem
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

    /**
     * 生成输入物品的粒子效果（原版 Item 粒子）
     */
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
                0.05, 0.05, 0.05,   // 减小散布范围
                0.1                 // 降低速度，粒子会缓慢飘散并受重力影响下落
        );
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context,
                                @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.meds_and_herbs.charge").withStyle(ChatFormatting.GRAY));
    }
}