package com.dannyandson.nutritionalbalance.commands;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.NutritionalBalancePlayerHelper;
import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CommandSetNutrient {

    public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
        return Commands.literal("set_nutrient")
                .requires(cs -> cs.hasPermission(3))
                .then(Commands.argument("nutrient", new NutrientStringArgumentType())
                        .then(Commands.argument("value", FloatArgumentType.floatArg(0.0f,Config.NUTRIENT_MAX.get().floatValue()))
                                .executes(ctx->setNutrients(ctx))));

    }

    public static int setNutrients(CommandContext<CommandSource> context){
        if (context.getSource().getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) context.getSource().getEntity();

            INutritionalBalancePlayer iNutritionalBalancePlayer = NutritionalBalancePlayerHelper.getInutritionalBalancePlayer(player);
            String nutrientName = context.getArgument("nutrient",String.class);
            float value = FloatArgumentType.getFloat(context,"value");
            iNutritionalBalancePlayer.getPlayerNutrientByName(nutrientName).setValue(value);
        }

        return 0;
    }

    public static class NutrientStringArgumentType implements ArgumentType<String>{

        private List<String> nutrientList = new ArrayList<>();

        @Override
        public String parse(StringReader reader) throws CommandSyntaxException {
            String string = reader.readString();
            if (getNutrientList().contains(string))
                return string;
            SimpleCommandExceptionType exceptionType = new SimpleCommandExceptionType(new Message() {
                @Override
                public String getString() {
                    return "Invalid nutrient argument";
                }
            });
            throw exceptionType.create();
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            return ISuggestionProvider.suggest(getNutrientList(),builder);
        }

        @Override
        public Collection<String> getExamples() {
            return getNutrientList();
        }

        private List<String> getNutrientList() {
            if (nutrientList.size()==0){
                List<Nutrient> nutrients = WorldNutrients.get();
                for(Nutrient nutrient : nutrients)
                    nutrientList.add(nutrient.name);
            }
            return nutrientList;
        }

        private void setNutrientList(List<String> nutrientList) {
            this.nutrientList = nutrientList;
        }

        public static class Serializer implements IArgumentSerializer<NutrientStringArgumentType> {

            @Override
            public void serializeToNetwork(NutrientStringArgumentType nutrientStringArgumentType, PacketBuffer buffer) {
                buffer.writeUtf(String.join("!!",nutrientStringArgumentType.getNutrientList()));
            }

            @Override
            public NutrientStringArgumentType deserializeFromNetwork(PacketBuffer buffer) {
                List<String> nutrientList = Arrays.stream(buffer.readUtf().split("!!")).collect(Collectors.toList());
                NutrientStringArgumentType nutrientStringArgumentType = new NutrientStringArgumentType();
                nutrientStringArgumentType.setNutrientList(nutrientList);
                return nutrientStringArgumentType;
            }

            @Override
            public void serializeToJson(NutrientStringArgumentType nutrientStringArgumentType, JsonObject json) {
                json.addProperty("nutrient_string_argument",String.join("!!",nutrientStringArgumentType.getNutrientList()));
            }
        }

    }



}
