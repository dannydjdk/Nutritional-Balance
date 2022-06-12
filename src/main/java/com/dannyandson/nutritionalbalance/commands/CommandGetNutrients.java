package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.StringJoiner;

public class CommandGetNutrients implements Command<CommandSourceStack> {

    private static final CommandGetNutrients CMD = new CommandGetNutrients();

    @Override
    public int run(CommandContext<CommandSourceStack> context) {
        String feedback = "Must be run on client.";

        if (context.getSource().getEntity() instanceof ServerPlayer player) {
            INutritionalBalancePlayer iNutritionalBalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(player);
            StringJoiner stringJoiner = new StringJoiner("\n");
            stringJoiner.add("Player Nutrition:");

            for (IPlayerNutrient nutrient : iNutritionalBalancePlayer.getPlayerNutrients()) {
                stringJoiner.add(nutrient.getNutrient().name + ": " + (((float) Math.round(nutrient.getValue() * 10)) / 10) + " " + nutrient.getStatus().name());
            }

            stringJoiner.add("Overall Status: " + iNutritionalBalancePlayer.getStatus().name());

            context.getSource().sendSuccess(Component.translatable(stringJoiner.toString()), false);

        }
        return 0;
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("get_nutrients")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }
}
