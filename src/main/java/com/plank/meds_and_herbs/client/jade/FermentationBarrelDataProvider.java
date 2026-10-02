package com.plank.meds_and_herbs.client.jade;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.block.entity.FermentationBarrelBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public enum FermentationBarrelDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof FermentationBarrelBlockEntity barrel) {
            compoundTag.putInt("progress", barrel.getProgress());
            compoundTag.putBoolean("running", barrel.isRunning());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return MedsAndHerbs.id("fermentation_barrel_data");
    }
}