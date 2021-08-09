package com.dannyandson.nutritionalbalance.capabilities;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class NutrientEventHandler {

    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            NutritionalBalanceProvider provider = new NutritionalBalanceProvider();
            event.addCapability(new ResourceLocation(NutritionalBalance.MODID, "nutrients"), provider);
            event.addListener(provider::invalidate);
        }
    }

}
