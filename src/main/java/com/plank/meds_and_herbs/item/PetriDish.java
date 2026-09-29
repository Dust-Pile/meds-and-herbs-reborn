package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.init.MHItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public class PetriDish extends Item {
    public PetriDish() {
        super(new Properties().craftRemainder(MHItems.PETRI_DISH_EMPTY.get()));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return false;

        return tag.getCompound("petri_dish_data").getInt("maxProgress") > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null) {
            int progress = tag.getCompound("petri_dish_data").getInt("maxProgress");
            int maxProgress = tag.getCompound("petri_dish_data").getInt("maxProgress");

            if (maxProgress > 0) {
                float progressPercentage = (float) progress / maxProgress;
                int width = Math.round(13.0f * progressPercentage);
                return Math.min(width, 13);
            }
        }

        return super.getBarWidth(stack);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if (tag != null) {
            int progress = tag.getCompound("petri_dish_data").getInt("maxProgress");
            int maxProgress = tag.getCompound("petri_dish_data").getInt("maxProgress");

            if (maxProgress > 0) {
                float progressPercentage = (float) progress / maxProgress;
                int r = (int) (255 * (1 - progressPercentage));
                int g = (int) (255 * progressPercentage);
                return (r << 16) | (g << 8);
            }
        }
        return super.getBarColor(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        CompoundTag tag = stack.getTag();

        if (tag != null) {
            int progress = tag.getCompound("petri_dish_data").getInt("maxProgress");
            int maxProgress = tag.getCompound("petri_dish_data").getInt("maxProgress");

            if (maxProgress > 0) {
                tooltipComponents.add(Component.translatable("tooltip.meds_and_herbs.progress",
                        progress, maxProgress).withStyle(ChatFormatting.GRAY));

                /* todo make this a config option
                tooltipComponents.add(Component.translatable("tooltip.meds_and_herbs.progress",
                        progress * (100 / maxProgress), 100).withStyle(ChatFormatting.GRAY));

                 */
            }
        }
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
