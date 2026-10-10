package com.dusty_dusty.meds_and_herbs.mixin;

import com.dusty_dusty.meds_and_herbs.init.MHEffects;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(
            method = "getDamageAfterMagicAbsorb",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void customMagicDamageReduction(DamageSource source, float amount,
                                            CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (source.is(DamageTypeTags.BYPASSES_EFFECTS)) {
            cir.setReturnValue(amount);
            return;
        }

        int resistLevel = 0;
        if (self.hasEffect(MobEffects.DAMAGE_RESISTANCE)
                && !source.is(DamageTypeTags.BYPASSES_RESISTANCE)) {
            resistLevel = self.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() + 1;
        }

        int bleedLevel = 0;
        if (self.hasEffect(MHEffects.INTERNAL_BLEEDING.get())) {
            bleedLevel = self.getEffect(MHEffects.INTERNAL_BLEEDING.get()).getAmplifier() + 1;
        }

        int effectiveLevel = resistLevel - bleedLevel;

        float multiplier = 1.0F - 0.2F * effectiveLevel;
        if (multiplier < 0.0F) multiplier = 0.0F;

        cir.setReturnValue(amount * multiplier);
    }
}