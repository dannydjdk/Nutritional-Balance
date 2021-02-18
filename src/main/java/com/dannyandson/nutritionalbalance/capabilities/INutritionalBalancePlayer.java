package com.dannyandson.nutritionalbalance.capabilities;

import java.util.List;

public interface INutritionalBalancePlayer {
    List<IPlayerNutrient> getPlayerNutrients();
    IPlayerNutrient getPlayerNutrientByName(String name);
    void processSaturationChange(float currentSaturation);
    void resetSavedSaturation(float savedSaturation);
    IPlayerNutrient.NutrientStatus getStatus();
    IPlayerNutrient.NutrientStatus getCachedStatus();
}
