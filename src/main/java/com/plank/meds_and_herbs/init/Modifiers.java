package com.plank.meds_and_herbs.init;

import com.plank.meds_and_herbs.MedsAndHerbs;
import net.minecraft.resources.ResourceLocation;

public class Modifiers {
    public static final ResourceLocation DRINK_DAMAGE = create("drink_damage");
    public static final ResourceLocation BONE_FRACTURE_SPEED = create("bone_fracture_speed");
    public static final ResourceLocation BONE_PATCHED_SPEED = create("bone_patched_speed");
    public static final ResourceLocation ADRENALINE_DAMAGE = create("adrenaline_damage");
    public static final ResourceLocation ADRENALINE_SPEED = create("adrenaline_speed");
    public static final ResourceLocation ADRENALINE_ATTACK_SPEED = create("adrenaline_attack_speed");
    private static ResourceLocation create(String name) {
        return ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, "effect." + name);
    }
}
