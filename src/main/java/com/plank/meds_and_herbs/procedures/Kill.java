package com.plank.meds_and_herbs.procedures;

import com.plank.meds_and_herbs.util.MHUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

public interface Kill {
    static void kill(LivingEntity entity, ResourceKey<DamageType> type) {
        if (entity.getHealth() > 0) {
            MHUtils.hurtWithCustomType(entity, type, entity.getHealth());
            if (entity.isAlive()) entity.kill();
        }
    }
}
