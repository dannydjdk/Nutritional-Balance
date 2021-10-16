package com.dannyandson.nutritionalbalance.api;

import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.CheckForNull;
import java.util.concurrent.atomic.AtomicReference;

public class NutritionalBalancePlayerHelper {
    @CheckForNull
    public static INutritionalBalancePlayer getInutritionalBalancePlayer(PlayerEntity player)
    {
        AtomicReference<INutritionalBalancePlayer> iNutritionalBalancePlayer = new AtomicReference<>();
        player.getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(iNutritionalBalancePlayer::set);
        return iNutritionalBalancePlayer.get();
    }
}
