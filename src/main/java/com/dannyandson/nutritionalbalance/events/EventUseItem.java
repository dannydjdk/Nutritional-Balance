package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import net.minecraft.item.Item;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class EventUseItem {

    @SubscribeEvent
    public void onUseItemFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity() instanceof net.minecraft.entity.player.PlayerEntity) {

            Item item = event.getItem().getItem();
            if (item.getFood()!=null)
            {
                event.getEntity().getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(inutritionalbalancePlayer -> {
                    List<Nutrient> nutrients = WorldNutrients.getNutrients(item,event.getEntity().world);
                    for (Nutrient nutrient:nutrients) {
                        float nutrientunits=WorldNutrients.getEffectiveFoodQuality(item.getFood()) * Config.NUTRIENT_INCREMENT_RATE.get().floatValue() / nutrients.size();
                        inutritionalbalancePlayer.getPlayerNutrientByName(nutrient.name).changeValue(nutrientunits);


                    }
                });
            }
        }

    }
}
