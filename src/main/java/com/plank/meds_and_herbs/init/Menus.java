package com.plank.meds_and_herbs.init;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.client.gui.menu.DistilleryApparatusGUIMenu;
import com.plank.meds_and_herbs.client.gui.menu.ExtractApparatusGUIMenu;
import com.plank.meds_and_herbs.client.gui.menu.IncubatorGUIMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Menus {
    public static final DeferredRegister<MenuType<?>> REGISTRY =
            DeferredRegister.create(Registries.MENU, MedsAndHerbs.MODID);

    public static final RegistryObject<MenuType<ExtractApparatusGUIMenu>> EXTRACTION_APPARATUS_GUI =
            REGISTRY.register("extract_apparatus_gui",
                    () -> new MenuType<>(ExtractApparatusGUIMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final RegistryObject<MenuType<DistilleryApparatusGUIMenu>> DISTILLERY_APPARATUS_GUI =
            REGISTRY.register("distillery_apparatus_gui",
                    () -> new MenuType<>(DistilleryApparatusGUIMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final RegistryObject<MenuType<IncubatorGUIMenu>> INCUBATOR_GUI = REGISTRY.register("incubator",
            () -> new MenuType<>(IncubatorGUIMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
