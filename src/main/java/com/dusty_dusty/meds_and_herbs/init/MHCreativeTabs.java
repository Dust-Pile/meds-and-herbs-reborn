package com.dusty_dusty.meds_and_herbs.init;

import com.dusty_dusty.meds_and_herbs.MedsAndHerbs;
import com.dusty_dusty.meds_and_herbs.data.MedicineDefinition;
import com.dusty_dusty.meds_and_herbs.data.MedicineTypeLoader;
import com.dusty_dusty.meds_and_herbs.item.Medicine;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class MHCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MedsAndHerbs.MODID);

    public static final RegistryObject<CreativeModeTab> MEDS_AND_HERBS_TAB =
            REGISTRY.register("meds_and_herbs_tab",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.meds_and_herbs"))
                            .icon(() -> new ItemStack(MHItems.MEDICINE.get()))
                            .displayItems((parameters, output) -> {
                                output.accept(MHItems.EXTRACTION_APPARATUS.get());
                                output.accept(MHItems.DISTILLERY_APPARATUS.get());
                                output.accept(MHItems.INCUBATOR.get());
                                output.accept(MHItems.FERMENTATION_BARREL.get());

                                output.accept(MHItems.VINCA.get());
                                output.accept(MHItems.BELLADONNA.get());
                                output.accept(MHItems.SWEET_CLOVER.get());
                                output.accept(MHItems.CHAMOMILE.get());
                                output.accept(MHItems.ARTEMISIA.get());
                                output.accept(MHItems.OPIUM.get());
                                output.accept(MHItems.PLANTAGO.get());
                                output.accept(MHItems.ALOE.get());
                                output.accept(MHItems.COTTON.get());

                                output.accept(MHItems.BELLADONNA_PIE.get());
                                output.accept(MHItems.BOUQUET.get());

                                output.accept(MHItems.PETRI_DISH_EMPTY.get());
                                output.accept(MHItems.PETRI_DISH_AGAR.get());
                                output.accept(MHItems.PETRI_DISH_MOLD.get());
                                output.accept(MHItems.PETRI_DISH_PENICILLIUM.get());

                                for (MedicineDefinition def : MedicineTypeLoader.getAllDefinitions()) {
                                    output.accept(Medicine.create(def.id(), Medicine.MAX_USES));
                                }

                                output.accept(MHItems.SYRINGE.get());
                                output.accept(MHItems.MEDICINE_BOTTLE.get());
                                output.accept(MHItems.DIRTY_MEDICINE_BOTTLE.get());
                                output.accept(MHItems.PLANTAGO_DRESSING.get());
                                output.accept(MHItems.WOOL_DRESSING.get());
                                output.accept(MHItems.COTTON_DRESSING.get());
                                output.accept(MHItems.SPLINT.get());
                                output.accept(MHItems.MEDKIT.get());
                                output.accept(MHItems.SEWING_KIT.get());
                                output.accept(MHItems.GRINDER.get());
                                output.accept(MHItems.UNFILTERED_WHISKEY_BUCKET.get());
                                output.accept(MHItems.WHISKEY_BUCKET.get());
                                output.accept(MHItems.PLASTER.get());
                                output.accept(MHItems.COTTON_FILTER.get());
                                output.accept(MHItems.FLASK.get());
                                output.accept(MHItems.QUARTZ_FLASK.get());
                                output.accept(MHItems.GLASS_TUBE.get());
                                output.accept(MHItems.AGAR_BOTTLE.get());

                                output.accept(MHItems.POWDER_HERBAL.get());
                                output.accept(MHItems.POWDER_SHROOMS.get());
                                output.accept(MHItems.POWDER_WOOD.get());
                                output.accept(MHItems.POWDER_CHARCOAL.get());
                                output.accept(MHItems.POWDER_COCOA.get());
                                output.accept(MHItems.POWDER_KELP.get());
                                output.accept(MHItems.POWDER_SUGARCANE.get());
                                output.accept(MHItems.POWDER_BEEF.get());
                                output.accept(MHItems.PENICILLIUM.get());
                                output.accept(MHItems.PENICILLIUM_COAL.get());
                                output.accept(MHItems.DISTILLED_LEFTOVERS.get());
                                output.accept(MHItems.BARK.get());
                                output.accept(MHItems.COTTON_CLOTH.get());
                                output.accept(MHItems.COTTON_FIBER.get());
                                output.accept(MHItems.PLANTAGO_LEAF.get());
                                output.accept(MHItems.ALOE_LEAF.get());
                                output.accept(MHItems.ALOE_FRUIT.get());

                                output.accept(MHItems.UNFILTERED_WHISKEY_BOTTLE.get());
                                output.accept(MHItems.WHISKEY_BOTTLE.get());
                                output.accept(MHItems.ALOE_JUICE_BOTTLE.get());
                                output.accept(MHItems.PARASITE_EGGS.get());
                            })
                            .build()
            );

}