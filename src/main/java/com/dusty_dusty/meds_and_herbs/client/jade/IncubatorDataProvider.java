package com.dusty_dusty.meds_and_herbs.client.jade;

import com.dusty_dusty.meds_and_herbs.MedsAndHerbs;
import com.dusty_dusty.meds_and_herbs.block.entity.IncubatorBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.ContainerData;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

public enum IncubatorDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (!(blockAccessor.getBlockEntity() instanceof IncubatorBlockEntity incubator)) {
            return;
        }

        ContainerData data = incubator.getContainerData();
        int slots = data.getCount() / 2;

        int[] progress = new int[8];
        int[] maxProgress = new int[8];
        for (int i = 0; i < slots; i++) {
            progress[i] = data.get(i);
            maxProgress[i] = data.get(i + slots);
        }
        compoundTag.putIntArray("progress", progress);
        compoundTag.putIntArray("maxProgress", maxProgress);
    }

    @Override
    public ResourceLocation getUid() {
        return MedsAndHerbs.id("incubator_data");
    }
}