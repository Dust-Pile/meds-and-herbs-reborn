package com.plank.meds_and_herbs.client.jade;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.block.entity.IncubatorBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

import java.util.ArrayList;
import java.util.List;

public enum IncubatorComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof IncubatorBlockEntity incubator)) {
            return;
        }

        IElementHelper elements = IElementHelper.get();

        List<ItemStack> slotStacks = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            slotStacks.add(incubator.getItemInSlot(i));
        }

        var hasAny = slotStacks.stream().anyMatch(s -> !s.isEmpty());
        if (!hasAny) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            var stack = slotStacks.get(i);
            var display = stack.isEmpty() ? new ItemStack(Items.STRUCTURE_VOID) : stack;
            var element = elements.smallItem(display);
            if (i == 0) {
                tooltip.add(element);
            } else {
                tooltip.append(element);
            }
        }

        for (int i = 0; i < 4; i++) {
            var stack = slotStacks.get(i + 4);
            var display = stack.isEmpty() ? new ItemStack(Items.STRUCTURE_VOID) : stack;
            var element = elements.smallItem(display);
            if (i == 0) {
                tooltip.add(element);
            } else {
                tooltip.append(element);
            }
        }

        var data = accessor.getServerData();
        int[] progress = data.getIntArray("progress");
        int[] maxProgress = data.getIntArray("maxProgress");

        for (int i = 0; i < 8; i++) {
            if (i >= progress.length || i >= maxProgress.length) break;
            if (maxProgress[i] <= 0) continue;
            int percent = progress[i] * 100 / maxProgress[i];
            tooltip.add(elements.text(Component.literal("Slot " + (i + 1) + ": " + percent + "%")));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return MedsAndHerbs.id("incubator_provider");
    }
}