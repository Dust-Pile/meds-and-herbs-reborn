package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.data.*;
import com.plank.meds_and_herbs.init.Items;
import com.plank.meds_and_herbs.init.DataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.effect.MobEffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Medicine extends Item {

    public static final int MAX_USES = 3;

    public Medicine() {
        super(new Item.Properties().stacksTo(1)
                .component(DataComponents.MEDICINE_DATA, new MedicineData(MedsType.HERBAL, MAX_USES))
        );
    }

    @Override
    public boolean hasCraftingRemainingItem(@Nonnull ItemStack stack) {
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        MedicineData data = stack.get(DataComponents.MEDICINE_DATA);
        if (data == null) {
            return new ItemStack(Items.DIRTY_MEDICINE_BOTTLE.get());
        }

        if (data.typeId().equals(MedsType.ETHANOL)) {
            return new ItemStack(Items.MEDICINE_BOTTLE);
        }

        return new ItemStack(Items.DIRTY_MEDICINE_BOTTLE.get());
    }

    public static ItemStack create(ResourceLocation typeId, int uses) {
        ItemStack stack = new ItemStack(Items.MEDICINE.get());
        stack.set(DataComponents.MEDICINE_DATA, new MedicineData(typeId, uses));
        return stack;
    }

    public static boolean isMedicineBottle(ItemStack stack) {
        return stack.getItem() instanceof Medicine;
    }

    public static MedicineData getMedicineData(ItemStack stack) {
        return stack.getOrDefault(DataComponents.MEDICINE_DATA, new MedicineData(MedsType.HERBAL, MAX_USES));
    }

    public static void setMedicineData(ItemStack stack, MedicineData data) {
        stack.set(DataComponents.MEDICINE_DATA, data);
    }

    public static ItemStack consume(ItemStack stack, Player player) {
        if(player.isCreative()) return stack;
        MedicineData bottleData = Medicine.getMedicineData(stack);
        if (bottleData.uses() <= 0) {
            return new ItemStack(Items.MEDICINE_BOTTLE.get());
        }
        int newUses = bottleData.uses() - 1;
        if (newUses == 0) {
            return bottleData.typeId().equals(MedsType.ETHANOL) ? new ItemStack(Items.MEDICINE_BOTTLE.get()) : new  ItemStack(Items.DIRTY_MEDICINE_BOTTLE.get());
        } else {
            setMedicineData(stack, new MedicineData(bottleData.typeId(), newUses));
            return stack;
        }
    }

    public static Component name(ItemStack stack) {
        MedicineData data = getMedicineData(stack);
        String typePath = data.typeId().getPath();
        return Component.translatable("meds_type." + typePath);
    }

    @Override
    @Nonnull
    public Component getName(@Nonnull ItemStack stack) {
        return Component.translatable("item.meds_and_herbs.medicine", name(stack));
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable TooltipContext context, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flag) {
        if (context != null) {
            super.appendHoverText(stack, context, tooltip, flag);
        }

        MedicineData data = getMedicineData(stack);

        ResourceLocation typeId = data.typeId();
        MedicineDefinition def = MedicineTypeLoader.get(typeId);
        if (def == null) return;

        // 内用/外用
        String typeKey = UseMedicine.isInternalMedicine(def.id()) ? "tooltip.medicine.internal" : "tooltip.medicine.external";
        tooltip.add(Component.translatable(typeKey).withStyle(ChatFormatting.GRAY));

        // 可治愈的状态效果
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
                    // 如果效果不存在，显示原始ID
                    name = effectId.toString();
                }
                tooltip.add(Component.literal("  ").append(name).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}