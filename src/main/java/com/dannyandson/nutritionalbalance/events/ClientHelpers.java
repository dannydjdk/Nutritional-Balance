package com.dannyandson.nutritionalbalance.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.SystemToast;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class ClientHelpers {
    public static void showStatusToast(String statusName){
        Minecraft.getInstance().getToasts().addToast(
                new SystemToast(
                        SystemToast.Type.TUTORIAL_HINT,
                        new TranslationTextComponent("nutritionalbalance.nutrientstatus." + statusName),
                        new TranslationTextComponent("nutritionalbalance.nutrientstatus.msg." + statusName))
        );
    }
    public static PlayerEntity getLocalPlayer(){
        return Minecraft.getInstance().player;
    }
}
