package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.events.ClientHelpers;
import com.dannyandson.nutritionalbalance.gui.NutrientGUI;
import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.dannyandson.nutritionalbalance.capabilities.DefaultPlayerNutrient;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

public record PlayerSync(String jsonData, boolean openGUI) implements CustomPacketPayload {

    public static final Type<PlayerSync> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "player_sync"));

    public static final StreamCodec<ByteBuf, PlayerSync> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PlayerSync::jsonData,
            ByteBufCodecs.BOOL, PlayerSync::openGUI,
            PlayerSync::new
    );

    public PlayerSync(INutritionalBalancePlayer iNutritionalBalancePlayer) {
        this(iNutritionalBalancePlayer, false);
    }

    public PlayerSync(INutritionalBalancePlayer iNutritionalBalancePlayer, boolean openGUI) {
        this(buildJson(iNutritionalBalancePlayer), openGUI);
    }

    private static String buildJson(INutritionalBalancePlayer iNutritionalBalancePlayer) {
        JsonObject json = new JsonObject();
        for (IPlayerNutrient iPlayerNutrient : iNutritionalBalancePlayer.getPlayerNutrients()) {
            json.addProperty(iPlayerNutrient.getNutrientName(), iPlayerNutrient.getValue());
        }
        return json.toString();
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PlayerSync packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            JsonObject inutritionalbalancePlayerJson = (JsonObject) JsonParser.parseString(packet.jsonData());
            INutritionalBalancePlayer iNutritionalBalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(ClientHelpers.getLocalPlayer());
            for (Map.Entry<String, JsonElement> jsonElementEntry : inutritionalbalancePlayerJson.entrySet()) {
                IPlayerNutrient playerNutrient = iNutritionalBalancePlayer.getPlayerNutrientByName(jsonElementEntry.getKey());
                if (playerNutrient == null) {
                    Nutrient worldNutrient = WorldNutrients.getByName(jsonElementEntry.getKey());
                    playerNutrient = new DefaultPlayerNutrient(worldNutrient);
                    iNutritionalBalancePlayer.getPlayerNutrients().add(playerNutrient);
                }
                playerNutrient.setValue(jsonElementEntry.getValue().getAsFloat());
            }
            if (packet.openGUI()) {
                NutrientGUI.open();
            }
        });
    }
}
