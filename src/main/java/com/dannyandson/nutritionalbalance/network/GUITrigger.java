package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record GUITrigger() implements CustomPacketPayload {

    public static final Type<GUITrigger> TYPE = new Type<>(Identifier.fromNamespaceAndPath(NutritionalBalance.MODID, "gui_trigger"));

    public static final StreamCodec<ByteBuf, GUITrigger> STREAM_CODEC = StreamCodec.unit(new GUITrigger());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(GUITrigger packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                INutritionalBalancePlayer inutritionalbalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(player);
                ModNetworkHandler.sendToClient(new PlayerSync(inutritionalbalancePlayer, true), player);
            }
        });
    }
}
