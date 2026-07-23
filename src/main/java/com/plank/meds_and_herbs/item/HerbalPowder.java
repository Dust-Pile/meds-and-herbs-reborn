package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.data.BouquetFlowers;
import com.plank.meds_and_herbs.init.DataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nonnull;
import java.util.List;

public class HerbalPowder extends Item {
    public HerbalPowder() {
        super(new Item.Properties().component(DataComponents.BOUQUET_FLOWERS.get(), BouquetFlowers.EMPTY));
    }
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nonnull TooltipContext context, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        BouquetFlowers flowers = stack.get(DataComponents.BOUQUET_FLOWERS.get());
        if (flowers == null || flowers.flowers().isEmpty()) {
            tooltip.add(Component.translatable("tooltip.meds_and_herbs.bouquet.empty")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }

        tooltip.add(Component.translatable("tooltip.meds_and_herbs.bouquet.contains")
                .withStyle(ChatFormatting.GOLD));

        for (ItemStack flower : flowers.getFlowerItems()) {
            Component flowerName = flower.getHoverName().copy().withStyle(ChatFormatting.WHITE);
            tooltip.add(Component.literal("  • ").withStyle(ChatFormatting.GRAY).append(flowerName));
        }
    }
}
