package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.data.*;
import com.plank.meds_and_herbs.init.MHItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Medicine extends Item {

    public static final int MAX_USES = 3;

    public Medicine() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public boolean hasCraftingRemainingItem(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return getType(stack).equals(MedsType.ETHANOL) ? new ItemStack(MHItems.MEDICINE_BOTTLE.get()) : new ItemStack(MHItems.DIRTY_MEDICINE_BOTTLE.get());
    }

    public static ItemStack create(ResourceLocation typeId, int uses) {
        ItemStack stack = new ItemStack(MHItems.MEDICINE.get());
        setMedicineData(stack, typeId, uses);
        return stack;
    }

    public static ResourceLocation getType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("MedicineData", CompoundTag.TAG_COMPOUND)) {
            return MedsType.HERBAL;
        }
        String raw = tag.getCompound("MedicineData").getString("type");
        ResourceLocation id = ResourceLocation.tryParse(raw);
        return id != null ? id : MedsType.HERBAL;
    }

    public static int getUses(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("MedicineData", CompoundTag.TAG_COMPOUND)) {
            return MAX_USES;
        }
        return tag.getCompound("MedicineData").getInt("uses");
    }

    public static void setMedicineData(ItemStack stack, ResourceLocation typeId, int uses) {
        CompoundTag data = new CompoundTag();
        data.putString("type", typeId.toString());
        data.putInt("MedicineData", uses);
        stack.getOrCreateTag().put("key", data);
    }

    public static boolean isMedicineBottle(ItemStack stack) {
        return stack.getItem() instanceof Medicine;
    }

    public static ItemStack consume(ItemStack stack, Player player) {
        if(player.isCreative()) return stack;
        if (getUses(stack) <= 0) {
            return new ItemStack(MHItems.MEDICINE_BOTTLE.get());
        }
        int newUses = getUses(stack) - 1;
        if (newUses == 0) {
            return getType(stack).equals(MedsType.ETHANOL) ? new ItemStack(MHItems.MEDICINE_BOTTLE.get()) : new  ItemStack(MHItems.DIRTY_MEDICINE_BOTTLE.get());
        } else {
            setMedicineData(stack, getType(stack), newUses);
            return stack;
        }
    }

    public static Component getTypeName(ItemStack stack) {
        return Component.translatable("meds_type." + getType(stack).getPath());
    }

    @Override
    @Nonnull
    public Component getName(@Nonnull ItemStack stack) {
        return Component.translatable("item.meds_and_herbs.medicine", getTypeName(stack));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level level, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        if (level != null) {
            super.appendHoverText(stack, level, tooltip, flag);
        }

        ResourceLocation typeId = getType(stack);
        MedicineDefinition def = MedicineTypeLoader.get(typeId);
        if (def == null) return;

        String typeKey = def.isInternal() ? "tooltip.medicine.internal" : "tooltip.medicine.external";
        tooltip.add(Component.translatable(typeKey).withStyle(ChatFormatting.GRAY));

        List<ResourceLocation> cures = def.cures();
        if (cures != null && !cures.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.medicine.treat")
                    .withStyle(ChatFormatting.GOLD));
            for (ResourceLocation effectId : cures) {
                MobEffect effect = BuiltInRegistries.MOB_EFFECT.get(effectId);
                String name;
                if (effect != null) {
                    name = effect.getDisplayName().getString();
                } else {
                    name = effectId.toString();
                }
                tooltip.add(Component.literal("  ").append(name).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}