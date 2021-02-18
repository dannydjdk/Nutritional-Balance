package com.dannyandson.nutritionalbalance.capabilities;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class NutrientEventHandler {

    public static void onAttachCapabilitiesEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity) {
            NutritionalBalanceProvider provider = new NutritionalBalanceProvider();
            event.addCapability(new ResourceLocation(NutritionalBalance.MODID, "nutrients"), provider);
            event.addListener(provider::invalidate);
        }
    }

}
