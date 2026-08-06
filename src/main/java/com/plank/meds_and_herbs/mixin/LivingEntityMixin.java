package com.plank.meds_and_herbs.mixin;

import com.plank.meds_and_herbs.init.Effects;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Stack;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Shadow(remap = false)
    @Nullable
    protected Stack<DamageContainer> damageContainers;

    /**
     * 完全重写魔法伤害减免，实现抗性等级与内出血等级相减的效果。
     * 内出血可以抵消抗性，使伤害增加（当 effectiveLevel 为负时）。
     */
    @Inject(
            method = "getDamageAfterMagicAbsorb",
            at = @At("HEAD"),
            cancellable = true,
            remap = false
    )
    private void customMagicDamageReduction(DamageSource source, float amount,
                                            CallbackInfoReturnable<Float> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        // 如果伤害无视效果，直接返回原伤害
        if (source.is(DamageTypeTags.BYPASSES_EFFECTS)) {
            cir.setReturnValue(amount);
            return;
        }

        // 获取抗性等级（放大器从0开始，等级=放大器+1）
        int resistLevel = 0;
        if (self.hasEffect(MobEffects.DAMAGE_RESISTANCE)) {
            resistLevel = self.getEffect(MobEffects.DAMAGE_RESISTANCE).getAmplifier() + 1;
        }

        // 获取内出血等级
        int bleedLevel = 0;
        if (self.hasEffect(Effects.INTERNAL_BLEEDING)) {
            bleedLevel = self.getEffect(Effects.INTERNAL_BLEEDING).getAmplifier() + 1;
        }

        // 有效等级 = 抗性 - 内出血（可为负）
        int effectiveLevel = resistLevel - bleedLevel;

        // 每级影响20%，允许伤害倍率 > 1（增伤），但禁止负伤害
        float multiplier = 1.0F - 0.2F * effectiveLevel;
        if (multiplier < 0.0F) multiplier = 0.0F; // 防止回血

        float finalDamage = amount * multiplier;

        // ===== 关键：更新 damageContainers，使 actuallyHurt 使用新伤害 =====
        if (this.damageContainers != null && !this.damageContainers.isEmpty()) {
            this.damageContainers.peek().setNewDamage(finalDamage);
        }

        // 返回最终伤害（虽然原版不用，但为兼容其他调用）
        cir.setReturnValue(finalDamage);
    }
}