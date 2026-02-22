package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.StringJoiner;

public class CommandGetUnassignedFoods implements Command<CommandSourceStack> {

    private static final CommandGetUnassignedFoods CMD = new CommandGetUnassignedFoods();

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

        StringJoiner stringJoiner = new StringJoiner("\n");
        for(Item item: BuiltInRegistries.ITEM) {
            if (item.getFoodProperties(item.getDefaultInstance(), null)!=null && WorldNutrients.getNutrients(item,context.getSource().getLevel()).size()==0)
                stringJoiner.add(BuiltInRegistries.ITEM.getKey(item).toString());
        }

        context.getSource().sendSuccess(() -> {return Component.translatable(stringJoiner.toString(),false);},false);

        return 0;
    }

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("get_unassigned_foods")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }
}
