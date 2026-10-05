package com.juiceybeans.meds_and_herbs.effect;


import com.juiceybeans.meds_and_herbs.init.MHDamageTypes;
import com.juiceybeans.meds_and_herbs.init.MHEffects;
import com.juiceybeans.meds_and_herbs.init.MHTags;
import com.juiceybeans.meds_and_herbs.util.MHUtils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class Bleeding extends MobEffect {

    public Bleeding() {
        super(MobEffectCategory.HARMFUL, 0xCC0000);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;

        if (entity.hasEffect(MHEffects.PAINKILLER.get()) || entity.hasEffect(MHEffects.BLOOD_LOSS.get())) return;

        boolean hasInternal = entity.hasEffect(MHEffects.INTERNAL_BLEEDING.get());
        float damage = hasInternal ? 3.0f : 1.0f;
        MHUtils.hurtWithCustomType(entity, MHDamageTypes.BLEEDING, damage);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    @Override
    public List<ItemStack> getCurativeItems() {
        List<ItemStack> items = super.getCurativeItems();
        for (Item item : MHUtils.getItemsFromTag(MHTags.Items.DRESSINGS)) {
            items.add(new ItemStack(item));
        }
        return items;
    }

    public static void onEffectExpired(LivingEntity entity) {
        if (entity.hasEffect(MHEffects.BLOOD_LOSS.get())) MHUtils.killWithDamageType(entity, MHDamageTypes.BLEEDING);
    }
}
