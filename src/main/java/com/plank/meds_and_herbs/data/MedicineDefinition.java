package com.plank.meds_and_herbs.data;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record MedicineDefinition(
        ResourceLocation id,
        String nameKey,
        Type type,
        int color,
        ResourceLocation functionPath,
        List<ResourceLocation> cures
) {
    protected enum Type { INTERNAL, EXTERNAL }
    public boolean isInternal() {
        return type == Type.INTERNAL;
    }
}