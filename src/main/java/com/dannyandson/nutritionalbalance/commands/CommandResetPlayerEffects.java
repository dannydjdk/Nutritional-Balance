package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.effects.ModMobAffects;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;

public class CommandResetPlayerEffects implements Command<CommandSource> {

    private static final CommandResetPlayerEffects CMD = new CommandResetPlayerEffects();

    @Override
    public int run(CommandContext<CommandSource> context) {

        if (context.getSource().getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)context.getSource().getEntity();
            ModMobAffects.resetPlayerEffects(player);
        }
        return 0;
    }

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("reset_player_effect")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }
}
