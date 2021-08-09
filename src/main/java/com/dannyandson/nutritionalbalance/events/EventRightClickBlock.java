package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

public class EventRightClickBlock {
    @SubscribeEvent
    public void rightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        Player player = event.getPlayer();
        BlockState blockState = event.getWorld().getBlockState(event.getPos());
        Block block = blockState.getBlock();

        //Detect eating cake
        if (block instanceof CakeBlock && player != null && player.canEat(false))
        {
            Item cakeItem = block.asItem();

            player.getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(inutritionalbalancePlayer -> {
                List<Nutrient> nutrients = WorldNutrients.getNutrients(cakeItem,player.level);
                for (Nutrient nutrient:nutrients) {
                    //hardcoding cake to 2.4 effective food quality per Minecraft wiki. No way to query for this.
                    float nutrientunits=2.4f * Config.NUTRIENT_INCREMENT_RATE.get().floatValue() / nutrients.size();
                    inutritionalbalancePlayer.getPlayerNutrientByName(nutrient.name).changeValue(nutrientunits);

                }
            });

        }
    }
}
