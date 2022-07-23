package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventPlayerClone {
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        //apply penalty
        Player playerEntity = event.getEntity();
        if (playerEntity != null && event.isWasDeath()) {
            INutritionalBalancePlayer iNutritionalBalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(playerEntity);
            for (IPlayerNutrient playerNutrient : iNutritionalBalancePlayer.getPlayerNutrients()) {
                float newNutrientValue = Math.max(Config.NUTRIENT_INITIAL.get().floatValue(), playerNutrient.getValue() - Config.NUTRIENT_DEATH_LOSS.get().floatValue());
                iNutritionalBalancePlayer.getPlayerNutrientByName(playerNutrient.getNutrientName()).setValue(newNutrientValue);
            }
            PlayerNutritionData.getWorldNutritionData().setDirty();
        }
    }
}
