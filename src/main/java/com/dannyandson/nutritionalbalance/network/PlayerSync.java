package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.gui.NutrientGUI;
import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.capabilities.DefaultPlayerNutrient;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class PlayerSync {

    private JsonObject iNutritionalBalancePlayerJson = new JsonObject();
    boolean openGUI;

    public PlayerSync(INutritionalBalancePlayer iNutritionalBalancePlayer) {
        this(iNutritionalBalancePlayer,false);
    }

    public PlayerSync(INutritionalBalancePlayer iNutritionalBalancePlayer, boolean openGUI){
        this.openGUI=openGUI;
        for(IPlayerNutrient iPlayerNutrient:iNutritionalBalancePlayer.getPlayerNutrients())
        {
            iNutritionalBalancePlayerJson.addProperty(iPlayerNutrient.getNutrientName(), iPlayerNutrient.getValue());
        }

    }

    public PlayerSync(PacketBuffer buffer)
    {
        this.openGUI = buffer.readBoolean();
        String bufferString = buffer.readUtf();
        iNutritionalBalancePlayerJson = (JsonObject) (new JsonParser()).parse(bufferString);
    }

    public void toBytes(PacketBuffer buf)
    {
        buf.writeBoolean(this.openGUI);
        buf.writeUtf(iNutritionalBalancePlayerJson.toString());
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            Minecraft.getInstance().player.getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(capabilitynutritionalbalancePlayer -> {
                for (Map.Entry<String, JsonElement> jsonElementEntry : iNutritionalBalancePlayerJson.entrySet()) {
                    IPlayerNutrient playerNutrient = capabilitynutritionalbalancePlayer.getPlayerNutrientByName(jsonElementEntry.getKey());
                    if (playerNutrient == null) {
                        Nutrient worldNutrient = WorldNutrients.getByName(jsonElementEntry.getKey());
                        playerNutrient = new DefaultPlayerNutrient(worldNutrient);
                        capabilitynutritionalbalancePlayer.getPlayerNutrients().add(playerNutrient);
                    }
                    playerNutrient.setValue(jsonElementEntry.getValue().getAsFloat());
                }
            });
            if (this.openGUI) {
                NutrientGUI.open();
            }

            ctx.get().setPacketHandled(true);
        });
        return true;
    }
}
