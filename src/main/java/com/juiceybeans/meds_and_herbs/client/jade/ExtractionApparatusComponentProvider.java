package com.juiceybeans.meds_and_herbs.client.jade;

import com.juiceybeans.meds_and_herbs.MedsAndHerbs;
import com.juiceybeans.meds_and_herbs.block.entity.ExtractionApparatusBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IBoxStyle;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import snownee.jade.api.ui.IProgressStyle;

public enum ExtractionApparatusComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof ExtractionApparatusBlockEntity)) {
            return;
        }

        var data = accessor.getServerData();
        if (!data.getBoolean("running")) return;

        var progress = data.getInt("progress");
        var maxProgress = data.getInt("maxProgress");
        if (maxProgress <= 0) return;

        IElementHelper helper = IElementHelper.get();
        float ratio = (float) progress / maxProgress;

        IProgressStyle style = helper.progressStyle();
        IBoxStyle box = IBoxStyle.Empty.INSTANCE;

        IElement progressElement = helper.progress(ratio, null, style, box, false)
                .translate(new Vec2(-2.0F, 0.0F));
        tooltip.add(progressElement);
    }

    @Override
    public ResourceLocation getUid() {
        return MedsAndHerbs.id("extraction_apparatus_provider");
    }
}