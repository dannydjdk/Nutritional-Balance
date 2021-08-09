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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;

public class CommandSyncClient implements Command<CommandSourceStack> {

    private static final CommandSyncClient CMD = new CommandSyncClient();

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final PlayerSync[] playerSync = new PlayerSync[1];
        context.getSource().getEntity().getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(inutritionalbalancePlayer -> {
            playerSync[0] = new PlayerSync(new ResourceLocation(NutritionalBalance.MODID, "playersync"),inutritionalbalancePlayer);
        });
        ModNetworkHandler.sendToClient(playerSync[0], context.getSource().getPlayerOrException());
        return 0;
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("sync")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }
}
