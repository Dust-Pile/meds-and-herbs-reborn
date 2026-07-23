package com.plank.meds_and_herbs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class HealCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("heal")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("target", EntityArgument.entity())
                                .then(Commands.argument("amount", FloatArgumentType.floatArg(0.0f, Float.MAX_VALUE))
                                        .executes(ctx -> healEntity(ctx,
                                                EntityArgument.getEntity(ctx, "target"),
                                                FloatArgumentType.getFloat(ctx, "amount")
                                        ))
                                )
                        )
        );
    }

    private static int healEntity(CommandContext<CommandSourceStack> ctx, Entity target, float amount) {
        if (!(target instanceof LivingEntity living)) {
            ctx.getSource().sendFailure(Component.translatable("command.meds_and_herbs.heal.not_living"));
            return 0;
        }

        float currentHealth = living.getHealth();
        float maxHealth = living.getMaxHealth();
        float newHealth = Math.min(currentHealth + amount, maxHealth);
        float healed = newHealth - currentHealth;

        if (healed <= 0) {
            ctx.getSource().sendFailure(Component.translatable("command.meds_and_herbs.heal.full"));
            return 0;
        }

        living.setHealth(newHealth);

        ctx.getSource().sendSuccess(() ->
                        Component.translatable("command.meds_and_herbs.heal.success",
                                living.getDisplayName(),
                                healed
                        ),
                true
        );
        return 1;
    }
}