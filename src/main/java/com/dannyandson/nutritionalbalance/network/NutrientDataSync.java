package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public record NutrientDataSync(Item item, List<Nutrient> nutrients) implements CustomPacketPayload {

    public static final Type<NutrientDataSync> TYPE = new Type<>(Identifier.fromNamespaceAndPath(NutritionalBalance.MODID, "nutrient_data_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, NutrientDataSync> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public NutrientDataSync decode(RegistryFriendlyByteBuf buf) {
            Item item = ItemStack.STREAM_CODEC.decode(buf).getItem();
            List<Nutrient> nutrients = new ArrayList<>();
            String[] nutrientStrings = buf.readUtf().split(",");
            for (String nutrientString : nutrientStrings) {
                Nutrient nutrient = WorldNutrients.getByName(nutrientString);
                if (nutrient != null)
                    nutrients.add(nutrient);
            }
            return new NutrientDataSync(item, nutrients);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, NutrientDataSync packet) {
            ItemStack.STREAM_CODEC.encode(buf, packet.item().getDefaultInstance());
            StringBuilder nutrientString = new StringBuilder();
            for (Nutrient nutrient : packet.nutrients()) {
                if (nutrientString.length() > 0) nutrientString.append(",");
                nutrientString.append(nutrient.name);
            }
            buf.writeUtf(nutrientString.toString());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(NutrientDataSync packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            WorldNutrients.setItemNutrients(packet.item(), packet.nutrients());
        });
    }
}
