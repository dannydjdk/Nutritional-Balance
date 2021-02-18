package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.capabilities.DefaultPlayerNutrient;
import com.dannyandson.nutritionalbalance.capabilities.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.capabilities.IPlayerNutrient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public PlayerSync(PacketBuffer buffer)
    {
        this.id = buffer.readResourceLocation();
        String bufferString = buffer.readString();
        inutritionalbalancePlayerJson = (JsonObject) (new JsonParser()).parse(bufferString);
    }

    public void toBytes(PacketBuffer buf)
    {
        buf.writeResourceLocation(id);
        buf.writeString(inutritionalbalancePlayerJson.toString());
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(()-> {
            //Minecraft.getInstance().player.sendStatusMessage(ITextComponent.getTextComponentOrEmpty(inutritionalbalancePlayer.toString()),true);

            Minecraft.getInstance().player.getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(capabilitynutritionalbalancePlayer -> {
                for (Map.Entry<String, JsonElement> jsonElementEntry : inutritionalbalancePlayerJson.entrySet())
                {

                    IPlayerNutrient playerNutrient = capabilitynutritionalbalancePlayer.getPlayerNutrientByName(jsonElementEntry.getKey());
                    if (playerNutrient == null) {
                        Nutrient worldNutrient = WorldNutrients.getByName(jsonElementEntry.getKey());
                        playerNutrient = new DefaultPlayerNutrient(worldNutrient);
                        capabilitynutritionalbalancePlayer.getPlayerNutrients().add(playerNutrient);
                    }
                    playerNutrient.setValue(jsonElementEntry.getValue().getAsFloat());

                }
            });


            ctx.get().setPacketHandled(true);
        });
        return true;
    }
}
