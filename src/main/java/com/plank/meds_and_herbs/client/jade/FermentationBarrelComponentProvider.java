package com.plank.meds_and_herbs.client.jade;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.block.entity.FermentationBarrelBlockEntity;
import com.plank.meds_and_herbs.recipe.FermentationRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

import java.util.ArrayList;
import java.util.List;

public enum FermentationBarrelComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof FermentationBarrelBlockEntity barrel)) {
            return;
        }

        IElementHelper elements = IElementHelper.get();

        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack stack = barrel.getItemInSlot(i);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }

        if (items.isEmpty()) {
            return;
        }

        if (items.size() < 5) {
            for (ItemStack stack : items) {
                tooltip.add(elements.smallItem(stack));
                tooltip.append(elements.text(
                        stack.getHoverName().plainCopy().withStyle(ChatFormatting.GRAY)));
            }
        } else {
            boolean first = true;
            for (ItemStack stack : items) {
                if (first) {
                    tooltip.add(elements.item(stack));
                    first = false;
                } else {
                    tooltip.append(elements.item(stack));
                }
            }
        }

        var data = accessor.getServerData();
        if (data.getBoolean("running")) {
            var progress = data.getInt("progress");
            int percent = progress * 100 / FermentationRecipe.DEFAULT_COOKING_TIME;
            tooltip.add(elements.text(
                    Component.translatable("jade.meds_and_herbs.fermentation_progress", percent)
                            .withStyle(ChatFormatting.AQUA)));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return MedsAndHerbs.id("fermentation_barrel_provider");
    }
}