package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.PlayerSync;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.ResourceLocation;

public class CommandSyncClient implements Command<CommandSource> {

    private static final CommandSyncClient CMD = new CommandSyncClient();

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        final PlayerSync[] playerSync = new PlayerSync[1];
        context.getSource().getEntity().getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(inutritionalbalancePlayer -> {
            playerSync[0] = new PlayerSync(new ResourceLocation(NutritionalBalance.MODID, "playersync"),inutritionalbalancePlayer);
        });
        ModNetworkHandler.sendToClient(playerSync[0], context.getSource().asPlayer());
        return 0;
    }

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("sync")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }
}
