package com.dusty_dusty.meds_and_herbs.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class AloeJuiceBottle extends Item {
    public AloeJuiceBottle() {
        super(new Item.Properties()
                .stacksTo(16)
                .craftRemainder(Items.GLASS_BOTTLE)
                .food(new FoodProperties.Builder()
                        .nutrition(1)
                        .saturationMod(1.0f)
                        .alwaysEat()
                        .build()));
    }
    @Override
    @Nonnull
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide && livingEntity instanceof Player player) {
            if (!player.isCreative()) {
                return new ItemStack(Items.GLASS_BOTTLE);
            }
        }
        return result;
    }

    @Override
    @Nonnull
    public UseAnim getUseAnimation(@Nonnull ItemStack stack) {
        return UseAnim.DRINK;
    }
}
