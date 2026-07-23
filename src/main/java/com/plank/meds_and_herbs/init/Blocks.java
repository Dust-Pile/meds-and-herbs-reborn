package com.plank.meds_and_herbs.init;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.block.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Blocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(Registries.BLOCK, MedsAndHerbs.MODID);

    private static final BlockBehaviour.Properties flowerProperties =
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XZ);

    // ─── 设备方块 ──────────────────────────────────────────────
    public static final DeferredHolder<Block, Block> EXTRACTION_APPARATUS = register("extraction_apparatus", ExtractionApparatusBlock::new);
    public static final DeferredHolder<Block, Block> DISTILLERY_APPARATUS = register("distillery_apparatus", DistilleryApparatusBlock::new);
    public static final DeferredHolder<Block, Block> INCUBATOR = register("incubator", IncubatorBlock::new);
    public static final DeferredHolder<Block, Block> FERMENTATION_BARREL = register("fermentation_barrel", FermentationBarrelBlock::new);

    // ─── 植物 ──────────────────────────────────────────────────
    public static final DeferredHolder<Block, Block> VINCA = registerPlant("vinca", Effects.THROMBOSIS, 440);
    public static final DeferredHolder<Block, Block> BELLADONNA = registerPlant("belladonna", Effects.BELLADONNA_BERRY, 220);
    public static final DeferredHolder<Block, Block> SWEET_CLOVER = register("sweet_clover", () -> new TallFlowerBlock(flowerProperties));
    public static final DeferredHolder<Block, Block> CHAMOMILE = registerPlant("chamomile", MobEffects.MOVEMENT_SLOWDOWN, 140);
    public static final DeferredHolder<Block, Block> ARTEMISIA = registerPlant("artemisia", MobEffects.CONFUSION, 140);
    public static final DeferredHolder<Block, Block> OPIUM = registerPlant("opium", Effects.OPIUM_ADDICTION, 220);
    public static final DeferredHolder<Block, Block> ALOE = register("aloe", () -> new TallGrassBlock(flowerProperties));
    public static final DeferredHolder<Block, Block> COTTON = register("cotton", () -> new TallGrassBlock(flowerProperties));
    public static final DeferredHolder<Block, Block> PLANTAGO = register("plantago", () -> new TallGrassBlock(flowerProperties));

    // ─── 花盆 ──────────────────────────────────────────────────
    public static final DeferredHolder<Block, Block> POTTED_VINCA = registerPottedFlower("vinca", VINCA);
    public static final DeferredHolder<Block, Block> POTTED_BELLADONNA = registerPottedFlower("belladonna", BELLADONNA);
    public static final DeferredHolder<Block, Block> POTTED_CHAMOMILE  = registerPottedFlower("chamomile", CHAMOMILE );
    public static final DeferredHolder<Block, Block> POTTED_ARTEMISIA = registerPottedFlower("artemisia", ARTEMISIA);
    public static final DeferredHolder<Block, Block> POTTED_OPIUM = registerPottedFlower("opium", OPIUM);


    // ─── 派 ────────────────────────────────────────────────────
    public static final DeferredHolder<Block, Block> BELLADONNA_PIE = register("belladonna_pie", BelladonnaPieBlock::new);

    // ─── 辅助方法 ──────────────────────────────────────────────
    private static DeferredHolder<Block, Block> register(String name, Supplier<Block> block) {
        return REGISTRY.register(name, block);  // ✅ 返回 DeferredHolder，不调用 .get()
    }

    private static DeferredHolder<Block, Block> registerPlant(String name, Holder<MobEffect> effect, int duration) {
        return REGISTRY.register(name, () -> new FlowerBlock(effect, duration, flowerProperties));
    }
    private static DeferredHolder<Block, Block> registerPottedFlower(String name, Supplier<Block> flower) {
        return REGISTRY.register("potted_" + name,
                () -> new FlowerPotBlock(
                        // 1. 空花盆的Supplier
                        () -> (FlowerPotBlock) net.minecraft.world.level.block.Blocks.FLOWER_POT,
                        // 2. 你的花朵的Supplier
                        flower,
                        // 3. 方块属性，直接使用原版花盆的属性
                        BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.FLOWER_POT)
                )
        );
    }
}
