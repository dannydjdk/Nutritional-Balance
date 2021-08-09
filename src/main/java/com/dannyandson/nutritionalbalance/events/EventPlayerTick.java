package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.PlayerSync;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


/*
If saturation has decreased, decrease nutrients relative to that decrease.
Call on finish eating event and reset the saturation value after eating.
*/

public class EventPlayerTick {

    private int i = 0;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase==TickEvent.Phase.END) {
            Player playerEntity = event.player;

            float playerSaturation = playerEntity.getFoodData().getSaturationLevel();
            int playerFoodLevel = playerEntity.getFoodData().getFoodLevel();
            float foodpoints = playerSaturation + playerFoodLevel;
            playerEntity.getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(inutritionalbalancePlayer -> {
                inutritionalbalancePlayer.processSaturationChange(foodpoints);
            });

            if (i >= 200) {
                playerEntity.getCapability(CapabilityNutritionalBalancePlayer.HEALTHY_DIET_PLAYER_CAPABILITY).ifPresent(inutritionalbalancePlayer -> {
                    IPlayerNutrient.NutrientStatus cachedStatus = inutritionalbalancePlayer.getCachedStatus();
                    IPlayerNutrient.NutrientStatus currentStatus = inutritionalbalancePlayer.getStatus();
                    if (currentStatus== IPlayerNutrient.NutrientStatus.ENGORGED) {
                        //slowness
                        playerEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,200, 0, true, true));
                        //mining fatigue
                        playerEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,200, 0, true, true));
                    }
                    else if (currentStatus== IPlayerNutrient.NutrientStatus.MALNOURISHED)
                    {
                        //mining fatigue
                        playerEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,200, 0, true, true));
                        //weakness
                        playerEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,200, 0, true, true));
                    }
                    else if (currentStatus== IPlayerNutrient.NutrientStatus.ON_TARGET)
                    {
                        //speed
                        playerEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,200, 0, true, true));
                        //haste
                        playerEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,200, 0, true, true));
                        //strength
                        playerEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,200, 0, true, true));
                    }

                    if (cachedStatus != currentStatus)
                    {
                        if (playerEntity.level.isClientSide()) {
                            /*
                            playerEntity.sendStatusMessage(ITextComponent.getTextComponentOrEmpty(I18n.format("nutritionalbalance.nutrientstatus.msg." + currentStatus.name())), true);
                            Minecraft.getInstance().getToastGui().add(
                                    new SystemToast(
                                            SystemToast.Type.TUTORIAL_HINT,
                                            ITextComponent.getTextComponentOrEmpty(I18n.format("nutritionalbalance.nutrientstatus." + currentStatus.name())),
                                            ITextComponent.getTextComponentOrEmpty(I18n.format("nutritionalbalance.nutrientstatus.msg." + currentStatus.name()))
                                    )
                            );

                             */
                        }
                        else {

                            PlayerSync playerSync = new PlayerSync(new ResourceLocation(NutritionalBalance.MODID, "playersync"),inutritionalbalancePlayer);
                            ModNetworkHandler.sendToClient(playerSync, (ServerPlayer) playerEntity);

                        }

                    }
                });

                i = 0;
            }
            i++;
        }
    }
}
