package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.PlayerSync;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventPlayerJoin {
    @SubscribeEvent
    public void EntityJoinWorldEvent(EntityJoinLevelEvent event) {

        // Server player only
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            INutritionalBalancePlayer inutritionalbalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(player);
            PlayerSync playerSync = new PlayerSync(inutritionalbalancePlayer);
            ModNetworkHandler.sendToClient(playerSync, (ServerPlayer) event.getEntity());
        }

    }
}
