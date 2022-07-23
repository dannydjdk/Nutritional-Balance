package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.gui.NutrientButton;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventNutrientButton {

    @SubscribeEvent
    public void openGUI (ScreenEvent.Init.Post event)
    {
        if ((event.getScreen() instanceof InventoryScreen gui) && Config.NUTRIENT_BUTTON_ENABLED.get()) {
            event.addListener(new NutrientButton(gui, Component.nullToEmpty("N")));
        }
    }

}
