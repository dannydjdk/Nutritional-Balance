package com.dannyandson.nutritionalbalance.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

public class ClientHelpers {
    public static void showStatusToast(String statusName){
        Minecraft.getInstance().getToasts().addToast(
                new SystemToast(
                        SystemToast.SystemToastIds.TUTORIAL_HINT,
                        new TranslatableComponent("nutritionalbalance.nutrientstatus." + statusName),
                        new TranslatableComponent("nutritionalbalance.nutrientstatus.msg." + statusName))
        );
    }
    public static Player getLocalPlayer(){
        return Minecraft.getInstance().player;
    }
}
