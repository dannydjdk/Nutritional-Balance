package com.dannyandson.nutritionalbalance.gui;

import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketOpenGui {

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(NutrientGUI::open);
        return true;
    }

}
