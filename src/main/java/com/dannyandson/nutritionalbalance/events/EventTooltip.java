package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import net.minecraft.block.CakeBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

public class EventTooltip {
    @SubscribeEvent
    public void onItemToolTipEvent(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        Item item = itemStack.getItem();
        World world;
        if (event.getEntity()!=null)
            world = event.getEntity().level;
        else
        {
            //tool tip event being called by a non-entity such as JEI
            try {
                world = Minecraft.getInstance().level;
            }catch (Exception e)
            {
                //this shouldn't happen unless some mod calls the tool tip event on the server side for some reason.
                NutritionalBalance.LOGGER.error("Exception during attempt to access tooltip by non-entity." + e.getLocalizedMessage());
                return;
            }
        }

        if(item.getFoodProperties() != null || item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof CakeBlock) {
            // Create readable list of nutrients
            StringJoiner stringJoiner = new StringJoiner(", ");

            try {

                List<Nutrient> nutrients = WorldNutrients.getNutrients(item, world);
                for (Nutrient nutrient : nutrients) {
                    stringJoiner.add(nutrient.getLocalizedName());
                }

                if (stringJoiner.length() > 0) {

                    String NUvalue = "";
                    if (item.getFoodProperties() != null) {
                        NUvalue = " (" + ((float) Math.round((WorldNutrients.getEffectiveFoodQuality(item.getFoodProperties())) * 10)) / 10 + "NU)";
                    }

                    event.getToolTip().add(ITextComponent.nullToEmpty(
                            "§7" + I18n.get("nutritionalbalance.nutrients") + ": §2" + stringJoiner.toString() + "§7" + NUvalue + "§r"
                    ));

                }

                if (event.getPlayer()!=null && event.getPlayer().getDisplayName().getString().equals("Dev") && Minecraft.getInstance().options.advancedItemTooltips)
                    if (Screen.hasShiftDown()) {
                        Collection<ResourceLocation> tags = ItemTags.getAllTags().getMatchingTags(itemStack.getItem());
                        for (ResourceLocation tag : tags) {
                            event.getToolTip().add(ITextComponent.nullToEmpty("#" + tag.toString()));
                        }

                        if (itemStack.getTag() != null) {
                            event.getToolTip().add(ITextComponent.nullToEmpty(itemStack.getTag().toString()));
                        }
                    } else {
                        event.getToolTip().add(ITextComponent.nullToEmpty("§8--Hold shift for tag info--§r"));
                    }

            }catch (Exception e)
            {
                //catch and log any exceptions thrown so JEI doesn't break if something goes wrong.
                NutritionalBalance.LOGGER.error("Exception thrown while adding nutrient info  for '" + item.getDefaultInstance().getDisplayName().getString() + "' to tooltips: " + e.getMessage());
            }
        }

    }
}
