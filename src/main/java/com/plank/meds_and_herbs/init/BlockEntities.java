package com.plank.meds_and_herbs.init;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.block.entity.DistilleryApparatusBlockEntity;
import com.plank.meds_and_herbs.block.entity.FermentationBarrelBlockEntity;
import com.plank.meds_and_herbs.block.entity.IncubatorBlockEntity;
import com.plank.meds_and_herbs.block.entity.ExtractionApparatusBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MedsAndHerbs.MODID);

    // 蒸馏器：使用无参 build()，不传递额外方块（因为已经在 Builder.of 中指定了方块）
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DistilleryApparatusBlockEntity>> DISTILLERY_APPARATUS =
            REGISTRY.register("distillery_apparatus",
                    () -> BlockEntityType.Builder.of(
                            DistilleryApparatusBlockEntity::new,
                            Blocks.DISTILLERY_APPARATUS.get()
                    ).build(null)
            );

    // 孵化器
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IncubatorBlockEntity>> INCUBATOR =
            REGISTRY.register("incubator",
                    () -> BlockEntityType.Builder.of(
                            IncubatorBlockEntity::new,
                            Blocks.INCUBATOR.get()
                    ).build(null)
            );

    // 提取装置
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ExtractionApparatusBlockEntity>> EXTRACTION_APPARATUS =
            REGISTRY.register("extraction_apparatus",
                    () -> BlockEntityType.Builder.of(
                            ExtractionApparatusBlockEntity::new,
                            Blocks.EXTRACTION_APPARATUS.get()
                    ).build(null)
            );

    // 发酵桶
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FermentationBarrelBlockEntity>> FERMENTATION_BARREL =
            REGISTRY.register("fermentation_barrel",
                    () -> BlockEntityType.Builder.of(
                            FermentationBarrelBlockEntity::new,
                            Blocks.FERMENTATION_BARREL.get()
                    ).build(null)
            );
}
