package com.plank.meds_and_herbs.init;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.data.BouquetFlowers;
import com.plank.meds_and_herbs.data.MedkitContents;
import com.plank.meds_and_herbs.data.MedicineData;
import com.plank.meds_and_herbs.data.PetriDishData;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// 2. 在注册类中注册 ComponentType
public class DataComponents {
    public static final DeferredRegister<DataComponentType<?>> REGISTRY =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MedsAndHerbs.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MedicineData>> MEDICINE_DATA =
            REGISTRY.register("medicine_data", MedicineData::createComponentType);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MedkitContents>> MEDKIT_CONTENTS =
            REGISTRY.register("medkit_contents", MedkitContents::createComponentType);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BouquetFlowers>> BOUQUET_FLOWERS =
            REGISTRY.register("bouquet_flowers", BouquetFlowers::createComponentType);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PetriDishData>> PETRI_DISH_DATA =
            REGISTRY.register("petri_dish_data", PetriDishData::createComponentType);
}
