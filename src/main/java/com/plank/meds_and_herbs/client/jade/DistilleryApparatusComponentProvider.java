package com.plank.meds_and_herbs.client.jade;

import com.plank.meds_and_herbs.block.entity.DistilleryApparatusBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

public enum DistilleryApparatusComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof DistilleryApparatusBlockEntity distillery)) {
            return;
        }

        var handler = distillery.getItemHandler();
        IElementHelper helper = IElementHelper.get();

        // 输入（槽位0,1）
        tooltip.add(helper.item(handler.getStackInSlot(0).copy()));
        tooltip.append(helper.item(handler.getStackInSlot(1).copy()));

        // 空格
        tooltip.append(helper.spacer(4, 0));

        // 进度箭头（实时）
        float progressRatio = distillery.getMaxProgress() > 0 ? (float) distillery.getProgress() / distillery.getMaxProgress() : 0;
        IElement progressElement = helper.progress(progressRatio).translate(new Vec2(-2.0F, 0.0F));
        tooltip.append(progressElement);

        // 输出（槽位2,3）
        tooltip.append(helper.item(handler.getStackInSlot(2).copy()));
        tooltip.append(helper.item(handler.getStackInSlot(3).copy()));
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("meds_and_herbs", "distillery_apparatus_provider");
    }
}