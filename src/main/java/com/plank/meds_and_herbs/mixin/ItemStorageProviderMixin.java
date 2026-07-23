package com.plank.meds_and_herbs.mixin;

import com.plank.meds_and_herbs.block.entity.FermentationBarrelBlockEntity;
import com.plank.meds_and_herbs.block.entity.IncubatorBlockEntity;
import com.plank.meds_and_herbs.block.entity.DistilleryApparatusBlockEntity;
import com.plank.meds_and_herbs.block.entity.ExtractionApparatusBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snownee.jade.addon.universal.ItemStorageProvider;
import snownee.jade.api.Accessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

@Mixin(ItemStorageProvider.class)
public class ItemStorageProviderMixin<T extends Accessor<?>> {

    @Inject(method = "appendTooltip", at = @At("HEAD"), cancellable = true, remap = false)
    private void onAppendTooltip(ITooltip tooltip, T accessor, IPluginConfig config, CallbackInfo ci) {
        Object target = accessor.getTarget();
        if (target instanceof BlockEntity be) {
            if (be instanceof FermentationBarrelBlockEntity ||
                    be instanceof IncubatorBlockEntity ||
                    be instanceof DistilleryApparatusBlockEntity ||
                    be instanceof ExtractionApparatusBlockEntity) {
                // 跳过默认容器显示（就像熔炉一样）
                ci.cancel();
            }
        }
    }

    @Inject(method = "appendServerData", at = @At("HEAD"), cancellable = true, remap = false)
    private void onAppendServerData(CompoundTag tag, T accessor, CallbackInfo ci) {
        Object target = accessor.getTarget();
        if (target instanceof BlockEntity be) {
            if (be instanceof FermentationBarrelBlockEntity ||
                    be instanceof IncubatorBlockEntity ||
                    be instanceof DistilleryApparatusBlockEntity ||
                    be instanceof ExtractionApparatusBlockEntity) {
                // 跳过默认容器数据发送
                ci.cancel();
            }
        }
    }

    @Inject(method = "shouldRequestData", at = @At("HEAD"), cancellable = true, remap = false)
    private void onShouldRequestData(T accessor, CallbackInfoReturnable<Boolean> cir) {
        Object target = accessor.getTarget();
        if (target instanceof BlockEntity be) {
            if (be instanceof FermentationBarrelBlockEntity ||
                    be instanceof IncubatorBlockEntity ||
                    be instanceof DistilleryApparatusBlockEntity ||
                    be instanceof ExtractionApparatusBlockEntity) {
                // 不需要请求默认容器数据
                cir.setReturnValue(false);
            }
        }
    }
}