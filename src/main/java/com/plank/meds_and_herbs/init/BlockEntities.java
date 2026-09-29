package com.plank.meds_and_herbs.init;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.block.entity.DistilleryApparatusBlockEntity;
import com.plank.meds_and_herbs.block.entity.FermentationBarrelBlockEntity;
import com.plank.meds_and_herbs.block.entity.IncubatorBlockEntity;
import com.plank.meds_and_herbs.block.entity.ExtractionApparatusBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MedsAndHerbs.MODID);

    public static final RegistryObject<BlockEntityType<DistilleryApparatusBlockEntity>> DISTILLERY_APPARATUS =
            REGISTRY.register("distillery_apparatus",
                    () -> BlockEntityType.Builder.of(
                            DistilleryApparatusBlockEntity::new,
                            Blocks.DISTILLERY_APPARATUS.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<IncubatorBlockEntity>> INCUBATOR =
            REGISTRY.register("incubator",
                    () -> BlockEntityType.Builder.of(
                            IncubatorBlockEntity::new,
                            Blocks.INCUBATOR.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<ExtractionApparatusBlockEntity>> EXTRACTION_APPARATUS =
            REGISTRY.register("extraction_apparatus",
                    () -> BlockEntityType.Builder.of(
                            ExtractionApparatusBlockEntity::new,
                            Blocks.EXTRACTION_APPARATUS.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<FermentationBarrelBlockEntity>> FERMENTATION_BARREL =
            REGISTRY.register("fermentation_barrel",
                    () -> BlockEntityType.Builder.of(
                            FermentationBarrelBlockEntity::new,
                            Blocks.FERMENTATION_BARREL.get()
                    ).build(null)
            );
}
