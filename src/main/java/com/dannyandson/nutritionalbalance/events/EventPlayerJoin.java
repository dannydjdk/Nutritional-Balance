package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.PlayerSync;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventPlayerJoin {
    @SubscribeEvent
    public void EntityJoinWorldEvent(EntityJoinWorldEvent event) {

        // Only check against players
        if (!(event.getEntity() instanceof PlayerEntity))
            return;

        // Server only
        if (!event.getWorld().isClientSide() && event.getEntity() instanceof ServerPlayerEntity)
        {
            event.getEntity().getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(inutritionalbalancePlayer -> {
                PlayerSync playerSync = new PlayerSync(new ResourceLocation(NutritionalBalance.MODID, "playersync"),inutritionalbalancePlayer);
                ModNetworkHandler.sendToClient(playerSync, (ServerPlayerEntity) event.getEntity());
            });
        }

    }
}
