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
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;
import java.util.function.Predicate;

public class VillagerProfessions {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, MedsAndHerbs.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS =
            DeferredRegister.create(Registries.VILLAGER_PROFESSION, MedsAndHerbs.MODID);

    // ✅ 只持有 DeferredHolder，不调用 .get()
    private static final DeferredHolder<Block, Block> WORK_BLOCK = Blocks.EXTRACTION_APPARATUS;

    public static final ResourceKey<PoiType> HERBALIST_POI_KEY =
            ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE,
                    ResourceLocation.fromNamespaceAndPath(MedsAndHerbs.MODID, "herbalist"));

    public static final DeferredHolder<PoiType, PoiType> HERBALIST_POI = POI_TYPES.register("herbalist",
            () -> {
                // 在注册事件中调用 .get()，此时 WORK_BLOCK 已绑定
                Block workBlock = WORK_BLOCK.get();
                Set<BlockState> states = ImmutableSet.copyOf(workBlock.getStateDefinition().getPossibleStates());
                return new PoiType(states, 1, 1);
            });

    public static final DeferredHolder<VillagerProfession, VillagerProfession> HERBALIST = PROFESSIONS.register("herbalist",
            () -> {
                Predicate<Holder<PoiType>> poiPredicate = holder -> holder.is(HERBALIST_POI_KEY);
                return new VillagerProfession(
                        "meds_and_herbs:herbalist",
                        poiPredicate,
                        poiPredicate,
                        ImmutableSet.of(),
                        ImmutableSet.of(),
                        Sounds.VILLAGER_WORK_HERBALIST.get() // 注册时调用 .get() 安全
                );
            });
}
