package com.plank.meds_and_herbs.client.jade;

import com.plank.meds_and_herbs.block.DistilleryApparatusBlock;
import com.plank.meds_and_herbs.block.ExtractionApparatusBlock;
import com.plank.meds_and_herbs.block.FermentationBarrelBlock;
import com.plank.meds_and_herbs.block.IncubatorBlock;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class MedsAndHerbsJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        // 无需注册数据提供者
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        // ---- 注册自定义组件 ----
        registration.registerBlockComponent(FermentationBarrelComponentProvider.INSTANCE, FermentationBarrelBlock.class);
        registration.registerBlockComponent(IncubatorComponentProvider.INSTANCE, IncubatorBlock.class);
        registration.registerBlockComponent(DistilleryApparatusComponentProvider.INSTANCE, DistilleryApparatusBlock.class);
        registration.registerBlockComponent(ExtractionApparatusComponentProvider.INSTANCE, ExtractionApparatusBlock.class);
    }
}