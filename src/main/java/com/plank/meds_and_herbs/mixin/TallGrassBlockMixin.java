package com.plank.meds_and_herbs.mixin;

import com.plank.meds_and_herbs.init.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TallGrassBlock.class)
public class TallGrassBlockMixin {

    @Inject(method = "performBonemeal", at = @At("HEAD"), cancellable = true, remap = false)
    private void onPerformBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo ci) {
        // 检查是否是芦荟、棉花或车前草
        if (state.getBlock() == Blocks.ALOE.get() ||
                state.getBlock() == Blocks.COTTON.get() ||
                state.getBlock() == Blocks.PLANTAGO.get()) {
            // 不执行任何操作，取消原本的方法
            ci.cancel();
        }
    }
}