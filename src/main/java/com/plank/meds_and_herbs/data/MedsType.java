package com.plank.meds_and_herbs.data;

import net.minecraft.resources.ResourceLocation;

public final class MedsType {
    // === 植物提取物 ===
    public static final ResourceLocation HERBAL = id("herbal");
    public static final ResourceLocation VINCA = id("vinca");
    public static final ResourceLocation BELLADONNA = id("belladonna");
    public static final ResourceLocation SWEET_CLOVER = id("sweet_clover");
    public static final ResourceLocation CHAMOMILE = id("chamomile");
    public static final ResourceLocation ARTEMISIA = id("artemisia");
    public static final ResourceLocation OPIUM = id("opium");
    public static final ResourceLocation MUSHROOM = id("mushroom");
    public static final ResourceLocation CAFFEINE = id("caffeine");
    public static final ResourceLocation GLUCOSE = id("glucose");
    public static final ResourceLocation ETHANOL = id("ethanol");
    public static final ResourceLocation ALOE = id("aloe");
    public static final ResourceLocation METHANOL = id("methanol");

    // === 血液/毒液类 ===
    public static final ResourceLocation BLOOD = id("blood");
    public static final ResourceLocation POISON_BLOOD = id("poison_blood");
    public static final ResourceLocation BELLADONNA_POISON_BLOOD = id("belladonna_poison_blood");
    public static final ResourceLocation HIGH_POTENCY_POISON_BLOOD = id("high_potency_poison_blood");
    public static final ResourceLocation ADRENALINE_BLOOD = id("adrenaline_blood");

    // === 毒药（纯毒液） ===
    public static final ResourceLocation POISON = id("poison");
    public static final ResourceLocation BELLADONNA_POISON = id("belladonna_poison");
    public static final ResourceLocation HIGH_POTENCY_POISON = id("high_potency_poison");

    // === 药品 ===
    public static final ResourceLocation ADRENALINE = id("adrenaline");
    public static final ResourceLocation ANTIDOTE = id("antidote");
    public static final ResourceLocation HIGH_POTENCY_ANTIDOTE = id("high_potency_antidote");
    public static final ResourceLocation PENICILLIN = id("penicillin");
    public static final ResourceLocation MORPHINE = id("morphine");

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("meds_and_herbs", path);
    }
}