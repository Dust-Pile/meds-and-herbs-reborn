package com.dusty_dusty.meds_and_herbs.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class RandomCommand {

    private static final SimpleCommandExceptionType ERROR_RANGE_TOO_LARGE =
            new SimpleCommandExceptionType(Component.translatable("commands.random.error.range_too_large"));
    private static final SimpleCommandExceptionType ERROR_RANGE_TOO_SMALL =
            new SimpleCommandExceptionType(Component.translatable("commands.random.error.range_too_small"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("random").then(drawRandomValueTree("value", false))
                        .then(drawRandomValueTree("roll", true)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> drawRandomValueTree(String subcommand, boolean displayResult) {
        return Commands.literal(subcommand)
                .then(Commands.argument("range", RangeArgument.intRange())
                        .executes(ctx -> randomSample(
                                ctx.getSource(),
                                RangeArgument.Ints.getRange(ctx, "range"),
                                displayResult)));
    }

    private static int randomSample(CommandSourceStack source, MinMaxBounds.Ints range, boolean displayResult)
            throws CommandSyntaxException {
        RandomSource random = source.getLevel().getRandom();

        int min = range.getMin() == null ? Integer.MIN_VALUE : range.getMin();
        int max = range.getMax() == null ? Integer.MAX_VALUE : range.getMax();
        long span = (long) max - (long) min;
        if (span == 0L) {
            throw ERROR_RANGE_TOO_SMALL.create();
        }
        if (span >= 2147483647L) {
            throw ERROR_RANGE_TOO_LARGE.create();
        }

        int value = Mth.randomBetweenInclusive(random, min, max);

        if (displayResult) {
            source.getServer().getPlayerList().broadcastSystemMessage(
                    Component.translatable("commands.random.roll", source.getDisplayName(), value, min, max),
                    false);
        } else {
            final int result = value;
            source.sendSuccess(
                    () -> Component.translatable("commands.random.sample.success", result),
                    false);
        }

        return value;
    }
}