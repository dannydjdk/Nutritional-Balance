package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LunchBoxActiveItemSync {

    String activeItemId;

    public LunchBoxActiveItemSync(String activeItemId) {
        this.activeItemId = activeItemId;
    }

    public LunchBoxActiveItemSync(FriendlyByteBuf buffer) {
        this.activeItemId = buffer.readUtf();
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeUtf(activeItemId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ItemStack stack = ctx.get().getSender().getMainHandItem();
            if (stack.getItem() instanceof LunchBoxItem lunchBoxItem) {
                lunchBoxItem.setActiveFood(stack,activeItemId);
            }
            ctx.get().setPacketHandled(true);
        });
        return true;
    }
}
