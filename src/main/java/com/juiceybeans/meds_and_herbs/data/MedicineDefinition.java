package com.juiceybeans.meds_and_herbs.data;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record MedicineDefinition(
        ResourceLocation id,
        String nameKey,
        Type type,
        int color,
        Optional<ResourceLocation> functionPath,
        List<ResourceLocation> cures
) {
    protected enum Type { INTERNAL, EXTERNAL }
    public boolean isInternal() {
        return type == Type.INTERNAL;
    }
}