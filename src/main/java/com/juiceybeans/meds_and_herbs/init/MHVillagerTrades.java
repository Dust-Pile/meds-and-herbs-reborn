package com.juiceybeans.meds_and_herbs.init;

import com.juiceybeans.meds_and_herbs.MedsAndHerbs;
import com.juiceybeans.meds_and_herbs.data.MedsType;
import com.juiceybeans.meds_and_herbs.item.Medicine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = MedsAndHerbs.MODID)
public class MHVillagerTrades {
    static Item EMERALD = Items.EMERALD;

    @SubscribeEvent
    public static void registerTrades(VillagerTradesEvent event) {
        if (event.getType() != MHVillagerProfessions.HERBALIST.get()) return;
        var trades = event.getTrades();

        trades.get(1).addAll(List.of(
                new BasicItemListing(new ItemStack(MHItems.BOUQUET.get(), 2), new ItemStack(EMERALD), 10, 5, 0.05f),
                new BasicItemListing(new ItemStack(EMERALD, 10), new ItemStack(MHItems.GRINDER.get()), 10, 5, 0.05f)
        ));

        trades.get(2).addAll(List.of(
                plantTrade(MHItems.VINCA.get()),
                plantTrade(MHItems.BELLADONNA.get()),
                plantTrade(MHItems.SWEET_CLOVER.get()),
                plantTrade(MHItems.CHAMOMILE.get()),
                plantTrade(MHItems.OPIUM.get()),
                plantTrade(MHItems.PLANTAGO.get()),
                plantTrade(MHItems.ARTEMISIA.get()),
                plantTrade(MHItems.ALOE.get())
        ));

        trades.get(3).addAll(List.of(
                medicineTrade(16, MedsType.VINCA, MHItems.VINCA.get()),
                medicineTrade(16, MedsType.BELLADONNA, MHItems.BELLADONNA.get()),
                medicineTrade(16, MedsType.SWEET_CLOVER, MHItems.SWEET_CLOVER.get()),
                medicineTrade(16, MedsType.CHAMOMILE, MHItems.CHAMOMILE.get()),
                medicineTrade(16, MedsType.ARTEMISIA, MHItems.ARTEMISIA.get()),
                medicineTrade(16, MedsType.OPIUM, MHItems.OPIUM.get()),
                medicineTrade(16, MedsType.ALOE, MHItems.ALOE.get()),
                medicineTrade(16, MedsType.HERBAL, MHItems.BOUQUET.get())
        ));

        trades.get(4).addAll(List.of(
                medicineTrade(32, MedsType.GLUCOSE, Items.SUGAR),
                medicineTrade(32, MedsType.ALOE, MHItems.BARK.get()),
                medicineTrade(32, MedsType.MUSHROOM, Items.BROWN_MUSHROOM),
                medicineTrade(32, MedsType.CAFFEINE, Items.COCOA_BEANS)
        ));

        trades.get(5).addAll(List.of(
                medicineTrade(32, MedsType.ANTIDOTE),
                medicineTrade(32, MedsType.ETHANOL),
                medicineTrade(64, MedsType.PENICILLIN),
                medicineTrade(64, MedsType.HIGH_POTENCY_ANTIDOTE),
                medicineTrade(64, MedsType.ADRENALINE)
        ));
    }

    private static BasicItemListing plantTrade(Item plant) {
        return new BasicItemListing(new ItemStack(plant, 4), new ItemStack(EMERALD), 10, 5, 0.05f);
    }

    private static BasicItemListing medicineTrade(int emeraldCost, ResourceLocation type) {
        ItemStack medicine = Medicine.create(type, 3);
        return new BasicItemListing(new ItemStack(EMERALD, emeraldCost), medicine, 10, 5, 0.05f);
    }

    private static BasicItemListing medicineTrade(int emeraldCost, ResourceLocation type, Item plant) {
        ItemStack price1 = new ItemStack(EMERALD, emeraldCost);
        ItemStack price2 = new ItemStack(plant, 16);
        ItemStack result = Medicine.create(type, 3);
        return new BasicItemListing(price1, price2, result, 10, 5, 0.05f);
    }
}