package com.plank.meds_and_herbs.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;

public class UseMedicine {

    private static void executeFunction(LivingEntity target, ResourceLocation functionPath) {
        var server = target.level().getServer();
        if (server == null) return;
        var function = server.getFunctions().get(functionPath);
        if(function.isPresent()) {
            var source = target.createCommandSourceStack()
                    .withSuppressedOutput()
                    .withPermission(server.getFunctionCompilationLevel());
            server.getFunctions().execute(function.get(), source);
        }
    }

    public static boolean isInternalMedicine(ResourceLocation typeId) {
        var def = MedicineTypeLoader.get(typeId);
        return def != null && def.type() == MedicineDefinition.Type.INTERNAL;
    }

    public static void use(LivingEntity target, ResourceLocation typeId) {
        var def = MedicineTypeLoader.get(typeId);
        if (def == null) return;

        executeFunction(target, def.functionPath());

        if (def.cures() != null && !def.cures().isEmpty()) {
            for (ResourceLocation effectId : def.cures()) {
                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(effectId);
                if (effect != null) {
                    target.removeEffect(effect);
                }
            }
        }
    }
}