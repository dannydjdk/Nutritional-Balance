package com.dannyandson.nutritionalbalance.capabilities;

import com.dannyandson.nutritionalbalance.nutrients.Nutrient;

public interface IPlayerNutrient {

    //get the Nutrient object for this player nutrient
    Nutrient getNutrient();

    //get the name of the nutrient for this player nutrient
    String getNutrientName();

    //get the amount of the nutrient
    float getValue();

    //increase or decrease the amount of nutrient
    void changeValue(float i);

    //set the value to a specific number
    void setValue(float i);

    public enum NutrientStatus {MALNOURISHED,SAFE,ON_TARGET,ENGORGED};
    NutrientStatus getStatus();

}
