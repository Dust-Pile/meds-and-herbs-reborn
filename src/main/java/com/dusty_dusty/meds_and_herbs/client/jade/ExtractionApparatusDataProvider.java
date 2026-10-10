package com.dusty_dusty.meds_and_herbs.client.jade;

import com.dusty_dusty.meds_and_herbs.MedsAndHerbs;
import com.dusty_dusty.meds_and_herbs.block.entity.ExtractionApparatusBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public enum ExtractionApparatusDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof ExtractionApparatusBlockEntity be) {
            compoundTag.putInt("progress", be.getProgress());
            compoundTag.putInt("maxProgress", be.getMaxProgress());
            compoundTag.putBoolean("running", be.isRunning());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return MedsAndHerbs.id("extraction_apparatus_data");
    }
}