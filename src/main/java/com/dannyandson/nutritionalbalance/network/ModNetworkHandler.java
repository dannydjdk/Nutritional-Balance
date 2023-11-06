package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.gui.PacketOpenGui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworkHandler {
    private static SimpleChannel INSTANCE;
    private static int ID = 0;
    private static final String PROTOCOL_VERSION = "2.3";
    public static PacketOpenGui packetOpenGui;

    private static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        packetOpenGui = new PacketOpenGui();
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(NutritionalBalance.MODID, "playersync"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals);

        INSTANCE.messageBuilder(PlayerSync.class,nextID())
                .encoder(PlayerSync::toBytes)
                .decoder(PlayerSync::new)
                .consumer(PlayerSync::handle)
                .add();
        INSTANCE.messageBuilder(GUITrigger.class,nextID())
                .encoder(GUITrigger::toBytes)
                .decoder(GUITrigger::new)
                .consumer(GUITrigger::handle)
                .add();
        INSTANCE.messageBuilder(LunchBoxActiveItemSync.class,nextID())
                .encoder(LunchBoxActiveItemSync::toBytes)
                .decoder(LunchBoxActiveItemSync::new)
                .consumer(LunchBoxActiveItemSync::handle)
                .add();
        INSTANCE.messageBuilder(NutrientDataSyncTrigger.class,nextID())
                .encoder(NutrientDataSyncTrigger::toBytes)
                .decoder(NutrientDataSyncTrigger::new)
                .consumer(NutrientDataSyncTrigger::handle)
                .add();
        INSTANCE.messageBuilder(NutrientDataSync.class,nextID())
                .encoder(NutrientDataSync::toBytes)
                .decoder(NutrientDataSync::new)
                .consumer(NutrientDataSync::handle)
                .add();
        INSTANCE.registerMessage(
                nextID(),
                PacketOpenGui.class,
                (((packetOpenGui, packetBuffer) -> {})),
                (packetBuffer->packetOpenGui),
                PacketOpenGui::handle
        );

    }

    public static void sendToClient(Object packet, ServerPlayer player) {
        INSTANCE.sendTo(packet, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }

}
