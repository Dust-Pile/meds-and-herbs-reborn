package com.plank.meds_and_herbs.init;

import com.plank.meds_and_herbs.item.Medicine;
import com.plank.meds_and_herbs.data.MedsType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.List;

public class VillagerTrades {
    static Item EMERALD = net.minecraft.world.item.Items.EMERALD;

    @SubscribeEvent
    public static void registerTrades(VillagerTradesEvent event) {
        if (event.getType() != VillagerProfessions.HERBALIST.get()) return;
        var trades = event.getTrades();

        // 等级1：花束、研钵
        trades.get(1).addAll(List.of(
                new BasicItemListing(new ItemStack(Items.BOUQUET, 2), new ItemStack(EMERALD), 10, 5, 0.05f),
                new BasicItemListing(new ItemStack(EMERALD, 10), new ItemStack(Items.GRINDER), 10, 5, 0.05f)
        ));

        // 等级2：植物材料
        trades.get(2).addAll(List.of(
                plantTrade(Items.VINCA.get()),
                plantTrade(Items.BELLADONNA.get()),
                plantTrade(Items.SWEET_CLOVER.get()),
                plantTrade(Items.CHAMOMILE.get()),
                plantTrade(Items.OPIUM.get()),
                plantTrade(Items.PLANTAGO.get()),
                plantTrade(Items.ARTEMISIA.get()),
                plantTrade(Items.ALOE.get())
        ));

        // 等级3：提取物（药品）交易
        trades.get(3).addAll(List.of(
                medicineTrade(16, MedsType.VINCA, Items.VINCA.get()),
                medicineTrade(16, MedsType.BELLADONNA, Items.BELLADONNA.get()),
                medicineTrade(16, MedsType.SWEET_CLOVER, Items.SWEET_CLOVER.get()),
                medicineTrade(16, MedsType.CHAMOMILE, Items.CHAMOMILE.get()),
                medicineTrade(16, MedsType.ARTEMISIA, Items.ARTEMISIA.get()),
                medicineTrade(16, MedsType.OPIUM, Items.OPIUM.get()),
                medicineTrade(16, MedsType.ALOE, Items.ALOE.get()),
                medicineTrade(16, MedsType.HERBAL, Items.BOUQUET.get())
        ));

        // 等级4：特殊提取物（糖 -> 葡萄糖，树皮 -> 芦荟，蘑菇 -> 蘑菇）
        trades.get(4).addAll(List.of(
                medicineTrade(32, MedsType.GLUCOSE, net.minecraft.world.item.Items.SUGAR),
                medicineTrade(32, MedsType.ALOE, Items.BARK.get()),
                medicineTrade(32, MedsType.MUSHROOM, net.minecraft.world.item.Items.BROWN_MUSHROOM),
                medicineTrade(32, MedsType.CAFFEINE, net.minecraft.world.item.Items.COCOA_BEANS)
        ));

        // 等级5：高级药品（解毒剂）
        trades.get(5).addAll(List.of(
                medicineTrade(32, MedsType.ANTIDOTE),
                medicineTrade(32, MedsType.ETHANOL),
                medicineTrade(64, MedsType.PENICILLIN),
                medicineTrade(64, MedsType.HIGH_POTENCY_ANTIDOTE),
                medicineTrade(64, MedsType.ADRENALINE)
        ));
    }

    // 辅助：植物换绿宝石交易
    private static BasicItemListing plantTrade(Item plant) {
        return new BasicItemListing(new ItemStack(plant, 4), new ItemStack(EMERALD), 10, 5, 0.05f);
    }

    // 新增：直接通过绿宝石数量 + 药品类型兑换药品（1个药品，默认3次使用）
    private static BasicItemListing medicineTrade(int emeraldCost, ResourceLocation type) {
        ItemStack medicine = Medicine.create(type, 3); // 假设有3次使用
        return new BasicItemListing(new ItemStack(EMERALD, emeraldCost), medicine, 10, 5, 0.05f);
    }

    // 原有：绿宝石 + 植物 → 药品
    private static BasicItemListing medicineTrade(int emeraldCost, ResourceLocation type, Item plant) {
        ItemStack price1 = new ItemStack(EMERALD, emeraldCost);
        ItemStack price2 = new ItemStack(plant, 16);
        ItemStack result = Medicine.create(type, 3);
        return new BasicItemListing(price1, price2, result, 10, 5, 0.05f);
    }
}