package com.plank.meds_and_herbs.client.jade;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.block.entity.IncubatorBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public enum IncubatorDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (!(blockAccessor.getBlockEntity() instanceof IncubatorBlockEntity incubator)) {
            return;
        }

        int[] progress = new int[8];
        int[] maxProgress = new int[8];
        for (int i = 0; i < 8; i++) {
            progress[i] = incubator.getContainerData().get(i);
            maxProgress[i] = incubator.getContainerData().get(i + 8);
        }
        compoundTag.putIntArray("progress", progress);
        compoundTag.putIntArray("maxProgress", maxProgress);
    }

    @Override
    public ResourceLocation getUid() {
        return MedsAndHerbs.id("incubator_data");
    }
}