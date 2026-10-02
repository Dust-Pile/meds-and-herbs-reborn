package com.plank.meds_and_herbs.client.jade;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.block.entity.DistilleryApparatusBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IBoxStyle;
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

        tooltip.add(helper.item(handler.getStackInSlot(0)));
        tooltip.append(helper.item(handler.getStackInSlot(1)));
        tooltip.append(helper.item(handler.getStackInSlot(2)));

        tooltip.append(helper.spacer(4, 0));

        var data = accessor.getServerData();
        var progress = data.getInt("progress");
        var maxProgress = data.getInt("maxProgress");
        float ratio = maxProgress > 0 ? (float) progress / maxProgress : 0f;

        IElement progressElement = helper
                .progress(ratio, null, helper.progressStyle(), IBoxStyle.Empty.INSTANCE, false) //todo test this
                .translate(new Vec2(-2.0F, 0.0F));
        tooltip.append(progressElement);

        tooltip.append(helper.spacer(4, 0));

        tooltip.append(helper.item(handler.getStackInSlot(3)));
        tooltip.append(helper.item(handler.getStackInSlot(4)));
    }

    @Override
    public ResourceLocation getUid() {
        return MedsAndHerbs.id("distillery_apparatus_provider");
    }
}