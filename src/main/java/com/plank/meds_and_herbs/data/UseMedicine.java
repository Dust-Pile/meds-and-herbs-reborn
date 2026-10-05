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
        if (function.isPresent()) {
            var source = target.createCommandSourceStack()
                    .withSuppressedOutput()
                    .withPermission(server.getFunctionCompilationLevel());
            server.getFunctions().execute(function.get(), source);
        }
    }

    public static void use(LivingEntity target, MedicineDefinition medicine) {
        medicine.functionPath().ifPresent(functionPath -> executeFunction(target, functionPath));

        if (medicine.cures() != null && !medicine.cures().isEmpty()) {
            for (ResourceLocation effectId : medicine.cures()) {
                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(effectId);
                if (effect != null) {
                    target.removeEffect(effect);
                }
            }
        }
    }
}