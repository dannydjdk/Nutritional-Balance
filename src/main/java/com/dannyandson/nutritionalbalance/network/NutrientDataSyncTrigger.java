package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record NutrientDataSyncTrigger(Item item) implements CustomPacketPayload {

    public static final Type<NutrientDataSyncTrigger> TYPE = new Type<>(Identifier.fromNamespaceAndPath(NutritionalBalance.MODID, "nutrient_sync_trigger"));

    public static final StreamCodec<RegistryFriendlyByteBuf, NutrientDataSyncTrigger> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public NutrientDataSyncTrigger decode(RegistryFriendlyByteBuf buf) {
            return new NutrientDataSyncTrigger(ItemStack.STREAM_CODEC.decode(buf).getItem());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, NutrientDataSyncTrigger packet) {
            ItemStack.STREAM_CODEC.encode(buf, packet.item().getDefaultInstance());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(NutrientDataSyncTrigger packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player) {
                ModNetworkHandler.sendToClient(new NutrientDataSync(packet.item(), WorldNutrients.getNutrients(packet.item(), player.level())), player);
            }
        });
    }
}
