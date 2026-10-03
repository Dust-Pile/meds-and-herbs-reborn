package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.MHEffects;
import com.plank.meds_and_herbs.init.MHItems;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class Laceration extends MobEffect {
    public Laceration() {
        super(MobEffectCategory.HARMFUL, 0xCC0000);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;

        if (entity.hasEffect(MHEffects.IMMUNE.get()) || entity.hasEffect(MHEffects.BACTERIAL_INFECTION.get())) {
            return;
        }

        MobEffectInstance instance = entity.getEffect(MHEffects.LACERATION.get());
        if (instance != null && entity.getRandom().nextFloat() < 0.001) {
            entity.addEffect(new MobEffectInstance(MHEffects.BACTERIAL_INFECTION.get(), 24000, 0));
        }
    }
    
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        List<ItemStack> items = super.getCurativeItems();
        items.add(new ItemStack(MHItems.SEWING_KIT.get()));
        return items;
    }
}
