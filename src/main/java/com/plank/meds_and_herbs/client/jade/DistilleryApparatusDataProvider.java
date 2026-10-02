package com.plank.meds_and_herbs.client.jade;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.block.entity.DistilleryApparatusBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public enum DistilleryApparatusDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof DistilleryApparatusBlockEntity distillery) {
            compoundTag.putInt("progress", distillery.getProgress());
            compoundTag.putInt("maxProgress", distillery.getMaxProgress());
            compoundTag.putBoolean("running", distillery.isRunning());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return MedsAndHerbs.id("distillery_apparatus_data");
    }
}