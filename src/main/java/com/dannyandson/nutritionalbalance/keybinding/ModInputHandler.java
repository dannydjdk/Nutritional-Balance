package com.dannyandson.nutritionalbalance.keybinding;

import com.dannyandson.nutritionalbalance.network.GUITrigger;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModInputHandler {
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (ModKeyBindings.keyBindings.get("nutrientgui").isDown())
        {
            ModNetworkHandler.sendToServer(new GUITrigger());
        }
    }
}