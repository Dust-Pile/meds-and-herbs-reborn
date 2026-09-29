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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;
import java.util.function.Supplier;

import static com.plank.meds_and_herbs.MedsAndHerbs.MODID;

public class Items {
    public static final DeferredRegister<Item> REGISTRY =
            DeferredRegister.create(Registries.ITEM, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, MODID);

    public static final RegistryObject<Item> SYRINGE = register("syringe", Syringe::new);
    public static final RegistryObject<Item> MEDICINE_BOTTLE = register("medicine_bottle", new Item.Properties().stacksTo(16));
    public static final RegistryObject<Item> DIRTY_MEDICINE_BOTTLE = register("dirty_medicine_bottle", new Item.Properties().stacksTo(16));

    public static final RegistryObject<Item> MEDICINE = register("medicine", Medicine::new);

    public static final RegistryObject<Item> PLANTAGO_DRESSING = register("plantago_dressing", PlantagoDressing::new);
    public static final RegistryObject<Item> WOOL_DRESSING = register("wool_dressing", WoolDressing::new);
    public static final RegistryObject<Item> COTTON_DRESSING = register("cotton_dressing", CottonDressing::new);
    public static final RegistryObject<Item> SPLINT = register("splint", Splint::new);
    public static final RegistryObject<Item> MEDKIT = register("medkit", Medkit::new);
    public static final RegistryObject<Item> SEWING_KIT = register("sewing_kit", SewingKit::new);

    public static final RegistryObject<Item> EXTRACTION_APPARATUS = blockItem(Blocks.EXTRACTION_APPARATUS);
    public static final RegistryObject<Item> DISTILLERY_APPARATUS = blockItem(Blocks.DISTILLERY_APPARATUS);
    public static final RegistryObject<Item> INCUBATOR = blockItem(Blocks.INCUBATOR);
    public static final RegistryObject<Item> FERMENTATION_BARREL = blockItem(Blocks.FERMENTATION_BARREL);

    public static final RegistryObject<Item> GRINDER = register("grinder", Grinder::new);
    public static final RegistryObject<Item> UNFILTERED_WHISKEY_BUCKET = register("unfiltered_whiskey_bucket", new Item.Properties().stacksTo(1).craftRemainder(net.minecraft.world.item.Items.BUCKET));
    public static final RegistryObject<Item> WHISKEY_BUCKET = register("whiskey_bucket", new Item.Properties().stacksTo(1).craftRemainder(net.minecraft.world.item.Items.BUCKET));
    public static final RegistryObject<Item> PETRI_DISH_EMPTY = register("petri_dish_empty");
    public static final RegistryObject<Item> PETRI_DISH_AGAR = register("petri_dish_agar", PetriDish::new);
    public static final RegistryObject<Item> PETRI_DISH_MOLD = register("petri_dish_mold", PetriDish::new);
    public static final RegistryObject<Item> PETRI_DISH_PENICILLIUM = register("petri_dish_penicillium", PetriDish::new);
    public static final RegistryObject<Item> PLASTER = register("plaster");
    public static final RegistryObject<Item> COTTON_FILTER = register("cotton_filter", new Item.Properties().durability(16));
    public static final RegistryObject<Item> FLASK = register("flask");
    public static final RegistryObject<Item> QUARTZ_FLASK = register("quartz_flask");
    public static final RegistryObject<Item> AGAR_BOTTLE = register("agar_bottle", AgarBottle::new);
    public static final RegistryObject<Item> GLASS_TUBE = register("glass_tube");

    public static final RegistryObject<Item> POWDER_HERBAL = register("powder_herbal", HerbalPowder::new);
    public static final RegistryObject<Item> POWDER_SHROOMS = register("powder_shrooms");
    public static final RegistryObject<Item> POWDER_WOOD = register("powder_wood");
    public static final RegistryObject<Item> POWDER_CHARCOAL = register("powder_charcoal");
    public static final RegistryObject<Item> POWDER_COCOA = register("powder_cocoa");
    public static final RegistryObject<Item> POWDER_KELP = register("powder_kelp");
    public static final RegistryObject<Item> POWDER_SUGARCANE = register("powder_sugarcane");
    public static final RegistryObject<Item> POWDER_BEEF = register("powder_beef");
    public static final RegistryObject<Item> PENICILLIUM = register("penicillium");
    public static final RegistryObject<Item> PENICILLIUM_COAL = register("penicillium_and_coal");
    public static final RegistryObject<Item> DISTILLED_LEFTOVERS = register("distilled_leftovers");
    public static final RegistryObject<Item> BARK = register("bark");
    public static final RegistryObject<Item> COTTON_CLOTH = register("cotton_cloth");
    public static final RegistryObject<Item> COTTON_FIBER = register("cotton_fiber");
    public static final RegistryObject<Item> PLANTAGO_LEAF = register("plantago_leaf");
    public static final RegistryObject<Item> ALOE_LEAF = register("aloe_leaf");
    public static final RegistryObject<Item> ALOE_FRUIT = register("aloe_fruit");

    public static final RegistryObject<Item> BOUQUET = register("bouquet", Bouquet::new);
    public static final RegistryObject<Item> BELLADONNA_PIE = blockItem(Blocks.BELLADONNA_PIE);
    public static final RegistryObject<Item> UNFILTERED_WHISKEY_BOTTLE = register("unfiltered_whiskey_bottle", UnfilteredWhiskeyBottle::new);
    public static final RegistryObject<Item> WHISKEY_BOTTLE = register("whiskey_bottle", WhiskeyBottle::new);
    public static final RegistryObject<Item> ALOE_JUICE_BOTTLE = register("aloe_juice_bottle", AloeJuiceBottle::new);
    public static final RegistryObject<Item> PARASITE_EGGS = register("parasite_eggs", new Item.Properties().food(new FoodProperties.Builder().effect(() -> new MobEffectInstance(Effects.PARASITES, 6000, 0), 1.0f).alwaysEdible().build()));

    public static final RegistryObject<Item> VINCA = blockItem(Blocks.VINCA);
    public static final RegistryObject<Item> BELLADONNA = blockItem(Blocks.BELLADONNA);
    public static final RegistryObject<Item> SWEET_CLOVER = doubleBlockItem(Blocks.SWEET_CLOVER);
    public static final RegistryObject<Item> CHAMOMILE = blockItem(Blocks.CHAMOMILE);
    public static final RegistryObject<Item> ARTEMISIA = blockItem(Blocks.ARTEMISIA);
    public static final RegistryObject<Item> OPIUM = blockItem(Blocks.OPIUM);
    public static final RegistryObject<Item> PLANTAGO = blockItem(Blocks.PLANTAGO);
    public static final RegistryObject<Item> ALOE = blockItem(Blocks.ALOE);
    public static final RegistryObject<Item> COTTON = blockItem(Blocks.COTTON);

    private static RegistryObject<Item> register(String name) {
        return register(name, () -> new Item(new Item.Properties()));
    }

    private static RegistryObject<Item> register(String name, Item.Properties properties) {
        return ITEMS.register(name.toLowerCase(Locale.ROOT), () -> new Item(properties));
    }

    private static RegistryObject<Item> register(String name, Supplier<Item> item) {
        return ITEMS.register(name.toLowerCase(Locale.ROOT), item);
    }

    private static RegistryObject<Item> blockItem(RegistryObject<Block> block) {
        return REGISTRY.register(block.getId().getPath(),
                () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private static RegistryObject<Item> doubleBlockItem(RegistryObject<Block> block) {
        return REGISTRY.register(block.getId().getPath(),
                () -> new DoubleHighBlockItem(block.get(), new Item.Properties()));
    }
}
