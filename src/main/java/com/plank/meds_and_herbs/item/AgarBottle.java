package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.init.Items;
import net.minecraft.world.item.Item;

public class AgarBottle extends Item {
    public AgarBottle() {
        super(new Item.Properties().craftRemainder(Items.DIRTY_MEDICINE_BOTTLE.get()).stacksTo(1));
    }
}
