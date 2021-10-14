package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventUseItem {

    @SubscribeEvent
    public void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {

            Item item = event.getItem().getItem();
            if (item.getFoodProperties()!=null)
            {
                event.getEntity().getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(inutritionalbalancePlayer -> {
                    inutritionalbalancePlayer.consume(event.getItem(),event.getEntity().level);
                });
            }
        }

    }
}
