package com.plank.meds_and_herbs.init;

import com.google.common.collect.ImmutableSet;
import com.plank.meds_and_herbs.MedsAndHerbs;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;
import java.util.function.Predicate;

public class VillagerProfessions {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, MedsAndHerbs.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS =
            DeferredRegister.create(Registries.VILLAGER_PROFESSION, MedsAndHerbs.MODID);

    private static final RegistryObject<Block> WORK_BLOCK = Blocks.EXTRACTION_APPARATUS;

    public static final ResourceKey<PoiType> HERBALIST_POI_KEY =
            ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE,
                    ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, "herbalist"));

    public static final RegistryObject<PoiType> HERBALIST_POI = POI_TYPES.register("herbalist",
            () -> {
                Block workBlock = WORK_BLOCK.get();
                Set<BlockState> states = ImmutableSet.copyOf(workBlock.getStateDefinition().getPossibleStates());
                return new PoiType(states, 1, 1);
            });

    public static final RegistryObject<VillagerProfession> HERBALIST = PROFESSIONS.register("herbalist",
            () -> {
                Predicate<Holder<PoiType>> poiPredicate = holder -> holder.is(HERBALIST_POI_KEY);
                return new VillagerProfession(
                        "meds_and_herbs:herbalist",
                        poiPredicate,
                        poiPredicate,
                        ImmutableSet.of(),
                        ImmutableSet.of(),
                        Sounds.VILLAGER_WORK_HERBALIST.get()
                );
            });
}
