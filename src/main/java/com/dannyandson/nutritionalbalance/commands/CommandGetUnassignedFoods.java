package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.item.Item;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.StringJoiner;

public class CommandGetUnassignedFoods implements Command<CommandSource> {

    private static final CommandGetUnassignedFoods CMD = new CommandGetUnassignedFoods();

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {

        IForgeRegistry<Item> items =  ForgeRegistries.ITEMS;
        StringJoiner stringJoiner = new StringJoiner("\n");
        for(Item item:items) {
            if (item.getFoodProperties()!=null && WorldNutrients.getNutrients(item,context.getSource().getLevel()).size()==0)
                stringJoiner.add(item.getRegistryName().toString());
        }

        context.getSource().sendSuccess(new TranslationTextComponent(stringJoiner.toString(),false),false);

        return 0;
    }

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("get_unassigned_foods")
                .requires(cs -> cs.hasPermission(0))
                .executes(CMD);
    }
}
