package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.gui.PacketOpenGui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class ModNetworkHandler {
    private static SimpleChannel INSTANCE;
    private static int ID = 0;
    private static final int PROTOCOL_VERSION = 3;
    public static PacketOpenGui packetOpenGui;

    private static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        packetOpenGui = new PacketOpenGui();
        INSTANCE = ChannelBuilder
                .named(new ResourceLocation(NutritionalBalance.MODID, "playersync"))
                .networkProtocolVersion(PROTOCOL_VERSION)
                .clientAcceptedVersions((status, version) -> true)
                .serverAcceptedVersions((status, version) -> true)
                .simpleChannel();

        INSTANCE.messageBuilder(PlayerSync.class,nextID())
                .encoder(PlayerSync::toBytes)
                .decoder(PlayerSync::new)
                .consumerNetworkThread(PlayerSync::handle)
                .add();
        INSTANCE.messageBuilder(GUITrigger.class,nextID())
                .encoder(GUITrigger::toBytes)
                .decoder(GUITrigger::new)
                .consumerNetworkThread(GUITrigger::handle)
                .add();
        INSTANCE.messageBuilder(LunchBoxActiveItemSync.class,nextID())
                .encoder(LunchBoxActiveItemSync::toBytes)
                .decoder(LunchBoxActiveItemSync::new)
                .consumerNetworkThread(LunchBoxActiveItemSync::handle)
                .add();
        INSTANCE.messageBuilder(NutrientDataSyncTrigger.class,nextID())
                .encoder(NutrientDataSyncTrigger::toBytes)
                .decoder(NutrientDataSyncTrigger::new)
                .consumerNetworkThread(NutrientDataSyncTrigger::handle)
                .add();
        INSTANCE.messageBuilder(NutrientDataSync.class,nextID())
                .encoder(NutrientDataSync::toBytes)
                .decoder(NutrientDataSync::new)
                .consumerNetworkThread(NutrientDataSync::handle)
                .add();
        INSTANCE.messageBuilder(PacketOpenGui.class,nextID())
                .consumerNetworkThread(PacketOpenGui::handle)
                .add();

    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        INSTANCE.send(packet, PacketDistributor.PLAYER.with(player));
    }

    public static void sendToServer(Object packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }

}
