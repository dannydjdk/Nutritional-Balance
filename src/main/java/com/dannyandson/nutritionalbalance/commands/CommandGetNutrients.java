package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.capabilities.IPlayerNutrient;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.StringJoiner;

public class CommandGetNutrients implements Command<CommandSource> {

    private static final CommandGetNutrients CMD = new CommandGetNutrients();

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        String feedback = "Must be run on client.";

        ServerPlayerEntity player = (ServerPlayerEntity)context.getSource().getEntity();

        player.getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(inutritionalbalancePlayer -> {
            StringJoiner stringJoiner=new StringJoiner("\n");
            stringJoiner.add("Player Nutrition:");

            for (IPlayerNutrient nutrient:inutritionalbalancePlayer.getPlayerNutrients())
            {
                stringJoiner.add(nutrient.getNutrient().name +": " + (((float)Math.round(nutrient.getValue()*10))/10) + " " + nutrient.getStatus().name());
            }

            stringJoiner.add("Overall Status: " + inutritionalbalancePlayer.getStatus().name());

            context.getSource().sendFeedback(new TranslationTextComponent(stringJoiner.toString()),false);

            //ModNetworkHandler.sendToClient(new PacketOpenGui(),player);
        });

        return 0;
    }

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("get_nutrients")
                .requires(cs -> cs.hasPermissionLevel(0))
                .executes(CMD);
    }
}
