package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.effects.ModMobAffects;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.PlayerSync;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;


/*
If saturation has decreased, decrease nutrients relative to that decrease.
Call on finish eating event and reset the saturation value after eating.
*/

public class EventPlayerTick {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player playerEntity = event.player;
            INutritionalBalancePlayer iNutritionalBalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(playerEntity);

            if (!NutritionalBalance.modEffectsLoaded)
                ModMobAffects.loadModEffects();

            if (!playerEntity.level.isClientSide) {
                float playerSaturation = playerEntity.getFoodData().getSaturationLevel();
                int playerFoodLevel = playerEntity.getFoodData().getFoodLevel();
                float foodpoints = playerSaturation + playerFoodLevel;
                iNutritionalBalancePlayer.processSaturationChange(foodpoints);
            }

            if (event.player.tickCount % 200 == 0) {

                IPlayerNutrient.NutrientStatus cachedStatus = iNutritionalBalancePlayer.getCachedStatus();
                IPlayerNutrient.NutrientStatus currentStatus = iNutritionalBalancePlayer.getStatus();

                if (event.side == LogicalSide.SERVER) {
                    MobEffectInstance nourished = playerEntity.getEffect(NutritionalBalance.NOURISHED_EFFECT.get()),
                            malnourished = playerEntity.getEffect(NutritionalBalance.MALNOURISHED_EFFECT.get()),
                            engorged = playerEntity.getEffect(NutritionalBalance.ENGORGED_EFFECT.get());

                    if (currentStatus == IPlayerNutrient.NutrientStatus.ENGORGED) {
                        if (nourished != null)
                            playerEntity.removeEffect(NutritionalBalance.NOURISHED_EFFECT.get());
                        if (malnourished != null)
                            playerEntity.removeEffect(NutritionalBalance.MALNOURISHED_EFFECT.get());
                        if (engorged == null)
                            playerEntity.addEffect(new MobEffectInstance(NutritionalBalance.ENGORGED_EFFECT.get(), Integer.MAX_VALUE, 0, true, false, true));

                    } else if (currentStatus == IPlayerNutrient.NutrientStatus.MALNOURISHED) {
                        if (nourished != null)
                            playerEntity.removeEffect(NutritionalBalance.NOURISHED_EFFECT.get());
                        if (malnourished == null)
                            playerEntity.addEffect(new MobEffectInstance(NutritionalBalance.MALNOURISHED_EFFECT.get(), Integer.MAX_VALUE, 0, true, false, true));
                        if (engorged != null)
                            playerEntity.removeEffect(NutritionalBalance.ENGORGED_EFFECT.get());

                    } else if (currentStatus == IPlayerNutrient.NutrientStatus.ON_TARGET) {
                        if (nourished == null)
                            playerEntity.addEffect(new MobEffectInstance(NutritionalBalance.NOURISHED_EFFECT.get(), Integer.MAX_VALUE, 0, true, false, true));
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

                        PlayerSync playerSync = new PlayerSync(iNutritionalBalancePlayer);
                        ModNetworkHandler.sendToClient(playerSync, (ServerPlayer) playerEntity);

                    }

                }
            }
        }
    }
}
