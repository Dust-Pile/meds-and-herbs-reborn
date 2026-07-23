package com.plank.meds_and_herbs.data;

import com.plank.meds_and_herbs.effect.EffectCures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class UseMedicine {

    private static void executeFunction(LivingEntity target, ResourceLocation functionPath) {
        var server = target.level().getServer();
        if (server == null) return;
        var function = server.getFunctions().get(functionPath);
        if(function.isPresent()) {
            // 使用 as 和 at 确保命令在目标实体上执行
            var source = target.createCommandSourceStack()
                    .withSuppressedOutput()
                    .withPermission(server.getFunctionCompilationLevel()); // 给足够权限
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
            EffectCures.cure(target, def.id());
        }
    }

}