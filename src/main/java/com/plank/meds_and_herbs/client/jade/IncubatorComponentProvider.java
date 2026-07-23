package com.plank.meds_and_herbs.client.jade;

import com.plank.meds_and_herbs.block.entity.IncubatorBlockEntity;
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

    private static final int SLOTS_PER_ROW = 4;
    private static final int TOTAL_SLOTS = 8;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof IncubatorBlockEntity incubator)) {
            return;
        }

        IElementHelper elements = IElementHelper.get();

        // 收集所有槽位的物品（保持顺序）
        List<ItemStack> slotStacks = new ArrayList<>();
        for (int i = 0; i < TOTAL_SLOTS; i++) {
            slotStacks.add(incubator.getItemInSlot(i));
        }

        // 检查是否有任何培养皿（非空）
        boolean hasAny = slotStacks.stream().anyMatch(s -> !s.isEmpty());
        if (!hasAny) {
            return; // 完全为空，不显示
        }

        // 第一行（槽位 0-3）
        List<IElement> firstRow = new ArrayList<>();
        for (int i = 0; i < SLOTS_PER_ROW; i++) {
            ItemStack stack = slotStacks.get(i);
            ItemStack display = stack.isEmpty() ? new ItemStack(Items.STRUCTURE_VOID) : stack;
            firstRow.add(elements.smallItem(display));
        }
        // 添加第一行
        for (int i = 0; i < firstRow.size(); i++) {
            if (i == 0) {
                tooltip.add(firstRow.get(i));
            } else {
                tooltip.append(firstRow.get(i));
            }
        }

        // 第二行（槽位 4-7）
        List<IElement> secondRow = new ArrayList<>();
        for (int i = 0; i < SLOTS_PER_ROW; i++) {
            int slotIndex = i + SLOTS_PER_ROW;
            ItemStack stack = slotStacks.get(slotIndex);
            ItemStack display = stack.isEmpty() ? new ItemStack(Items.STRUCTURE_VOID) : stack;
            secondRow.add(elements.smallItem(display));
        }
        // 添加第二行（新的一行）
        for (int i = 0; i < secondRow.size(); i++) {
            if (i == 0) {
                tooltip.add(secondRow.get(i));
            } else {
                tooltip.append(secondRow.get(i));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("meds_and_herbs", "incubator_provider");
    }
}