package com.dannyandson.nutritionalbalance.setup;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NutritionalBalance.MODID, value = Dist.CLIENT)

public class ClientBinding {

    @SubscribeEvent
    public static void wheelEvent(final InputEvent.MouseScrollEvent mouseScrollEvent) {
        if (mouseScrollEvent.isCanceled() || mouseScrollEvent.getScrollDelta() == 0) return;
        Player player = Minecraft.getInstance().player;
        if (player != null && player.isSecondaryUseActive() && player.getMainHandItem().getItem() instanceof LunchBoxItem lunchBoxItem) {
            ItemStack lunchBoxStack = player.getMainHandItem();
            Integer activeSlot = lunchBoxItem.getActiveFoodItemSlot(lunchBoxStack);
            Integer maxSlot = null;
            for (int i = 0; i < Config.LUNCHBOX_SLOT_COUNT.get(); i++) {
                ItemStack stack = lunchBoxItem.getItemStack(lunchBoxStack, i);
                if (stack != null && stack != ItemStack.EMPTY)
                    maxSlot = i;
            }
            if (maxSlot!=null){
                boolean reverse = mouseScrollEvent.getScrollDelta() < 0;
                ItemStack activeStack;
                if (activeSlot == null) {
                    activeStack = lunchBoxItem.getItemStack(lunchBoxStack, (reverse)?maxSlot:0);
                } else {
                    activeSlot += (reverse)?-1:1;
                    if (activeSlot>maxSlot)activeSlot=0;
                    if (activeSlot<0)activeSlot=maxSlot;
                    activeStack = lunchBoxItem.getItemStack(lunchBoxStack,activeSlot);
                }
                if (activeStack != null)
                    lunchBoxItem.setActiveFood(lunchBoxStack, activeStack,true);
            }
            mouseScrollEvent.setCanceled(true);
        }
    }

}
