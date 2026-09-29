package com.plank.meds_and_herbs.init;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.data.MedicineDefinition;
import com.plank.meds_and_herbs.data.MedicineTypeLoader;
import com.plank.meds_and_herbs.item.Medicine;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MedsAndHerbs.MODID);

    public static final RegistryObject<CreativeModeTab> MEDS_AND_HERBS_TAB =
            REGISTRY.register("meds_and_herbs_tab",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable("itemGroup.meds_and_herbs"))
                            .icon(() -> new ItemStack(Items.MEDICINE.get()))
                            .displayItems((parameters, output) -> {
                                output.accept(Items.EXTRACTION_APPARATUS.get());
                                output.accept(Items.DISTILLERY_APPARATUS.get());
                                output.accept(Items.INCUBATOR.get());
                                output.accept(Items.FERMENTATION_BARREL.get());

                                output.accept(Items.VINCA.get());
                                output.accept(Items.BELLADONNA.get());
                                output.accept(Items.SWEET_CLOVER.get());
                                output.accept(Items.CHAMOMILE.get());
                                output.accept(Items.ARTEMISIA.get());
                                output.accept(Items.OPIUM.get());
                                output.accept(Items.PLANTAGO.get());
                                output.accept(Items.ALOE.get());
                                output.accept(Items.COTTON.get());

                                output.accept(Items.BELLADONNA_PIE.get());
                                output.accept(Items.BOUQUET.get());

                                output.accept(Items.PETRI_DISH_EMPTY.get());
                                output.accept(Items.PETRI_DISH_AGAR.get());
                                output.accept(Items.PETRI_DISH_MOLD.get());
                                output.accept(Items.PETRI_DISH_PENICILLIUM.get());

                                for (MedicineDefinition def : MedicineTypeLoader.getAllDefinitions()) {
                                    ItemStack stack = new ItemStack(Items.MEDICINE.get());
                                    CompoundTag medicineData = new CompoundTag();
                                    medicineData.putString("id", def.id().toString());
                                    medicineData.putInt("uses", Medicine.MAX_USES);
                                    stack.setTag(medicineData);
                                    output.accept(stack);
                                }

                                output.accept(Items.SYRINGE.get());
                                output.accept(Items.MEDICINE_BOTTLE.get());
                                output.accept(Items.DIRTY_MEDICINE_BOTTLE.get());
                                output.accept(Items.PLANTAGO_DRESSING.get());
                                output.accept(Items.WOOL_DRESSING.get());
                                output.accept(Items.COTTON_DRESSING.get());
                                output.accept(Items.SPLINT.get());
                                output.accept(Items.MEDKIT.get());
                                output.accept(Items.SEWING_KIT.get());
                                output.accept(Items.GRINDER.get());
                                output.accept(Items.UNFILTERED_WHISKEY_BUCKET.get());
                                output.accept(Items.WHISKEY_BUCKET.get());
                                output.accept(Items.PLASTER.get());
                                output.accept(Items.COTTON_FILTER.get());
                                output.accept(Items.FLASK.get());
                                output.accept(Items.QUARTZ_FLASK.get());
                                output.accept(Items.GLASS_TUBE.get());
                                output.accept(Items.AGAR_BOTTLE.get());

                                output.accept(Items.POWDER_HERBAL.get());
                                output.accept(Items.POWDER_SHROOMS.get());
                                output.accept(Items.POWDER_WOOD.get());
                                output.accept(Items.POWDER_CHARCOAL.get());
                                output.accept(Items.POWDER_COCOA.get());
                                output.accept(Items.POWDER_KELP.get());
                                output.accept(Items.POWDER_SUGARCANE.get());
                                output.accept(Items.POWDER_BEEF.get());
                                output.accept(Items.PENICILLIUM.get());
                                output.accept(Items.PENICILLIUM_COAL.get());
                                output.accept(Items.DISTILLED_LEFTOVERS.get());
                                output.accept(Items.BARK.get());
                                output.accept(Items.COTTON_CLOTH.get());
                                output.accept(Items.COTTON_FIBER.get());
                                output.accept(Items.PLANTAGO_LEAF.get());
                                output.accept(Items.ALOE_LEAF.get());
                                output.accept(Items.ALOE_FRUIT.get());

                                output.accept(Items.UNFILTERED_WHISKEY_BOTTLE.get());
                                output.accept(Items.WHISKEY_BOTTLE.get());
                                output.accept(Items.ALOE_JUICE_BOTTLE.get());
                                output.accept(Items.PARASITE_EGGS.get());
                            })
                            .build()
            );

}