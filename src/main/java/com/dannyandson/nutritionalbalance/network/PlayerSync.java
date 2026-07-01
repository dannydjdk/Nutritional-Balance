package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.Config;
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
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

// toastStatus carries the new nutrient status name when the server detects a change; empty means no toast.
public record PlayerSync(String jsonData, boolean openGUI, String toastStatus) implements CustomPacketPayload {

    public static final Type<PlayerSync> TYPE = new Type<>(Identifier.fromNamespaceAndPath(NutritionalBalance.MODID, "player_sync"));

    public static final StreamCodec<ByteBuf, PlayerSync> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PlayerSync::jsonData,
            ByteBufCodecs.BOOL, PlayerSync::openGUI,
            ByteBufCodecs.STRING_UTF8, PlayerSync::toastStatus,
            PlayerSync::new
    );

    public PlayerSync(INutritionalBalancePlayer iNutritionalBalancePlayer) {
        this(iNutritionalBalancePlayer, false, "");
    }

    public PlayerSync(INutritionalBalancePlayer iNutritionalBalancePlayer, boolean openGUI) {
        this(iNutritionalBalancePlayer, openGUI, "");
    }

    public PlayerSync(INutritionalBalancePlayer iNutritionalBalancePlayer, boolean openGUI, String toastStatus) {
        this(buildJson(iNutritionalBalancePlayer), openGUI, toastStatus);
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
            // Server-driven status toast: shown only when the server flagged a status change,
            // and only if this client has toasts enabled.
            if (!packet.toastStatus().isEmpty() && Config.SHOW_THRESHOLD_TOAST.get()) {
                ClientHelpers.showStatusToast(packet.toastStatus());
            }
        });
    }
}