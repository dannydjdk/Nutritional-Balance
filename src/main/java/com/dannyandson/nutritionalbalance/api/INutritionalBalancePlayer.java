package com.dannyandson.nutritionalbalance.api;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.jspecify.annotations.Nullable;
import java.util.List;

/**
 * Player capability to manage a player's nutrient values
 */
public interface INutritionalBalancePlayer {
    List<IPlayerNutrient> getPlayerNutrients();
    IPlayerNutrient getPlayerNutrientByName(String name);
    void processSaturationChange(float currentSaturation);
    void resetSavedSaturation(float savedSaturation);
    IPlayerNutrient.NutrientStatus getStatus();
    IPlayerNutrient.NutrientStatus getCachedStatus();
    void consume(ItemStack itemStack, @Nullable Level world);
    void consume(List<IPlayerNutrient> nutrients, float health, float saturation);
}
