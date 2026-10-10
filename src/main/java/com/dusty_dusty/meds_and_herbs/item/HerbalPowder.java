package com.dusty_dusty.meds_and_herbs.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class HerbalPowder extends Item {
    public HerbalPowder() {
        super(new Item.Properties());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        List<ItemStack> flowers = Bouquet.getFlowerItems(stack);

        if (flowers.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.meds_and_herbs.bouquet.empty")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        tooltip.add(Component.translatable("tooltip.meds_and_herbs.bouquet.contains")
                .withStyle(ChatFormatting.GOLD));

        for (ItemStack flower : flowers) {
            Component flowerName = flower.getHoverName().copy().withStyle(ChatFormatting.WHITE);
            tooltip.add(Component.literal("  • ").withStyle(ChatFormatting.GRAY).append(flowerName));
        }
    }
}
