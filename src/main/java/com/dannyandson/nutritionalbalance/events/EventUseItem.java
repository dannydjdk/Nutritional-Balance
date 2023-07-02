package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventUseItem {

    @SubscribeEvent
    public void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack item = event.getItem();
            if (item.getFoodProperties(player) != null) {
                INutritionalBalancePlayer iNutritionalBalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(player);
                iNutritionalBalancePlayer.consume(event.getItem(), event.getEntity().level);
                PlayerNutritionData.getWorldNutritionData().setDirty();
            }
        }

    }
}
