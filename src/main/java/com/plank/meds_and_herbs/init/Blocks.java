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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

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

    // Equipment
    public static final RegistryObject<Block> EXTRACTION_APPARATUS = register("extraction_apparatus", ExtractionApparatusBlock::new);
    public static final RegistryObject<Block> DISTILLERY_APPARATUS = register("distillery_apparatus", DistilleryApparatusBlock::new);
    public static final RegistryObject<Block> INCUBATOR = register("incubator", IncubatorBlock::new);
    public static final RegistryObject<Block> FERMENTATION_BARREL = register("fermentation_barrel", FermentationBarrelBlock::new);

    // Plants
    public static final RegistryObject<Block> VINCA = registerPlant("vinca", Effects.THROMBOSIS.get(), 440);
    public static final RegistryObject<Block> BELLADONNA = registerPlant("belladonna", Effects.BELLADONNA_BERRY.get(), 220);
    public static final RegistryObject<Block> SWEET_CLOVER = register("sweet_clover", () -> new TallFlowerBlock(flowerProperties));
    public static final RegistryObject<Block> CHAMOMILE = registerPlant("chamomile", MobEffects.MOVEMENT_SLOWDOWN, 140);
    public static final RegistryObject<Block> ARTEMISIA = registerPlant("artemisia", MobEffects.CONFUSION, 140);
    public static final RegistryObject<Block> OPIUM = registerPlant("opium", Effects.OPIUM_ADDICTION.get(), 220);
    public static final RegistryObject<Block> ALOE = register("aloe", () -> new NonBonemealableTallGrassBlock(flowerProperties));
    public static final RegistryObject<Block> COTTON = register("cotton", () -> new NonBonemealableTallGrassBlock(flowerProperties));
    public static final RegistryObject<Block> PLANTAGO = register("plantago", () -> new NonBonemealableTallGrassBlock(flowerProperties));

    // Potted Plants
    public static final RegistryObject<Block> POTTED_VINCA = registerPottedFlower("vinca", VINCA);
    public static final RegistryObject<Block> POTTED_BELLADONNA = registerPottedFlower("belladonna", BELLADONNA);
    public static final RegistryObject<Block> POTTED_CHAMOMILE  = registerPottedFlower("chamomile", CHAMOMILE );
    public static final RegistryObject<Block> POTTED_ARTEMISIA = registerPottedFlower("artemisia", ARTEMISIA);
    public static final RegistryObject<Block> POTTED_OPIUM = registerPottedFlower("opium", OPIUM);


    // Food
    public static final RegistryObject<Block> BELLADONNA_PIE = register("belladonna_pie", BelladonnaPieBlock::new);

    private static RegistryObject<Block> register(String name, Supplier<Block> block) {
        return REGISTRY.register(name, block);
    }

    private static RegistryObject<Block> registerPlant(String name, MobEffect effect, int duration) {
        return REGISTRY.register(name, () -> new FlowerBlock(effect, duration, flowerProperties));
    }
    private static RegistryObject<Block> registerPottedFlower(String name, Supplier<Block> flower) {
        return REGISTRY.register("potted_" + name,
                () -> new FlowerPotBlock(
                        () -> (FlowerPotBlock) net.minecraft.world.level.block.Blocks.FLOWER_POT,
                        flower,
                        BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.FLOWER_POT)
                )
        );
    }
}
