package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CakeBlock;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.bus.api.SubscribeEvent;

import java.util.List;
import java.util.StringJoiner;

public class EventTooltip {
    @SubscribeEvent
    public void onItemToolTipEvent(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        Item item = itemStack.getItem();
        Level world;
        if (event.getEntity()!=null)
            world = event.getEntity().level();
        else
        {
            try {
                world = Minecraft.getInstance().level;
            }catch (Exception e)
            {
                NutritionalBalance.LOGGER.error("Exception during attempt to access tooltip by non-entity." + e.getLocalizedMessage());
                return;
            }
        }

        if(itemStack.getFoodProperties(null) != null || item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof CakeBlock) {
            StringJoiner stringJoiner = new StringJoiner(", ");

            try {

                List<Nutrient> nutrients = WorldNutrients.getNutrients(itemStack, world);
                if (nutrients != null) {
                    for (Nutrient nutrient : nutrients) {
                        stringJoiner.add(nutrient.getLocalizedName());
                    }

                    if (stringJoiner.length() > 0) {

                        String NUvalue = "";
                        FoodProperties foodProperties = itemStack.getFoodProperties(null);
                        if (foodProperties != null) {
                            NUvalue = " (" + ((float) Math.round((WorldNutrients.getEffectiveFoodQuality(foodProperties, nutrients.size())) * 10)) / 10 + "NU)";
                        }

                        event.getToolTip().add(Component.nullToEmpty(
                                "§7" + I18n.get("nutritionalbalance.nutrients") + ": §2" + stringJoiner.toString() + "§7" + NUvalue + "§r"
                        ));
                    }
                }

                if (event.getEntity()!=null && event.getEntity().getDisplayName().getString().equals("Dev") && Minecraft.getInstance().options.advancedItemTooltips)
                    if (Screen.hasShiftDown()) {
                        for (TagKey<Item> tagKey: itemStack.getTags().toList()) {
                            ResourceLocation tag = tagKey.location();
                            event.getToolTip().add(Component.nullToEmpty("#" + tag.toString()));
                        }
                    } else {
                        event.getToolTip().add(Component.nullToEmpty("§8--Hold shift for tag info--§r"));
                    }

            }catch (Exception e)
            {
                NutritionalBalance.LOGGER.error("Exception thrown while adding nutrient info  for '" + itemStack.getDisplayName().getString() + "' to tooltips: " + e.getMessage());
            }
        }
    }
}
