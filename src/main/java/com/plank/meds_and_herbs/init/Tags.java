package com.plank.meds_and_herbs.init;

import com.plank.meds_and_herbs.MedsAndHerbs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class Tags {
    public static final class Items {
        public static final TagKey<Item> RAW_MEAT = tag("raw_meat");
        public static final TagKey<Item> MUSHROOM_STEW = tag("mushroom_stew");
        public static final TagKey<Item> MEDKIT_ITEMS = tag("medkit_items");
        public static final TagKey<Item> MEDICINE = tag("medicine");
        public static final TagKey<Item> POWDERS = tag("powders");
        public static final TagKey<Item> FILTER = tag("filter");
        public static final TagKey<Item> EMPTY_BOTTLE = tag("empty_bottle");
        // 花朵标签：包含模组自带的花 + 原版小花
        public static final TagKey<Item> FLOWERS = tag("flowers");

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, name));
        }
    }
    public static final class Blocks {
        public static final TagKey<Block> STATIONS = tag("stations");
        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, name));
        }
    }
    public static final class DamageTypes {
        public static final TagKey<DamageType> FIRE = tag("fire");
        public  static final TagKey<DamageType> PHYSICAL = tag("physical");

        private static TagKey<DamageType> tag(String name) {
            return TagKey.create(Registries.DAMAGE_TYPE, ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, name));
        }
    }
}
