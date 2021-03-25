package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventPlayerDeath {
    @SubscribeEvent
    public void onPlayerDeath(PlayerEvent.Clone event)
    {
        //player capabilities do not persist on death, so
        //grab previous nutrition values, apply penalty,
        //and set to new player instance
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)event.getEntity();

            event.getOriginal().getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(originalInutritionalbalancePlayer -> {

                playerEntity.getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(newInutritionalbalancePlayer -> {

                    for (IPlayerNutrient originalPlayerNutrient : originalInutritionalbalancePlayer.getPlayerNutrients()) {
                        if (event.isWasDeath()) {
                            float newNutrientValue = Math.max(Config.NUTRIENT_INITIAL.get().floatValue(), originalPlayerNutrient.getValue() - Config.NUTRIENT_DEATH_LOSS.get().floatValue());
                            if (newNutrientValue > originalPlayerNutrient.getValue())
                                newNutrientValue = originalPlayerNutrient.getValue();
                            newInutritionalbalancePlayer.getPlayerNutrientByName(originalPlayerNutrient.getNutrientName()).setValue(newNutrientValue);
                        }
                        else
                        {
                            newInutritionalbalancePlayer.getPlayerNutrientByName(originalPlayerNutrient.getNutrientName()).setValue(originalPlayerNutrient.getValue());
                        }
                    }

                });

            });
        }

    }
}
