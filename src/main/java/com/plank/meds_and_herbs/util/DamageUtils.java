package com.plank.meds_and_herbs.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

public class DamageUtils {
    public static boolean hurtWithCustomType(Entity entity, ResourceKey<DamageType> damageType, float amount) {
        return entity.hurt(new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType)), amount);
    }
}
