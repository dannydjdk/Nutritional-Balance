package com.dannyandson.nutritionalbalance.gui;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketOpenGui() implements CustomPacketPayload {

    public static final Type<PacketOpenGui> TYPE = new Type<>(Identifier.fromNamespaceAndPath(NutritionalBalance.MODID, "open_gui"));

    public static final StreamCodec<ByteBuf, PacketOpenGui> STREAM_CODEC = StreamCodec.unit(new PacketOpenGui());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(PacketOpenGui packet, IPayloadContext ctx) {
        ctx.enqueueWork(NutrientGUI::open);
    }
}
