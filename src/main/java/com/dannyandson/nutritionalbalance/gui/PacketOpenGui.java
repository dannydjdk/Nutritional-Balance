package com.dannyandson.nutritionalbalance.gui;

import net.minecraftforge.event.network.CustomPayloadEvent;

public class PacketOpenGui {

    public boolean handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(NutrientGUI::open);
        return true;
    }

}
