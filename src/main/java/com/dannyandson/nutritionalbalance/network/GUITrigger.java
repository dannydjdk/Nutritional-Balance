package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GUITrigger {

    public GUITrigger() {
    }

    public GUITrigger(FriendlyByteBuf buffer) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()-> {
            ServerPlayer player =  ctx.get().getSender();
            INutritionalBalancePlayer inutritionalbalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(player);
            ModNetworkHandler.sendToClient(new PlayerSync(inutritionalbalancePlayer,true), ctx.get().getSender());
        });
        return true;
    }
}
