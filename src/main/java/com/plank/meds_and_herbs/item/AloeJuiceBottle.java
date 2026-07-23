package com.plank.meds_and_herbs.item;

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
            // 返回玻璃瓶（前提：非创造模式且物品被消耗）
            if (!player.hasInfiniteMaterials()) {
                return new ItemStack(Items.GLASS_BOTTLE);
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
