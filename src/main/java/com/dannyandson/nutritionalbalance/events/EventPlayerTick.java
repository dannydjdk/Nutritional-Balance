package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.PlayerSync;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


/*
If saturation has decreased, decrease nutrients relative to that decrease.
Call on finish eating event and reset the saturation value after eating.
*/

public class EventPlayerTick {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase==TickEvent.Phase.END) {
            PlayerEntity playerEntity = event.player;

            if (!playerEntity.level.isClientSide) {
                float playerSaturation = playerEntity.getFoodData().getSaturationLevel();
                int playerFoodLevel = playerEntity.getFoodData().getFoodLevel();
                float foodpoints = playerSaturation + playerFoodLevel;
                playerEntity.getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(inutritionalbalancePlayer -> {
                    inutritionalbalancePlayer.processSaturationChange(foodpoints);
                });
            }

            if (event.player.tickCount % 200 == 0) {
                playerEntity.getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(inutritionalbalancePlayer -> {
                    IPlayerNutrient.NutrientStatus cachedStatus = inutritionalbalancePlayer.getCachedStatus();
                    IPlayerNutrient.NutrientStatus currentStatus = inutritionalbalancePlayer.getStatus();
                   
                    if(!playerEntity.level.isClientSide){
                    
                    EffectInstance nourished = playerEntity.getEffect(NutritionalBalance.NOURISHED_EFFECT.get()),
                            malnourished = playerEntity.getEffect(NutritionalBalance.MALNOURISHED_EFFECT.get()),
                            engorged = playerEntity.getEffect(NutritionalBalance.ENGORGED_EFFECT.get());

                    if (currentStatus == IPlayerNutrient.NutrientStatus.ENGORGED) {
                        if (nourished != null)
                            playerEntity.removeEffect(NutritionalBalance.NOURISHED_EFFECT.get());
                        if (malnourished != null)
                            playerEntity.removeEffect(NutritionalBalance.MALNOURISHED_EFFECT.get());
                        if (engorged == null)
                            playerEntity.addEffect(new EffectInstance(NutritionalBalance.ENGORGED_EFFECT.get(), Integer.MAX_VALUE, 0, true, false, true));

                    } else if (currentStatus == IPlayerNutrient.NutrientStatus.MALNOURISHED) {
                        if (nourished != null)
                            playerEntity.removeEffect(NutritionalBalance.NOURISHED_EFFECT.get());
                        if (malnourished == null)
                            playerEntity.addEffect(new EffectInstance(NutritionalBalance.MALNOURISHED_EFFECT.get(), Integer.MAX_VALUE, 0, true, false, true));
                        if (engorged != null)
                            playerEntity.removeEffect(NutritionalBalance.ENGORGED_EFFECT.get());

                    } else if (currentStatus == IPlayerNutrient.NutrientStatus.ON_TARGET) {
                        if (nourished == null)
                            playerEntity.addEffect(new EffectInstance(NutritionalBalance.NOURISHED_EFFECT.get(), Integer.MAX_VALUE, 0, true, false, true));
                        if (malnourished != null)
                            playerEntity.removeEffect(NutritionalBalance.MALNOURISHED_EFFECT.get());
                        if (engorged != null)
                            playerEntity.removeEffect(NutritionalBalance.ENGORGED_EFFECT.get());

                    } else {
                        if (nourished != null)
                            playerEntity.removeEffect(NutritionalBalance.NOURISHED_EFFECT.get());
                        if (malnourished != null)
                            playerEntity.removeEffect(NutritionalBalance.MALNOURISHED_EFFECT.get());
                        if (engorged != null)
                            playerEntity.removeEffect(NutritionalBalance.ENGORGED_EFFECT.get());
                    }
                    }

                    if (cachedStatus != currentStatus) {
                        if (playerEntity.level.isClientSide()) {
                            //playerEntity.sendStatusMessage(ITextComponent.getTextComponentOrEmpty(I18n.format("nutritionalbalance.nutrientstatus.msg." + currentStatus.name())), true);
                            if (Config.SHOW_THRESHOLD_TOAST.get())
                                ClientHelpers.showStatusToast(currentStatus.name());
                        } else {

                            PlayerSync playerSync = new PlayerSync(inutritionalbalancePlayer);
                            ModNetworkHandler.sendToClient(playerSync, (ServerPlayerEntity) playerEntity);

                        }

                    }
                });

            
            }
        }
    }
}
