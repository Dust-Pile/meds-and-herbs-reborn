package com.plank.meds_and_herbs.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class Immune extends MobEffect {
    public Immune() {
        super(MobEffectCategory.BENEFICIAL, 0x00CCCC);
    }
}
