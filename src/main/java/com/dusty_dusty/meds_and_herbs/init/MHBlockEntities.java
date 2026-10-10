package com.dusty_dusty.meds_and_herbs.init;

import com.dusty_dusty.meds_and_herbs.MedsAndHerbs;
import com.dusty_dusty.meds_and_herbs.block.entity.DistilleryApparatusBlockEntity;
import com.dusty_dusty.meds_and_herbs.block.entity.FermentationBarrelBlockEntity;
import com.dusty_dusty.meds_and_herbs.block.entity.IncubatorBlockEntity;
import com.dusty_dusty.meds_and_herbs.block.entity.ExtractionApparatusBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MHBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MedsAndHerbs.MODID);

    public static final RegistryObject<BlockEntityType<DistilleryApparatusBlockEntity>> DISTILLERY_APPARATUS =
            REGISTRY.register("distillery_apparatus",
                    () -> BlockEntityType.Builder.of(
                            DistilleryApparatusBlockEntity::new,
                            MHBlocks.DISTILLERY_APPARATUS.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<IncubatorBlockEntity>> INCUBATOR =
            REGISTRY.register("incubator",
                    () -> BlockEntityType.Builder.of(
                            IncubatorBlockEntity::new,
                            MHBlocks.INCUBATOR.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<ExtractionApparatusBlockEntity>> EXTRACTION_APPARATUS =
            REGISTRY.register("extraction_apparatus",
                    () -> BlockEntityType.Builder.of(
                            ExtractionApparatusBlockEntity::new,
                            MHBlocks.EXTRACTION_APPARATUS.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<FermentationBarrelBlockEntity>> FERMENTATION_BARREL =
            REGISTRY.register("fermentation_barrel",
                    () -> BlockEntityType.Builder.of(
                            FermentationBarrelBlockEntity::new,
                            MHBlocks.FERMENTATION_BARREL.get()
                    ).build(null)
            );
}
