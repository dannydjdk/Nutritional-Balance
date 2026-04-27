package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.NutrientDataBulkSync;
import com.dannyandson.nutritionalbalance.network.PlayerSync;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class EventPlayerJoin {
    @SubscribeEvent
    public void EntityJoinWorldEvent(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            // Bulk-sync the pre-resolved nutrient cache so the client's tooltip indexer doesn't flood
            // the server with per-item NutrientDataSyncTrigger requests.
            ModNetworkHandler.sendToClient(new NutrientDataBulkSync(WorldNutrients.snapshotForSync()), player);

            INutritionalBalancePlayer inutritionalbalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(player);
            PlayerSync playerSync = new PlayerSync(inutritionalbalancePlayer);
            ModNetworkHandler.sendToClient(playerSync, (ServerPlayer) event.getEntity());
        }
    }
}