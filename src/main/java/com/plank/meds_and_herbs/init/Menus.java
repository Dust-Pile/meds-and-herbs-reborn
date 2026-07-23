package com.plank.meds_and_herbs.init;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.client.gui.menu.DistilleryApparatusGUIMenu;
import com.plank.meds_and_herbs.client.gui.menu.ExtractApparatusGUIMenu;
import com.plank.meds_and_herbs.client.gui.menu.IncubatorGUIMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class Menus {
    // 只保留一个 DeferredRegister
    public static final DeferredRegister<MenuType<?>> REGISTRY =
            DeferredRegister.create(Registries.MENU, MedsAndHerbs.MODID);

    // 使用 DeferredHolder 而非 Supplier
    public static final DeferredHolder<MenuType<?>, MenuType<ExtractApparatusGUIMenu>> EXTRACTION_APPARATUS_GUI =
            REGISTRY.register("extract_apparatus_gui",
                    () -> new MenuType<>(ExtractApparatusGUIMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<DistilleryApparatusGUIMenu>> DISTILLERY_APPARATUS_GUI =
            REGISTRY.register("distillery_apparatus_gui",
                    () -> new MenuType<>(DistilleryApparatusGUIMenu::new, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredHolder<MenuType<?>, MenuType<IncubatorGUIMenu>> INCUBATOR_GUI = REGISTRY.register("incubator",
            () -> new MenuType<>(IncubatorGUIMenu::new, FeatureFlags.DEFAULT_FLAGS));
}
