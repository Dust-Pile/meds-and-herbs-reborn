package com.dusty_dusty.meds_and_herbs.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Bouquet extends Item {

    public Bouquet() {
        super(new Item.Properties());
    }

    public static void setFlowerData(ItemStack stack, List<ResourceLocation> flowers) {
        if (flowers == null || flowers.size() != 4) return;

        var list = new ListTag();
        for (var id : flowers) {
            list.add(StringTag.valueOf(id.toString()));
        }

        var bouquet = new CompoundTag();
        bouquet.put("flowers", list);
        stack.getOrCreateTag().put("BouquetFlowers", bouquet);
    }

    public static List<ResourceLocation> createFlowersList(Item... items) {
        List<ResourceLocation> flowers = new ArrayList<>();
        for (Item item : items) {
            var id = ForgeRegistries.ITEMS.getKey(item);
            flowers.add(id);
        }
        return flowers;
    }

    @Nonnull
    public static List<ResourceLocation> getFlowers(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null || !tag.contains("BouquetFlowers", Tag.TAG_COMPOUND)) return List.of();
        var bouquet = tag.getCompound("BouquetFlowers");
        if (!bouquet.contains("flowers", Tag.TAG_LIST)) return List.of();

        var list = bouquet.getList("flowers", Tag.TAG_STRING);
        List<ResourceLocation> ids = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            ResourceLocation id = ResourceLocation.tryParse(list.getString(i));
            if (id != null) ids.add(id);
        }
        return ids;
    }

    public static boolean isValid(ItemStack stack) {
        List<ResourceLocation> flowers = getFlowers(stack);
        if (flowers.size() != 4) return false;
        for (var id : flowers) {
            if (!ForgeRegistries.ITEMS.containsKey(id)) return false;
        }
        return true;
    }

    @Nonnull
    public static List<ItemStack> getFlowerItems(ItemStack stack) {
        List<ItemStack> items = new ArrayList<>();
        for (ResourceLocation id : getFlowers(stack)) {
            var item = ForgeRegistries.ITEMS.getValue(id);
            if (item != null) {
                items.add(new ItemStack(item));
            }
        }
        return items;
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level,
                                @Nonnull List<Component> tooltip, @Nonnull TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);

        List<ItemStack> flowerItems = getFlowerItems(stack);
        if (flowerItems.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.meds_and_herbs.bouquet.empty")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        tooltip.add(Component.translatable("tooltip.meds_and_herbs.bouquet.contains")
                .withStyle(ChatFormatting.GOLD));

        for (ItemStack flower : flowerItems) {
            Component flowerName = flower.getHoverName().copy().withStyle(ChatFormatting.WHITE);
            tooltip.add(Component.literal("  • ").withStyle(ChatFormatting.GRAY).append(flowerName));
        }
    }
}