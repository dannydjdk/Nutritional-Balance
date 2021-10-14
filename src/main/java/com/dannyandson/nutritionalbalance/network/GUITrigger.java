package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class GUITrigger {

    public GUITrigger() {
    }

    public GUITrigger(PacketBuffer buffer) {
    }

    public void toBytes(PacketBuffer buffer) {
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()-> {
            ServerPlayerEntity player =  ctx.get().getSender();
            player.getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(capabilitynutritionalbalancePlayer -> {
                ModNetworkHandler.sendToClient(new PlayerSync(capabilitynutritionalbalancePlayer, true), ctx.get().getSender());
            });
        });
        return true;
    }
}
