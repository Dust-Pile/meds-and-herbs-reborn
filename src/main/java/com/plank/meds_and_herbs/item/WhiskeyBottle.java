package com.plank.meds_and_herbs.item;

import com.plank.meds_and_herbs.init.Effects;
import com.plank.meds_and_herbs.init.Items;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

public class WhiskeyBottle extends Item {

    public WhiskeyBottle() {
        super(new Item.Properties()
                .stacksTo(16)
                .craftRemainder(Items.DIRTY_MEDICINE_BOTTLE.get())
                .food(new FoodProperties.Builder()
                        .nutrition(4)
                        .saturationModifier(1.0f)
                        .alwaysEdible()
                        .build()));
    }

    @Override
    @Nonnull
    public ItemStack finishUsingItem(@Nonnull ItemStack stack, @Nonnull Level level, @Nonnull LivingEntity livingEntity) {
        // 先应用原版食物效果（恢复饥饿、饱和度）
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);
        if (!level.isClientSide && livingEntity instanceof Player player) {
            // 给予饮料效果 30 秒
            player.addEffect(new MobEffectInstance(Effects.BEVERAGE_DRINK, 600, 0));
            // 返回玻璃瓶（前提：非创造模式且物品被消耗）
            if (!player.hasInfiniteMaterials()) {
                return new ItemStack(Items.DIRTY_MEDICINE_BOTTLE.get());
            }
        }
        // 如果玩家是创造模式，返回剩下的 stack（可能为空）
        return result;
    }

    @Override
    @Nonnull
    public UseAnim getUseAnimation(@Nonnull ItemStack stack) {
        return UseAnim.DRINK;
    }
}