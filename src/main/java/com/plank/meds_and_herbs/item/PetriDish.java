package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.init.Items;
import net.minecraft.world.item.Item;

public class PetriDish extends Item {
    public PetriDish() {
        super(new Properties().craftRemainder(Items.PETRI_DISH_EMPTY.get()));
    }
}
