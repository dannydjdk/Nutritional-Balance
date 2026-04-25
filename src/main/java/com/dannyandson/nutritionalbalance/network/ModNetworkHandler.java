package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import net.minecraft.server.level.ServerPlayer;
import com.dannyandson.nutritionalbalance.gui.PacketOpenGui;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetworkHandler {

    public static void registerMessages(RegisterPayloadHandlersEvent event) {
        // Bumped from 2.3 -> 2.4 for the new NutrientDataBulkSync payload.
        final PayloadRegistrar registrar = event.registrar(NutritionalBalance.MODID).versioned("2.4");

        registrar.playToClient(PlayerSync.TYPE, PlayerSync.STREAM_CODEC, PlayerSync::handle);
        registrar.playToServer(GUITrigger.TYPE, GUITrigger.STREAM_CODEC, GUITrigger::handle);
        registrar.playToServer(LunchBoxActiveItemSync.TYPE, LunchBoxActiveItemSync.STREAM_CODEC, LunchBoxActiveItemSync::handle);
        registrar.playToServer(NutrientDataSyncTrigger.TYPE, NutrientDataSyncTrigger.STREAM_CODEC, NutrientDataSyncTrigger::handle);
        registrar.playToClient(NutrientDataSync.TYPE, NutrientDataSync.STREAM_CODEC, NutrientDataSync::handle);
        registrar.playToClient(NutrientDataBulkSync.TYPE, NutrientDataBulkSync.STREAM_CODEC, NutrientDataBulkSync::handle);
        registrar.playToClient(PacketOpenGui.TYPE, PacketOpenGui.STREAM_CODEC, PacketOpenGui::handle);
    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        if (packet instanceof PlayerSync ps) {
            PacketDistributor.sendToPlayer(player, ps);
        } else if (packet instanceof NutrientDataSync nds) {
            PacketDistributor.sendToPlayer(player, nds);
        } else if (packet instanceof NutrientDataBulkSync ndbs) {
            PacketDistributor.sendToPlayer(player, ndbs);
        } else if (packet instanceof PacketOpenGui pog) {
            PacketDistributor.sendToPlayer(player, pog);
        }
    }

    public static void sendToServer(Object packet) {
        if (packet instanceof GUITrigger gt) {
            PacketDistributor.sendToServer(gt);
        } else if (packet instanceof LunchBoxActiveItemSync lbais) {
            PacketDistributor.sendToServer(lbais);
        } else if (packet instanceof NutrientDataSyncTrigger ndst) {
            PacketDistributor.sendToServer(ndst);
        }
    }
}