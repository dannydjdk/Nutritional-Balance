package com.dannyandson.nutritionalbalance.api;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Player capability to manage a player's nutrient values
 */
public interface INutritionalBalancePlayer {
    /**
     * Get the player's nutrients.
     * @return List of IPlayerNutrient objects for this player. One for each nutrient.
     */
    List<IPlayerNutrient> getPlayerNutrients();

    /**
     * Get the IPlayerNutrient object from the nutrient name.
     * @param name String name of the nutrient (corresponds with the item tag forge:nutrient/**name**)
     * @return IPlayerNutrient object
     */
    IPlayerNutrient getPlayerNutrientByName(String name);

    /**
     * Processes a change in the player's saturation level. Called whenever there is a change in the player's saturation level.
     * Probably no need to call this otherwise.
     * @param currentSaturation Player's current saturation level.
     */
    void processSaturationChange(float currentSaturation);

    /**
     * Sets the player's saved saturation level. Used to keep track of changes to saturation.
     * Called after saturation change is processed.
     * Probably no need to call this otherwise.
     * @param savedSaturation Player's current saturation level.
     */
    void resetSavedSaturation(float savedSaturation);

    /**
     * Gets the current status of the player's nutritional balance.
     * @return IPlayerNutrient.NutrientStatus object.
     */
    IPlayerNutrient.NutrientStatus getStatus();

    /**
     * Gets the last saved status of the player's nutritional balance.
     * Used to detect changes in status.
     * @return IPlayerNutrient.NutrientStatus object.
     */
    IPlayerNutrient.NutrientStatus getCachedStatus();

    /**
     * Process the ItemStack using the default method as if the player had used it.
     * @param itemStack ItemStack to process
     * @param world A world object used to query recipes to discover nutrients if necessary
     */
    void consume(ItemStack itemStack, @Nullable Level world);

    /**
     * Add to the player nutrients as if a food item was consumed.
     * @param nutrients List of nutrients to be added to the player.
     * @param health Food health points to apply as nutrients.
     * @param saturation Saturation point to apply as nutrients.
     */
    void consume(List<IPlayerNutrient> nutrients, float health, float saturation);


}
