package com.plank.meds_and_herbs.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.extensions.IForgeMobEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(IForgeMobEffect.class)
public interface IForgeMobEffectMixin {
    @Inject(method = "getCurativeItems", at = @At("RETURN"), remap = false)
    private void modifyCures(CallbackInfoReturnable<List<ItemStack>> cir, @Local(name = "ret") ArrayList<ItemStack> ret) {
        MobEffect self = (MobEffect) this;
        ResourceLocation effectId = BuiltInRegistries.MOB_EFFECT.getKey(self);
        if (effectId == null) return;

        if (self.getCategory() == MobEffectCategory.HARMFUL) { //todo test this
            ret.remove(new ItemStack(Items.MILK_BUCKET));
        }
    }
}
