package com.plank.meds_and_herbs.block;

import com.plank.meds_and_herbs.init.Effects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nonnull;

public class BelladonnaPieBlock extends CakeBlock {
    public BelladonnaPieBlock() {
        super(BlockBehaviour.Properties.of()
                .forceSolidOn()
                .strength(0.5F)
                .sound(SoundType.WOOL)
                .pushReaction(PushReaction.DESTROY)
        );
    }

    @Override
    @Nonnull
    public InteractionResult useWithoutItem(@Nonnull BlockState state, @Nonnull Level level,
                                            @Nonnull BlockPos pos, @Nonnull Player player,
                                            @Nonnull BlockHitResult hit) {
        // 检查玩家是否能够进食（非满饥饿）
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        }

        // 调用原版蛋糕的食用逻辑（减少一片，增加 2 饥饿值 + 0.1 饱和度）
        InteractionResult result = super.useWithoutItem(state, level, pos, player, hit);

        if (result.consumesAction() && !level.isClientSide) {
            applyBelladonnaEffect(player);
        }

        return result;
    }

    private void applyBelladonnaEffect(Player player) {
        var effect = Effects.BELLADONNA_BERRY;
        MobEffectInstance existing = player.getEffect(effect);

        if (existing != null) {
            // 已有效果：持续时间 +600 刻（30秒），等级 +1
            int newDuration = existing.getDuration() + 600;
            int newAmplifier = existing.getAmplifier() + 1;
            player.addEffect(new MobEffectInstance(effect, newDuration, newAmplifier));
        } else {
            // 新效果：持续 600 刻（30秒），等级 0
            player.addEffect(new MobEffectInstance(effect, 600, 0));
        }
    }
}
