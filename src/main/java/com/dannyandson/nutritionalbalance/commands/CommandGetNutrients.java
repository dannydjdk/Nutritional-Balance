package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.StringJoiner;

public class CommandGetNutrients implements Command<CommandSourceStack> {

    private static final CommandGetNutrients CMD = new CommandGetNutrients();

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String feedback = "Must be run on client.";

        ServerPlayer player = (ServerPlayer)context.getSource().getEntity();

        player.getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(inutritionalbalancePlayer -> {
            StringJoiner stringJoiner=new StringJoiner("\n");
            stringJoiner.add("Player Nutrition:");

            for (IPlayerNutrient nutrient:inutritionalbalancePlayer.getPlayerNutrients())
            {
                stringJoiner.add(nutrient.getNutrient().name +": " + (((float)Math.round(nutrient.getValue()*10))/10) + " " + nutrient.getStatus().name());
            }

            stringJoiner.add("Overall Status: " + inutritionalbalancePlayer.getStatus().name());

            context.getSource().sendSuccess(new TranslatableComponent(stringJoiner.toString()),false);

            //ModNetworkHandler.sendToClient(new PacketOpenGui(),player);
        });

        return 0;
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("get_nutrients")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }
}
