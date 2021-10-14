package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.PlayerSync;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class CommandSyncClient implements Command<CommandSourceStack> {

    private static final CommandSyncClient CMD = new CommandSyncClient();

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        final PlayerSync[] playerSync = new PlayerSync[1];
        if (context.getSource().getEntity() instanceof Player player) {
            INutritionalBalancePlayer iNutritionalBalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(player);
            playerSync[0] = new PlayerSync(iNutritionalBalancePlayer);
            ModNetworkHandler.sendToClient(playerSync[0], context.getSource().getPlayerOrException());
        }
        return 0;
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("sync")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }
}
