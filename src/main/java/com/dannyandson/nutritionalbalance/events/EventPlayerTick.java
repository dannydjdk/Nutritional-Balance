package com.dannyandson.nutritionalbalance.events;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.effects.ModMobAffects;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.PlayerSync;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.dannyandson.nutritionalbalance.setup.Registration;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class EventPlayerTick {

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent.Post event) {
        Player playerEntity = event.getEntity();
        INutritionalBalancePlayer iNutritionalBalancePlayer = PlayerNutritionData.getWorldNutritionData().getNutritionalBalancePlayer(playerEntity);

        if (!NutritionalBalance.modEffectsLoaded)
            ModMobAffects.loadModEffects();

        if (!playerEntity.level().isClientSide) {
            float playerSaturation = playerEntity.getFoodData().getSaturationLevel();
            int playerFoodLevel = playerEntity.getFoodData().getFoodLevel();
            float foodpoints = playerSaturation + playerFoodLevel;
            iNutritionalBalancePlayer.processSaturationChange(foodpoints);
        }

        if (playerEntity.tickCount % 200 == 0) {

            IPlayerNutrient.NutrientStatus cachedStatus = iNutritionalBalancePlayer.getCachedStatus();
            IPlayerNutrient.NutrientStatus currentStatus = iNutritionalBalancePlayer.getStatus();

            if (!playerEntity.level().isClientSide) {
                Holder<MobEffect> nourishedHolder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Registration.NOURISHED_EFFECT.get());
                Holder<MobEffect> malnourishedHolder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Registration.MALNOURISHED_EFFECT.get());
                Holder<MobEffect> engorgedHolder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(Registration.ENGORGED_EFFECT.get());

                MobEffectInstance nourished = playerEntity.getEffect(nourishedHolder),
                        malnourished = playerEntity.getEffect(malnourishedHolder),
                        engorged = playerEntity.getEffect(engorgedHolder);

                if (currentStatus == IPlayerNutrient.NutrientStatus.ENGORGED) {
                    if (nourished != null)
                        playerEntity.removeEffect(nourishedHolder);
                    if (malnourished != null)
                        playerEntity.removeEffect(malnourishedHolder);
                    if (engorged == null)
                        playerEntity.addEffect(new MobEffectInstance(engorgedHolder, Integer.MAX_VALUE, 0, true, false, true));

                } else if (currentStatus == IPlayerNutrient.NutrientStatus.MALNOURISHED) {
                    if (nourished != null)
                        playerEntity.removeEffect(nourishedHolder);
                    if (malnourished == null)
                        playerEntity.addEffect(new MobEffectInstance(malnourishedHolder, Integer.MAX_VALUE, 0, true, false, true));
                    if (engorged != null)
                        playerEntity.removeEffect(engorgedHolder);

                } else if (currentStatus == IPlayerNutrient.NutrientStatus.ON_TARGET) {
                    if (nourished == null)
                        playerEntity.addEffect(new MobEffectInstance(nourishedHolder, Integer.MAX_VALUE, 0, true, false, true));
                    if (malnourished != null)
                        playerEntity.removeEffect(malnourishedHolder);
                    if (engorged != null)
                        playerEntity.removeEffect(engorgedHolder);

                } else {
                    if (nourished != null)
                        playerEntity.removeEffect(nourishedHolder);
                    if (malnourished != null)
                        playerEntity.removeEffect(malnourishedHolder);
                    if (engorged != null)
                        playerEntity.removeEffect(engorgedHolder);
                }
            }

            if (cachedStatus != currentStatus) {
                if (playerEntity.level().isClientSide()) {
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
