package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * One-shot packet sent from the server to a client to populate the entire local nutrient cache, so the
 * client never needs to round-trip per-item NutrientDataSyncTrigger requests during JEI/REI/EMI tooltip
 * indexing or general inventory hovering. Sent on player join and re-broadcast after datapack reloads.
 *
 * Wire format: VarInt count, then for each entry: ResourceLocation (item id) + UTF (comma-separated
 * nutrient names; empty string means "this item is known to have no nutrients").
 */
public record NutrientDataBulkSync(Map<ResourceLocation, List<String>> data) implements CustomPacketPayload {

    public static final Type<NutrientDataBulkSync> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(NutritionalBalance.MODID, "nutrient_data_bulk_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, NutrientDataBulkSync> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public NutrientDataBulkSync decode(RegistryFriendlyByteBuf buf) {
            int count = buf.readVarInt();
            Map<ResourceLocation, List<String>> data = new HashMap<>(count);
            for (int i = 0; i < count; i++) {
                ResourceLocation key = buf.readResourceLocation();
                String csv = buf.readUtf();
                List<String> values = csv.isEmpty()
                        ? new ArrayList<>()
                        : new ArrayList<>(Arrays.asList(csv.split(",")));
                data.put(key, values);
            }
            return new NutrientDataBulkSync(data);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, NutrientDataBulkSync packet) {
            buf.writeVarInt(packet.data().size());
            for (Map.Entry<ResourceLocation, List<String>> entry : packet.data().entrySet()) {
                buf.writeResourceLocation(entry.getKey());
                buf.writeUtf(String.join(",", entry.getValue()));
            }
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(NutrientDataBulkSync packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> WorldNutrients.applyBulkSync(packet.data()));
    }
}