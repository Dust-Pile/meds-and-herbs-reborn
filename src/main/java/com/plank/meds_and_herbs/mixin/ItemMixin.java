package com.plank.meds_and_herbs.mixin;

import com.plank.meds_and_herbs.data.PetriDishData;
import com.plank.meds_and_herbs.init.DataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Item.class)
public class ItemMixin {

    /**
     * 如果物品带有 PetriDishData 组件，则显示进度条。
     */
    @Inject(method = "isBarVisible", at = @At("HEAD"), cancellable = true, remap = false)
    private void onIsBarVisible(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        PetriDishData data = stack.get(DataComponents.PETRI_DISH_DATA.get());
        if (data != null && data.maxProgress() > 0) {
            cir.setReturnValue(true);
        }
        // 否则走原逻辑（默认 false）
    }

    /**
     * 计算进度条宽度（13 像素为满格）。
     */
    @Inject(method = "getBarWidth", at = @At("HEAD"), cancellable = true, remap = false)
    private void onGetBarWidth(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        PetriDishData data = stack.get(DataComponents.PETRI_DISH_DATA.get());
        if (data != null && data.maxProgress() > 0) {
            float progress = (float) data.progress() / data.maxProgress();
            int width = Math.round(13.0f * progress);
            cir.setReturnValue(Math.min(width, 13));
        }
        // 否则走原逻辑
    }

    /**
     * 自定义进度条颜色（从红色渐变到绿色）。
     */
    @Inject(method = "getBarColor", at = @At("HEAD"), cancellable = true, remap = false)
    private void onGetBarColor(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        PetriDishData data = stack.get(DataComponents.PETRI_DISH_DATA.get());
        if (data != null && data.maxProgress() > 0) {
            float progress = (float) data.progress() / data.maxProgress();
            int r = (int) (255 * (1 - progress));
            int g = (int) (255 * progress);
            int color = (r << 16) | (g << 8);
            cir.setReturnValue(color);
        }
        // 否则走原逻辑
    }

    @Inject(method = "appendHoverText", at = @At("TAIL"), remap = false)
    private void onAppendHoverText(ItemStack stack, Item.TooltipContext context,
                                   List<Component> tooltipComponents, TooltipFlag tooltipFlag,
                                   CallbackInfo ci) {
        PetriDishData data = stack.get(DataComponents.PETRI_DISH_DATA.get());
        if (data != null && data.maxProgress() > 0) {
            tooltipComponents.add(Component.translatable("tooltip.meds_and_herbs.progress",
                    data.progress(), data.maxProgress()).withStyle(ChatFormatting.GRAY));
        }
    }
}