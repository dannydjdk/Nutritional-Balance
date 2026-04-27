package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * One-shot server -> client packet that populates the entire local nutrient cache, so JEI/REI/EMI
 * tooltip indexing doesn't trigger per-item NutrientDataSyncTrigger round-trips. Sent on player join
 * and after /reload. Wire format: VarInt count, then (UTF identifier, UTF csv-of-nutrient-names) pairs;
 * empty CSV means "this item is known to have no nutrients".
 */
public record NutrientDataBulkSync(Map<String, List<String>> data) implements CustomPacketPayload {

    public static final Type<NutrientDataBulkSync> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(NutritionalBalance.MODID, "nutrient_data_bulk_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, NutrientDataBulkSync> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public NutrientDataBulkSync decode(RegistryFriendlyByteBuf buf) {
            int count = buf.readVarInt();
            Map<String, List<String>> data = new HashMap<>(count);
            for (int i = 0; i < count; i++) {
                String key = buf.readUtf();
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
            for (Map.Entry<String, List<String>> entry : packet.data().entrySet()) {
                buf.writeUtf(entry.getKey());
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