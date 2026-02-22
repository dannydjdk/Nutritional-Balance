package com.dannyandson.nutritionalbalance.keybinding;

import com.dannyandson.nutritionalbalance.network.GUITrigger;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class ModInputHandler {
    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        if (ModKeyBindings.keyBindings.get("nutrientgui").isDown())
        {
            ModNetworkHandler.sendToServer(new GUITrigger());
        }
    }
}
