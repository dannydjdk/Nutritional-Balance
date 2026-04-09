package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record LunchBoxActiveItemSync(String activeItemId) implements CustomPacketPayload {

    public static final Type<LunchBoxActiveItemSync> TYPE = new Type<>(Identifier.fromNamespaceAndPath(NutritionalBalance.MODID, "lunchbox_active"));

    public static final StreamCodec<ByteBuf, LunchBoxActiveItemSync> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, LunchBoxActiveItemSync::activeItemId,
            LunchBoxActiveItemSync::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(LunchBoxActiveItemSync packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof LunchBoxItem lunchBoxItem) {
                    lunchBoxItem.setActiveFood(stack, packet.activeItemId());
                }
            }
        });
    }
}
