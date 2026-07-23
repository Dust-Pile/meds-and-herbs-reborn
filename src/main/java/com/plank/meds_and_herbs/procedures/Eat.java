package com.plank.meds_and_herbs.procedures;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.init.DamageTypes;
import com.plank.meds_and_herbs.init.Effects;
import com.plank.meds_and_herbs.init.Tags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@EventBusSubscriber(modid = MedsAndHerbs.MODID)
public class Eat {


    @SubscribeEvent
    public static void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        ItemStack stack = event.getItem();

        // 仅在服务端执行
        if (entity.level().isClientSide) return;

        // 创造模式玩家不受影响
        if (entity instanceof Player player && player.isCreative()) return;
        {
            // 检查是否属于生肉标签
            if (stack.is(Tags.Items.RAW_MEAT)) {
                // 如果已有寄生虫效果，直接造成伤害
                if (entity.hasEffect(Effects.PARASITES)) {
                    entity.hurt(entity.level().damageSources().source(DamageTypes.PARASITES), 2.0f);
                } else if (entity.getRandom().nextFloat() < 0.2) {
                    entity.addEffect(new MobEffectInstance(Effects.PARASITES, 24000, 0, false, false));
                }
            }
        }

        {
            if (stack.is(Tags.Items.MUSHROOM_STEW) && entity.getRandom().nextFloat() < 0.01f) {
                entity.addEffect(new MobEffectInstance(Effects.MUSHROOM_POISONING, 2400, 0, false, false));
            }
        }
    }
}
