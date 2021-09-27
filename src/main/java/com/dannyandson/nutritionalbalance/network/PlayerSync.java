package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.dannyandson.nutritionalbalance.capabilities.DefaultPlayerNutrient;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class PlayerSync {

    private final ResourceLocation id;
    private JsonObject inutritionalbalancePlayerJson = new JsonObject();

    public PlayerSync(ResourceLocation id, INutritionalBalancePlayer inutritionalbalancePlayer)
    {
        this.id=id;
        for(IPlayerNutrient iPlayerNutrient:inutritionalbalancePlayer.getPlayerNutrients())
        {
            inutritionalbalancePlayerJson.addProperty(iPlayerNutrient.getNutrientName(), iPlayerNutrient.getValue());
        }

    }

    public PlayerSync(FriendlyByteBuf buffer)
    {
        this.id = buffer.readResourceLocation();
        String bufferString = buffer.readUtf();
        inutritionalbalancePlayerJson = (JsonObject) (new JsonParser()).parse(bufferString);
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeResourceLocation(id);
        buf.writeUtf(inutritionalbalancePlayerJson.toString());
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(()-> {
            INutritionalBalancePlayer iNutritionalBalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(ctx.get().getSender());
                for (Map.Entry<String, JsonElement> jsonElementEntry : inutritionalbalancePlayerJson.entrySet())
                {

                    IPlayerNutrient playerNutrient = iNutritionalBalancePlayer.getPlayerNutrientByName(jsonElementEntry.getKey());
                    if (playerNutrient == null) {
                        Nutrient worldNutrient = WorldNutrients.getByName(jsonElementEntry.getKey());
                        playerNutrient = new DefaultPlayerNutrient(worldNutrient);
                        iNutritionalBalancePlayer.getPlayerNutrients().add(playerNutrient);
                    }
                    playerNutrient.setValue(jsonElementEntry.getValue().getAsFloat());

                }

            ctx.get().setPacketHandled(true);
        });
        return true;
    }
}
