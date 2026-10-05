package com.juiceybeans.meds_and_herbs.item;

import com.juiceybeans.meds_and_herbs.init.MHEffects;
import com.juiceybeans.meds_and_herbs.init.MHItems;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class UnfilteredWhiskeyBottle extends Item {

    public UnfilteredWhiskeyBottle() {
        super(new Item.Properties()
                .stacksTo(16)
                .craftRemainder(MHItems.DIRTY_MEDICINE_BOTTLE.get())
                .food(new FoodProperties.Builder()
                        .nutrition(4)
                        .saturationMod(0.5f)
                        .alwaysEat()
                        .build()));
    }

    @Override
    @Nonnull
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity livingEntity) {
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide && livingEntity instanceof Player player) {
            if (player.getRandom().nextFloat() < 0.2f) {
                player.addEffect(new MobEffectInstance(MHEffects.METHANOL_POISONING.get(), 12000, 0));
            } else {
                player.addEffect(new MobEffectInstance(MHEffects.BEVERAGE_DRINK.get(), 300, 0));
            }
            if (!player.isCreative()) {
                return new ItemStack(MHItems.DIRTY_MEDICINE_BOTTLE.get());
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