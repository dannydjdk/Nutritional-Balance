package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class GUITrigger {

    public GUITrigger() {
    }

    public GUITrigger(FriendlyByteBuf buffer) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(()-> {
            ServerPlayer player =  ctx.getSender();
            INutritionalBalancePlayer inutritionalbalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(player);
            ModNetworkHandler.sendToClient(new PlayerSync(inutritionalbalancePlayer,true), ctx.getSender());
        });
        return true;
    }
}
