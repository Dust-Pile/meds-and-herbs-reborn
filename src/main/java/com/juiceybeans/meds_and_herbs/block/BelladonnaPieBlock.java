package com.juiceybeans.meds_and_herbs.block;

import com.juiceybeans.meds_and_herbs.init.MHEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
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
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!player.canEat(false)) {
            return InteractionResult.PASS;
        }

        InteractionResult result = super.use(state, level, pos, player, hand, hit);

        if (result.consumesAction() && !level.isClientSide) {
            applyBelladonnaEffect(player);
        }

        return result;
    }

    private void applyBelladonnaEffect(Player player) {
        var effect = MHEffects.BELLADONNA_BERRY.get();
        MobEffectInstance existing = player.getEffect(effect);

        if (existing != null) {
            int newDuration = existing.getDuration() + 600;
            int newAmplifier = existing.getAmplifier() + 1;
            player.addEffect(new MobEffectInstance(effect, newDuration, newAmplifier));
        } else {
            player.addEffect(new MobEffectInstance(effect, 600, 0));
        }
    }
}
