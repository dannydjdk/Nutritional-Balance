package com.dannyandson.nutritionalbalance.network;

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
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class PlayerSync {

    private JsonObject inutritionalbalancePlayerJson = new JsonObject();
    private boolean openGUI;

    public PlayerSync(INutritionalBalancePlayer iNutritionalBalancePlayer) {
        this(iNutritionalBalancePlayer,false);
    }
    public PlayerSync(INutritionalBalancePlayer iNutritionalBalancePlayer, boolean openGUI){
        for(IPlayerNutrient iPlayerNutrient:iNutritionalBalancePlayer.getPlayerNutrients())
        {
            inutritionalbalancePlayerJson.addProperty(iPlayerNutrient.getNutrientName(), iPlayerNutrient.getValue());
        }
        this.openGUI=openGUI;
    }

    public PlayerSync(FriendlyByteBuf buffer)
    {
        this.openGUI=buffer.readBoolean();
        String bufferString = buffer.readUtf();
        inutritionalbalancePlayerJson = (JsonObject) (new JsonParser()).parse(bufferString);
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeBoolean(this.openGUI);
        buf.writeUtf(inutritionalbalancePlayerJson.toString());
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {
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
            if (this.openGUI) {
                NutrientGUI.open();
            }

            ctx.get().setPacketHandled(true);
        });
        return true;
    }
}
