package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CakeBlock;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.bus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

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
        else {
            try { world = Minecraft.getInstance().level; }
            catch (Exception e) {
                NutritionalBalance.LOGGER.error("Exception during attempt to access tooltip by non-entity." + e.getLocalizedMessage());
                return;
            }
        }

        if(itemStack.has(DataComponents.FOOD) || item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof CakeBlock) {
            StringJoiner stringJoiner = new StringJoiner(", ");
            try {
                List<Nutrient> nutrients = WorldNutrients.getNutrients(itemStack, world);
                if (nutrients != null) {
                    for (Nutrient nutrient : nutrients) stringJoiner.add(nutrient.getLocalizedName());
                    if (stringJoiner.length() > 0) {
                        String NUvalue = "";
                        FoodProperties foodProperties = itemStack.get(DataComponents.FOOD);
                        if (foodProperties != null)
                            NUvalue = " (" + ((float) Math.round((WorldNutrients.getEffectiveFoodQuality(foodProperties, nutrients.size())) * 10)) / 10 + "NU)";
                        event.getToolTip().add(Component.nullToEmpty(
                                "\u00A77" + I18n.get("nutritionalbalance.nutrients") + ": \u00A72" + stringJoiner.toString() + "\u00A77" + NUvalue + "\u00A7r"));
                    }
                }
                var window = Minecraft.getInstance().getWindow();
                boolean shiftDown = InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT) || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
                if (event.getEntity()!=null && event.getEntity().getDisplayName().getString().equals("Dev") && Minecraft.getInstance().options.advancedItemTooltips)
                    if (shiftDown) {
                        for (TagKey<Item> tagKey: itemStack.tags().toList()) {
                            Identifier tag = tagKey.location();
                            event.getToolTip().add(Component.nullToEmpty("#" + tag.toString()));
                        }
                    } else {
                        event.getToolTip().add(Component.nullToEmpty("\u00A78--Hold shift for tag info--\u00A7r"));
                    }
            } catch (Exception e) {
                NutritionalBalance.LOGGER.error("Exception thrown while adding nutrient info for '" + itemStack.getDisplayName().getString() + "' to tooltips: " + e.getMessage());
            }
        }
    }
}
