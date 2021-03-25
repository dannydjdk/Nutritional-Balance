package com.dannyandson.nutritionalbalance.api;

import com.dannyandson.nutritionalbalance.nutrients.Nutrient;

/**
 * Corresponds to a single nutrient for a player.
 * Each player will have one of these objects for each nutrient defined.
 */
public interface IPlayerNutrient {

    /**
     * get the Nutrient object for this player nutrient
     * @return Nutrient object.
     */
    Nutrient getNutrient();

    /**
     * get the name of the nutrient for this player nutrient
     * @return String name of the nutrient.
     */
    String getNutrientName();

    /**
     * get the amount of the nutrient
     * @return Returns the nutritional units (NUs) for this nutrient for this player.
     */
    float getValue();

    /**
     * Increments or decrements the nutritional units for this nutrient for this player
     * @param i How many NUs to add or subtract (with a negative number)
     */
    void changeValue(float i);

    /**
     * Set the nutritional units (NUs) value for this nutrient for this player to a specific number
     * @param i value to set the player's NUs
     */
    void setValue(float i);

    enum NutrientStatus {MALNOURISHED,SAFE,ON_TARGET,ENGORGED}

    /**
     * Get the player's current status with this nutrient
     * @return a NutrientStatus enum
     */
    NutrientStatus getStatus();

}
