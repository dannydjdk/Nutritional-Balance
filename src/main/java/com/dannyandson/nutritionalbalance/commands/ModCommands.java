package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ModCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> cmdHD = dispatcher.register(
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