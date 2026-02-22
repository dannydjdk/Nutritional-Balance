package com.dannyandson.nutritionalbalance.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class ClientHelpers {
    public static void showStatusToast(String statusName){
        Minecraft.getInstance().getToasts().addToast(
                SystemToast.multiline(
                        Minecraft.getInstance(),
                        SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                        Component.translatable("nutritionalbalance.nutrientstatus." + statusName),
                        Component.translatable("nutritionalbalance.nutrientstatus.msg." + statusName))
        );
    }
    public static Player getLocalPlayer(){
        return Minecraft.getInstance().player;
    }
}
