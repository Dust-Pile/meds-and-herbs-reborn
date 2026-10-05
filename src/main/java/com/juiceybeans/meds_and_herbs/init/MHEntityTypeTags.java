package com.juiceybeans.meds_and_herbs.init;

import com.juiceybeans.meds_and_herbs.MedsAndHerbs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class MHEntityTypeTags {
    public static final TagKey<EntityType<?>> UNDEAD = create(ResourceLocation.withDefaultNamespace("undead"));

    private static TagKey<EntityType<?>> create(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, MedsAndHerbs.id(name));
    }

    private static TagKey<EntityType<?>> create(ResourceLocation resLoc) {
        return TagKey.create(Registries.ENTITY_TYPE, resLoc);
    }
}
