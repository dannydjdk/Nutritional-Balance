package com.dannyandson.nutritionalbalance.keybinding;

import com.dannyandson.nutritionalbalance.gui.NutrientGUI;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModInputHandler {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
       // NutritionalBalance.LOGGER.info("Key pressed:" + event.getKey());
        if (ModKeyBindings.keyBindings.get("nutrientgui").isPressed())
        {
            NutrientGUI.open();
        }
    }
}