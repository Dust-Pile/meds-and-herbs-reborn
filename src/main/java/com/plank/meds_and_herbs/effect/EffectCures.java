package com.plank.meds_and_herbs.effect;

import com.plank.meds_and_herbs.init.MHItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class EffectCures {

    private EffectCures() {}

    public static boolean matchesType(ItemStack stack, String typeId) {
        if (stack.isEmpty() || !stack.is(MHItems.MEDICINE.get())) return false;

        CompoundTag tag = stack.getTag();
        if (tag == null) return false;
        if (!tag.contains("MedicineData", CompoundTag.TAG_COMPOUND)) return false;

        CompoundTag data = tag.getCompound("MedicineData");
        return typeId.equals(data.getString("type"));
    }

    public static void cure(LivingEntity entity, ItemStack cureItem) {
        if (entity.level().isClientSide) return;
        if (cureItem.isEmpty()) return;

        List<MobEffect> toRemove = new ArrayList<>();
        for (MobEffectInstance instance : entity.getActiveEffects()) {
            for (ItemStack curative : instance.getEffect().getCurativeItems()) {
                if (ItemStack.isSameItem(curative, cureItem)) {
                    toRemove.add(instance.getEffect());
                    break;
                }
            }
        }
        for (MobEffect effect : toRemove) {
            entity.removeEffect(effect);
        }
    }
}