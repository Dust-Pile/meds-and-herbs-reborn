package com.juiceybeans.meds_and_herbs.client.jade;

import com.juiceybeans.meds_and_herbs.block.DistilleryApparatusBlock;
import com.juiceybeans.meds_and_herbs.block.ExtractionApparatusBlock;
import com.juiceybeans.meds_and_herbs.block.FermentationBarrelBlock;
import com.juiceybeans.meds_and_herbs.block.IncubatorBlock;
import com.juiceybeans.meds_and_herbs.block.entity.DistilleryApparatusBlockEntity;
import com.juiceybeans.meds_and_herbs.block.entity.ExtractionApparatusBlockEntity;
import com.juiceybeans.meds_and_herbs.block.entity.FermentationBarrelBlockEntity;
import com.juiceybeans.meds_and_herbs.block.entity.IncubatorBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MedsAndHerbsJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(DistilleryApparatusDataProvider.INSTANCE, DistilleryApparatusBlockEntity.class);
        registration.registerBlockDataProvider(ExtractionApparatusDataProvider.INSTANCE, ExtractionApparatusBlockEntity.class);
        registration.registerBlockDataProvider(FermentationBarrelDataProvider.INSTANCE, FermentationBarrelBlockEntity.class);
        registration.registerBlockDataProvider(IncubatorDataProvider.INSTANCE, IncubatorBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(FermentationBarrelComponentProvider.INSTANCE, FermentationBarrelBlock.class);
        registration.registerBlockComponent(IncubatorComponentProvider.INSTANCE, IncubatorBlock.class);
        registration.registerBlockComponent(DistilleryApparatusComponentProvider.INSTANCE, DistilleryApparatusBlock.class);
        registration.registerBlockComponent(ExtractionApparatusComponentProvider.INSTANCE, ExtractionApparatusBlock.class);
    }
}