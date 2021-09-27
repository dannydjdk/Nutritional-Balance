package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventUseItem {

    @SubscribeEvent
    public void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof Player player) {
            Item item = event.getItem().getItem();
            INutritionalBalancePlayer iNutritionalBalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(player);
            if (item.getFoodProperties() != null) {
                iNutritionalBalancePlayer.consume(event.getItem(), event.getEntity().level);
                PlayerNutritionData.getWorldNutritionData().setDirty();
            }
        }

    }
}
