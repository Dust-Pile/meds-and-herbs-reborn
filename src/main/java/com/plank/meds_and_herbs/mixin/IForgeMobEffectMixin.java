package com.plank.meds_and_herbs.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(MobEffectInstance.class)
public abstract class IForgeMobEffectMixin {
    @Shadow public abstract MobEffect getEffect();

//    @Inject(method = "getCurativeItems", at = @At("RETURN"), remap = false)
//    private void modifyCures(CallbackInfoReturnable<List<ItemStack>> cir, @Local(name = "ret") ArrayList<ItemStack> ret) {
//        MobEffect self = (MobEffect) this;
//        ResourceLocation effectId = BuiltInRegistries.MOB_EFFECT.getKey(self);
//        if (effectId == null) return;
//
//        if (self.getCategory() == MobEffectCategory.HARMFUL) { //todo test this
//            ret.remove(new ItemStack(Items.MILK_BUCKET));
//        }
//    }

    @Redirect(
            method = "getCurativeItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/effect/MobEffect;getCurativeItems()Ljava/util/List;"
            ),
            remap = false
    )
    private List<ItemStack> aa(MobEffect instance) {
        var cures = instance.getCurativeItems();
        if (this.getEffect().getCategory() == MobEffectCategory.HARMFUL) { //todo test this
            cures.remove(new ItemStack(Items.MILK_BUCKET));
        }
        return cures;
    }
}
