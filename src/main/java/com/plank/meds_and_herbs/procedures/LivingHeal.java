package com.plank.meds_and_herbs.procedures;

import com.plank.meds_and_herbs.MedsAndHerbs;
import com.plank.meds_and_herbs.init.Effects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

/**
 * 监听实体回血事件，若实体拥有血失效果则取消回血。
 */
@EventBusSubscriber(modid = MedsAndHerbs.MODID)
public class LivingHeal {
    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        var entity = event.getEntity();
        if (entity instanceof Player player && player.isCreative()) return;
        if (entity.hasEffect(Effects.BLOOD_LOSS)) {
            event.setCanceled(true);
        }
        if (entity.hasEffect(Effects.PAINKILLER)) {
            event.setCanceled(true);
            // 记录被拦截的治疗量
            var data = entity.getPersistentData();
            double current = data.getDouble("PainkillerDamageTaken");
            data.putDouble("PainkillerDamageTaken", current - event.getAmount());
        }

    }
}