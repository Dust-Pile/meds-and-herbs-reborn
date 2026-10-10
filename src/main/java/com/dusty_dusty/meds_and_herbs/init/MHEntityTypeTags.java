package com.dusty_dusty.meds_and_herbs.init;

import com.dusty_dusty.meds_and_herbs.MedsAndHerbs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class MHEntityTypeTags {
    public static final TagKey<EntityType<?>> UNDEAD = create("undead");

    public static final TagKey<EntityType<?>> MEDS_IMMUNE = create("meds_immune");
    public static final TagKey<EntityType<?>> NO_BLOOD = create("no_blood");
    public static final TagKey<EntityType<?>> SELF_TREATING = create("self_treating");

    private static TagKey<EntityType<?>> create(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, MedsAndHerbs.id(name));
    }

    private static TagKey<EntityType<?>> create(ResourceLocation resLoc) {
        return TagKey.create(Registries.ENTITY_TYPE, resLoc);
    }
}
