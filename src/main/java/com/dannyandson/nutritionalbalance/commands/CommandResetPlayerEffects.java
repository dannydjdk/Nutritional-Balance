package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.effects.ModMobAffects;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class CommandResetPlayerEffects implements Command<CommandSourceStack> {

    private static final CommandResetPlayerEffects CMD = new CommandResetPlayerEffects();

    @Override
    public int run(CommandContext<CommandSourceStack> context) {

        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            ModMobAffects.resetPlayerEffects(player);
        }
        return 0;
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("reset_player_effect")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }
}
