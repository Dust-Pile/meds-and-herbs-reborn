package com.plank.meds_and_herbs.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.neoforge.common.EffectCure;
import net.neoforged.neoforge.common.EffectCures;
import net.neoforged.neoforge.common.extensions.IMobEffectExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(IMobEffectExtension.class)
public interface MixinMobEffect {
    @Inject(method = "fillEffectCures", at = @At("RETURN"), remap = false)
    private void modifyCures(Set<EffectCure> cures, MobEffectInstance effectInstance, CallbackInfo ci) {
        MobEffect self = (MobEffect) this;
        ResourceLocation effectId = BuiltInRegistries.MOB_EFFECT.getKey(self);
        if (effectId == null) return;

        if (self.getCategory() == MobEffectCategory.HARMFUL) {
            cures.remove(EffectCures.MILK);
        }
    }
}
