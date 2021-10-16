package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> cmdHD = dispatcher.register(
                Commands.literal(NutritionalBalance.MODID)
                        .then(CommandGetNutrients.register(dispatcher))
                        .then(CommandSyncClient.register(dispatcher))
                        .then(CommandGetUnassignedFoods.register(dispatcher))
                        .then(CommandResetPlayerEffects.register(dispatcher))
                        .then(CommandSetNutrient.register(dispatcher))
        );
        dispatcher.register(Commands.literal("nb").redirect(cmdHD));
    }

}