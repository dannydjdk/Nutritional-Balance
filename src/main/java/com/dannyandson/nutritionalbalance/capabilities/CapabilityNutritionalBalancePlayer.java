package com.dannyandson.nutritionalbalance.capabilities;

import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityNutritionalBalancePlayer {
    @CapabilityInject(INutritionalBalancePlayer.class)
    public static Capability<INutritionalBalancePlayer> HEALTHY_DIET_PLAYER_CAPABILITY = null;

    public static void register()
    {
        CapabilityManager.INSTANCE.register(INutritionalBalancePlayer.class);
    }

}
