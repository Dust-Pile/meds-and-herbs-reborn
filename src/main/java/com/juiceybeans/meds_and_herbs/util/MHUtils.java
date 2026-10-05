package com.juiceybeans.meds_and_herbs.util;

import com.juiceybeans.meds_and_herbs.init.MHItems;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MHUtils {
    public static boolean hurtWithCustomType(Entity entity, ResourceKey<DamageType> damageType, float amount) {
        return entity.hurt(getDamageSourceFromType(entity.level(), damageType), amount);
    }

    public static void killWithDamageType(LivingEntity entity, ResourceKey<DamageType> damageType) {
        if (!(entity instanceof Player player && player.isCreative()) && entity.isAlive()) {
            hurtWithCustomType(entity, damageType, Float.MAX_VALUE);
        }
    }

    private static @NotNull DamageSource getDamageSourceFromType(Level entity, ResourceKey<DamageType> damageType) {
        return new DamageSource(entity.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType));
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
        root.put("meds_and_herbs:MedicineData", medicineData);

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
        root.put("meds_and_herbs:MedicineData", medicineData);

        stack.setTag(root);
        return stack;
    }

    /**
     * Entity-agnostic version of {@link net.minecraft.world.item.ItemStack#hurtAndBreak}
     */
    public static void hurtAndBreak(ItemStack stack, int amount, RandomSource random) {
        if (stack.isDamageableItem()) {
            int unbreakingLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.UNBREAKING, stack);
            int reduction = 0;

            for (int i = 0; unbreakingLevel > 0 && i < amount; ++i) {
                if (DigDurabilityEnchantment.shouldIgnoreDurabilityDrop(stack, unbreakingLevel, random)) {
                    ++reduction;
                }
            }

            amount -= reduction;
            if (amount > 0) {
                var newDamage = stack.getDamageValue() + amount;
                if (newDamage >= stack.getMaxDamage()) {
                    stack.shrink(1);
                } else stack.setDamageValue(newDamage);
            }
        }
    }

    public static void loadItemsFromTag(ItemStackHandler itemHandler, CompoundTag tag) {
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }

        if (tag.contains("inventory", Tag.TAG_COMPOUND)) {
            CompoundTag invTag = tag.getCompound("inventory");
            if (invTag.contains("Items", Tag.TAG_LIST)) {
                load(itemHandler, invTag.getList("Items", Tag.TAG_COMPOUND));
                return;
            }
        }

        if (tag.contains("Items", Tag.TAG_LIST)) {
            load(itemHandler, tag.getList("Items", Tag.TAG_COMPOUND));
        }
    }

    public static void load(ItemStackHandler itemHandler, ListTag list) {
        for (int i = 0; i < list.size(); i++) {
            CompoundTag itemTag = list.getCompound(i);
            int slot = itemTag.getInt("Slot");
            if (slot >= 0 && slot < itemHandler.getSlots()) {
                ItemStack stack = ItemStack.of(itemTag);
                itemHandler.setStackInSlot(slot, stack);
            }
        }
    }

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        if (from == to) return shape;
        int rotations = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        VoxelShape result = shape;
        for (int i = 0; i < rotations; i++) {
            result = rotateClockwise(result);
        }
        return result;
    }

    private static VoxelShape rotateClockwise(VoxelShape shape) {
        var boxes = shape.toAabbs();
        VoxelShape result = Shapes.empty();
        for (var box : boxes) {
            double minX = 1 - box.maxZ;
            double minZ = box.minX;
            double maxX = 1 - box.minZ;
            double maxZ = box.maxX;
            VoxelShape rotatedBox = Shapes.box(minX, box.minY, minZ, maxX, box.maxY, maxZ);
            result = Shapes.or(result, rotatedBox);
        }
        return result;
    }
}
