package com.dusty_dusty.meds_and_herbs.item;

import com.dusty_dusty.meds_and_herbs.init.MHItems;
import net.minecraft.world.item.Item;

public class AgarBottle extends Item {
    public AgarBottle() {
        super(new Item.Properties().craftRemainder(MHItems.DIRTY_MEDICINE_BOTTLE.get()).stacksTo(1));
    }
}
