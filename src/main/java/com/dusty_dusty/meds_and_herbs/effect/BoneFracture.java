package com.dusty_dusty.meds_and_herbs.effect;

import com.dusty_dusty.meds_and_herbs.init.*;
import com.dusty_dusty.meds_and_herbs.util.MHUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BoneFracture extends MobEffect {
    private final String BONE_FRACTURE_SPEED = "fb80a688-7e9e-4687-9a8b-d5c43479d032";

    public BoneFracture() {
        super(MobEffectCategory.HARMFUL, 0x8B4513);
        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                BONE_FRACTURE_SPEED,
                -0.05,
                AttributeModifier.Operation.ADDITION
        );
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        if (entity.getHealth() <= 2.0f) return;
        if (entity.hasEffect(MHEffects.PAINKILLER.get())) return;
        if (!entity.isSprinting()) return;

        boolean hasAdrenaline = entity.hasEffect(MHEffects.ADRENALINE.get());

        double threshold = hasAdrenaline ? 1 : 0.5;

        if (entity.getRandom().nextFloat() < threshold) MHUtils.hurtWithCustomType(entity, MHDamageTypes.FRACTURE, 1.0f);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        List<ItemStack> items = super.getCurativeItems();
        items.add(new ItemStack(MHItems.SPLINT.get()));
        return items;
    }
}