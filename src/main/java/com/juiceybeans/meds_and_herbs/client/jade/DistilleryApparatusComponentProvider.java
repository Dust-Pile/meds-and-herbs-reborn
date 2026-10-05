package com.juiceybeans.meds_and_herbs.client.jade;

import com.juiceybeans.meds_and_herbs.MedsAndHerbs;
import com.juiceybeans.meds_and_herbs.block.entity.DistilleryApparatusBlockEntity;
import net.minecraft.resources.ResourceLocation;
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
        if (!(accessor.getBlockEntity() instanceof DistilleryApparatusBlockEntity)) {
            return;
        }

        var data = accessor.getServerData();
        if (!data.getBoolean("running")) return;

        var progress = data.getInt("progress");
        var maxProgress = data.getInt("maxProgress");
        float ratio = maxProgress > 0 ? (float) progress / maxProgress : 0f;

        IElement bar = IElementHelper.get().progress(ratio, null, IElementHelper.get().progressStyle(), IBoxStyle.Empty.INSTANCE, false);
        tooltip.add(bar);
    }

    @Override
    public ResourceLocation getUid() {
        return MedsAndHerbs.id("distillery_apparatus_provider");
    }
}