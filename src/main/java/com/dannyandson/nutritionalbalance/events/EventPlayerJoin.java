package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.PlayerSync;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventPlayerJoin {
    @SubscribeEvent
    public void EntityJoinWorldEvent(EntityJoinWorldEvent event) {

        // Server player only
        if (!event.getWorld().isClientSide() && event.getEntity() instanceof ServerPlayer player) {
            INutritionalBalancePlayer inutritionalbalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(player);
            PlayerSync playerSync = new PlayerSync(new ResourceLocation(NutritionalBalance.MODID, "playersync"), inutritionalbalancePlayer);
            ModNetworkHandler.sendToClient(playerSync, (ServerPlayer) event.getEntity());
        }

    }
}
