package com.plank.meds_and_herbs.util;

import com.plank.meds_and_herbs.init.MHItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class MHUtils {
    public static boolean hurtWithCustomType(Entity entity, ResourceKey<DamageType> damageType, float amount) {
        return entity.hurt(new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType)), amount);
    }

    public static List<Item> getItemsFromTag(TagKey<Item> tag) {
        return ForgeRegistries.ITEMS.tags().getTag(tag).stream().toList();
    }

    public static ItemStack getMedicineStackFromType(String type) {
        return getMedicineStackFromType(type, 3);
    }

    public static ItemStack getMedicineStackFromType(String type, int uses) {
        ItemStack stack = new ItemStack(MHItems.MEDICINE.get());

        CompoundTag medicineData = new CompoundTag();
        medicineData.putString("type", "meds_and_herbs:" + type);
        medicineData.putInt("uses", uses);

        CompoundTag root = new CompoundTag();
        root.put("meds_and_herbs:medicine_data", medicineData);

        stack.setTag(root);
        return stack;
    }

    /**
     * Doesn't add a uses NBT tag to medicine data
     * @param type medicine type
     * @return Medicine stack with only the type
     */
    public static ItemStack getMedicineStackFromTypeNoUses(String type) {
        ItemStack stack = new ItemStack(MHItems.MEDICINE.get());

        CompoundTag medicineData = new CompoundTag();
        medicineData.putString("type", "meds_and_herbs:" + type);

        CompoundTag root = new CompoundTag();
        root.put("meds_and_herbs:medicine_data", medicineData);

        stack.setTag(root);
        return stack;
    }
}
