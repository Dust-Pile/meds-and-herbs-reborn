package com.plank.meds_and_herbs.init;

import com.plank.meds_and_herbs.item.Bouquet;
import com.plank.meds_and_herbs.item.CottonDressing;
import com.plank.meds_and_herbs.item.SewingKit;
import com.plank.meds_and_herbs.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Locale;
import java.util.function.Supplier;

import static com.plank.meds_and_herbs.MedsAndHerbs.MODID;

public class Items {
    public static final DeferredRegister<Item> REGISTRY =
            DeferredRegister.create(Registries.ITEM, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);

    // ─── 空容器 ──────────────────────────────────────────────
    public static final DeferredHolder<Item, Item> SYRINGE = register("syringe", Syringe::new);
    public static final DeferredHolder<Item, Item> MEDICINE_BOTTLE = register("medicine_bottle", new Item.Properties().stacksTo(16));
    public static final DeferredHolder<Item, Item> DIRTY_MEDICINE_BOTTLE = register("dirty_medicine_bottle", new Item.Properties().stacksTo(16));

    // ─── 核心物品 ──────────────────────────────────────────────
    public static final DeferredHolder<Item, Item> MEDICINE = register("medicine", Medicine::new);

    // ─── 敷料、夹板、医疗包 ──────────────────────────────────
    public static final DeferredHolder<Item, Item> PLANTAGO_DRESSING = register("plantago_dressing", PlantagoDressing::new);
    public static final DeferredHolder<Item, Item> WOOL_DRESSING = register("wool_dressing", WoolDressing::new);
    public static final DeferredHolder<Item, Item> COTTON_DRESSING = register("cotton_dressing", CottonDressing::new);
    public static final DeferredHolder<Item, Item> SPLINT = register("splint", Splint::new);
    public static final DeferredHolder<Item, Item> MEDKIT = register("medkit", Medkit::new);
    public static final DeferredHolder<Item, Item> SEWING_KIT = register("sewing_kit", SewingKit::new);

    // ─── 设备方块物品 ──────────────────────────────────────────
    public static final DeferredHolder<Item, Item> EXTRACTION_APPARATUS = blockItem(Blocks.EXTRACTION_APPARATUS);
    public static final DeferredHolder<Item, Item> DISTILLERY_APPARATUS = blockItem(Blocks.DISTILLERY_APPARATUS);
    public static final DeferredHolder<Item, Item> INCUBATOR = blockItem(Blocks.INCUBATOR);
    public static final DeferredHolder<Item, Item> FERMENTATION_BARREL = blockItem(Blocks.FERMENTATION_BARREL);

    // ─── 工具、耗材 ──────────────────────────────────────────
    public static final DeferredHolder<Item, Item> GRINDER = register("grinder", Grinder::new);
    public static final DeferredHolder<Item, Item> UNFILTERED_WHISKEY_BUCKET = register("unfiltered_whiskey_bucket", new Item.Properties().stacksTo(1).craftRemainder(net.minecraft.world.item.Items.BUCKET));
    public static final DeferredHolder<Item, Item> WHISKEY_BUCKET = register("whiskey_bucket", new Item.Properties().stacksTo(1).craftRemainder(net.minecraft.world.item.Items.BUCKET));
    public static final DeferredHolder<Item, Item> PETRI_DISH_EMPTY = register("petri_dish_empty");
    public static final DeferredHolder<Item, Item> PETRI_DISH_AGAR = register("petri_dish_agar", PetriDish::new);
    public static final DeferredHolder<Item, Item> PETRI_DISH_MOLD = register("petri_dish_mold", PetriDish::new);
    public static final DeferredHolder<Item, Item> PETRI_DISH_PENICILLIUM = register("petri_dish_penicillium", PetriDish::new);
    public static final DeferredHolder<Item, Item> PLASTER = register("plaster");
    public static final DeferredHolder<Item, Item> COTTON_FILTER = register("cotton_filter", new Item.Properties().durability(16));
    public static final DeferredHolder<Item, Item> FLASK = register("flask");
    public static final DeferredHolder<Item, Item> QUARTZ_FLASK = register("quartz_flask");
    public static final DeferredHolder<Item, Item> AGAR_BOTTLE = register("agar_bottle", AgarBottle::new);
    public static final DeferredHolder<Item, Item> GLASS_TUBE = register("glass_tube");

    // ─── 粉末、树皮、棉花制品 ──────────────────────────────
    public static final DeferredHolder<Item, Item> POWDER_HERBAL = register("powder_herbal", HerbalPowder::new);
    public static final DeferredHolder<Item, Item> POWDER_SHROOMS = register("powder_shrooms");
    public static final DeferredHolder<Item, Item> POWDER_WOOD = register("powder_wood");
    public static final DeferredHolder<Item, Item> POWDER_CHARCOAL = register("powder_charcoal");
    public static final DeferredHolder<Item, Item> POWDER_COCOA = register("powder_cocoa");
    public static final DeferredHolder<Item, Item> POWDER_KELP = register("powder_kelp");
    public static final DeferredHolder<Item, Item> POWDER_SUGARCANE = register("powder_sugarcane");
    public static final DeferredHolder<Item, Item> POWDER_BEEF = register("powder_beef");
    public static final DeferredHolder<Item, Item> PENICILLIUM = register("penicillium");
    public static final DeferredHolder<Item, Item> PENICILLIUM_COAL = register("penicillium_and_coal");
    public static final DeferredHolder<Item, Item> DISTILLED_LEFTOVERS = register("distilled_leftovers");
    public static final DeferredHolder<Item, Item> BARK = register("bark");
    public static final DeferredHolder<Item, Item> COTTON_CLOTH = register("cotton_cloth");
    public static final DeferredHolder<Item, Item> COTTON_FIBER = register("cotton_fiber");
    public static final DeferredHolder<Item, Item> PLANTAGO_LEAF = register("plantago_leaf");
    public static final DeferredHolder<Item, Item> ALOE_LEAF = register("aloe_leaf");
    public static final DeferredHolder<Item, Item> ALOE_FRUIT = register("aloe_fruit");

    // ─── 食品、花束 ──────────────────────────────────────────
    public static final DeferredHolder<Item, Item> BOUQUET = register("bouquet", Bouquet::new);
    public static final DeferredHolder<Item, Item> BELLADONNA_PIE = blockItem(Blocks.BELLADONNA_PIE);
    public static final DeferredHolder<Item, Item> UNFILTERED_WHISKEY_BOTTLE = register("unfiltered_whiskey_bottle", UnfilteredWhiskeyBottle::new);
    public static final DeferredHolder<Item, Item> WHISKEY_BOTTLE = register("whiskey_bottle", WhiskeyBottle::new);
    public static final DeferredHolder<Item, Item> ALOE_JUICE_BOTTLE = register("aloe_juice_bottle", AloeJuiceBottle::new);
    public static final DeferredHolder<Item, Item> PARASITE_EGGS = register("parasite_eggs", new Item.Properties().food(new FoodProperties.Builder().effect(() -> new MobEffectInstance(Effects.PARASITES, 6000, 0), 1.0f).alwaysEdible().build()));

    // ─── 植物相关物品 ──────────────────────────────────────────
    public static final DeferredHolder<Item, Item> VINCA = blockItem(Blocks.VINCA);
    public static final DeferredHolder<Item, Item> BELLADONNA = blockItem(Blocks.BELLADONNA);
    public static final DeferredHolder<Item, Item> SWEET_CLOVER = doubleBlockItem(Blocks.SWEET_CLOVER);
    public static final DeferredHolder<Item, Item> CHAMOMILE = blockItem(Blocks.CHAMOMILE);
    public static final DeferredHolder<Item, Item> ARTEMISIA = blockItem(Blocks.ARTEMISIA);
    public static final DeferredHolder<Item, Item> OPIUM = blockItem(Blocks.OPIUM);
    public static final DeferredHolder<Item, Item> PLANTAGO = blockItem(Blocks.PLANTAGO);
    public static final DeferredHolder<Item, Item> ALOE = blockItem(Blocks.ALOE);
    public static final DeferredHolder<Item, Item> COTTON = blockItem(Blocks.COTTON);

    // ─── 辅助方法 ──────────────────────────────────────────────
    private static DeferredHolder<Item, Item> register(String name) {
        return register(name, () -> new Item(new Item.Properties()));
    }

    private static DeferredHolder<Item, Item> register(String name, Item.Properties properties) {
        return ITEMS.register(name.toLowerCase(Locale.ROOT), () -> new Item(properties));
    }

    private static DeferredHolder<Item, Item> register(String name, Supplier<Item> item) {
        return ITEMS.register(name.toLowerCase(Locale.ROOT), item);  // ✅ 返回 DeferredHolder
    }

    private static DeferredHolder<Item, Item> blockItem(DeferredHolder<Block, Block> blockHolder) {
        return REGISTRY.register(blockHolder.getId().getPath(),
                () -> new BlockItem(blockHolder.get(), new Item.Properties()));
    }

    private static DeferredHolder<Item, Item> doubleBlockItem(DeferredHolder<Block, Block> blockHolder) {
        return REGISTRY.register(blockHolder.getId().getPath(),
                () -> new DoubleHighBlockItem(blockHolder.get(), new Item.Properties()));
    }
}
