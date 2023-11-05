package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class NutrientDataSyncTrigger {

    private Item item;

    public NutrientDataSyncTrigger(Item item) {
        this.item = item;
    }

    public NutrientDataSyncTrigger(FriendlyByteBuf buffer) {
        this.item=buffer.readItem().getItem();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeItem(item.getDefaultInstance());
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(()-> {
            ServerPlayer player =  ctx.get().getSender();
            ModNetworkHandler.sendToClient(new NutrientDataSync(item, WorldNutrients.getNutrients(item,player.level())), player);
        });
        return true;
    }
}
