package com.plank.meds_and_herbs.mixin;

import com.plank.meds_and_herbs.effect.MedicalEffect;
import com.plank.meds_and_herbs.init.Effects;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Stack;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Shadow(remap = false)
    @Nullable
    protected Stack<DamageContainer> damageContainers;

    // 存储原始伤害（用于增伤计算）
    @Unique
    private static final ThreadLocal<Float> ORIGINAL_DAMAGE = ThreadLocal.withInitial(() -> 0.0F);

    // ========== 增伤逻辑 ==========

    // 1. 捕获原始伤害
    @ModifyVariable(
            method = "getDamageAfterMagicAbsorb",
            at = @At("HEAD"),
            argsOnly = true,
            remap = false
    )
    private float captureOriginalDamage(float damageAmount) {
        ORIGINAL_DAMAGE.set(damageAmount);
        return damageAmount;
    }

    // 2. 有抗性时降低抗性等级（允许负数）
    @Redirect(
            method = "getDamageAfterMagicAbsorb",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/effect/MobEffectInstance;getAmplifier()I"
            ),
            remap = false
    )
    private int reduceResistanceAmplifier(MobEffectInstance instance) {
        if (instance.getEffect() == MobEffects.DAMAGE_RESISTANCE) {
            LivingEntity self = (LivingEntity) (Object) this;
            if (self.hasEffect(Effects.INTERNAL_BLEEDING)) {
                int original = instance.getAmplifier();
                int reduction = Objects.requireNonNull(self.getEffect(Effects.INTERNAL_BLEEDING)).getAmplifier();
                return original - reduction; // 允许负数，实现减抗
            }
        }
        return instance.getAmplifier();
    }

    // 3. 计算增伤后的最终伤害，并更新 damageContainers
    @Inject(
            method = "getDamageAfterMagicAbsorb",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private void applyFinalDamage(DamageSource damageSource, float damageAmount,
                                  CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        float original = ORIGINAL_DAMAGE.get();
        ORIGINAL_DAMAGE.remove(); // 清理

        if (self.hasEffect(Effects.INTERNAL_BLEEDING)
                && !damageSource.is(DamageTypeTags.BYPASSES_EFFECTS)
                && original > 0.0F) {
            int amp = Objects.requireNonNull(self.getEffect(Effects.INTERNAL_BLEEDING)).getAmplifier();
            float bonus = original * 0.20F * (amp + 1);
            float finalDamage = cir.getReturnValueF() + bonus;
            cir.setReturnValue(finalDamage);

            // 更新容器，确保 actuallyHurt 使用增伤后的值
            if (this.damageContainers != null && !this.damageContainers.isEmpty()) {
                this.damageContainers.peek().setNewDamage(finalDamage);
            }
        }
    }

    // ========== 效果移除回调 ==========

    /**
     * 拦截所有效果移除（包括手动移除和自然到期），
     * 若移除的效果是 MedicalEffect 的子类，则调用其 onEffectRemoved 方法。
     */
    @Inject(
            method = "onEffectRemoved",
            at = @At("HEAD"),
            remap = false
    )
    private void onEffectRemovedCallback(MobEffectInstance effectInstance, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.getPersistentData().contains("meds_and_herbs:curing") && effectInstance.getEffect().value() instanceof MedicalEffect medicalEffect) {
            medicalEffect.onEffectRemoved(self, effectInstance.getAmplifier());
        }
    }
}