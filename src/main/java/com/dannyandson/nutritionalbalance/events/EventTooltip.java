package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import net.minecraft.block.CakeBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.StringJoiner;

public class EventTooltip {
    @SubscribeEvent
    public void onItemToolTipEvent(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        Item item = itemStack.getItem();
        World world;
        if (event.getEntity()!=null)
            world = event.getEntity().world;
        else
        {
            //tool tip event being called by a non-entity such as JEI
            try {
                world = Minecraft.getInstance().world;
            }catch (Exception e)
            {
                //this shouldn't happen unless some mod calls the tool tip event on the server side for some reason.
                NutritionalBalance.LOGGER.error("Exception during attempt to access tooltip by non-entity." + e.getLocalizedMessage());
                return;
            }
        }

        if(item.getFood() != null || item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof CakeBlock) {
            // Create readable list of nutrients
            StringJoiner stringJoiner = new StringJoiner(", ");

            try {

                List<Nutrient> nutrients = WorldNutrients.getNutrients(item, world);
                for (Nutrient nutrient : nutrients) {
                    stringJoiner.add(nutrient.getLocalizedName());
                }

                if (stringJoiner.length() > 0) {

                    String NUvalue = "";
                    if (item.getFood() != null) {
                        NUvalue = " (" + ((float) Math.round((WorldNutrients.getEffectiveFoodQuality(item.getFood())) * 10)) / 10 + "NU)";
                    }

                    event.getToolTip().add(ITextComponent.getTextComponentOrEmpty(
                            "Nutrients: " + stringJoiner.toString() + NUvalue
                    ));

                }

            }catch (Exception e)
            {
                //catch and log any exceptions thrown so JEI doesn't break if something goes wrong.
                NutritionalBalance.LOGGER.error("Exception thrown while adding nutrient info  for '" + item.getName().getString() + "' to tooltips: " + e.getMessage());
            }
        }

    }
}
