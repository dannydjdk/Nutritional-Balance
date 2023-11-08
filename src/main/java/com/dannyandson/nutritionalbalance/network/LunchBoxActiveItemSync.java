package com.dannyandson.nutritionalbalance.network;

import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

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

    public boolean handle(CustomPayloadEvent.Context ctx) {

        ctx.enqueueWork(() -> {

            ItemStack stack = ctx.getSender().getMainHandItem();
            if (stack.getItem() instanceof LunchBoxItem lunchBoxItem) {
                lunchBoxItem.setActiveFood(stack,activeItemId);
            }
            ctx.setPacketHandled(true);
        });
        return true;
    }
}
