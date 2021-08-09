package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.gui.NutrientButton;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventNutrientButton {

    @SubscribeEvent
    public void openGUI (GuiScreenEvent.InitGuiEvent.Post event)
    {
        if ((event.getGui() instanceof InventoryScreen) && Config.NUTRIENT_BUTTON_ENABLED.get()) {
            InventoryScreen gui = (InventoryScreen) event.getGui();
            event.addWidget(new NutrientButton(gui, ITextComponent.nullToEmpty("N")));
        }
    }

}
