package com.dannyandson.nutritionalbalance.capabilities;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.api.INutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class DefaultNutritionalBalancePlayer implements INutritionalBalancePlayer {

    private final List<IPlayerNutrient> playerNutrients = new ArrayList<>();
    private float savedSaturation;
    private IPlayerNutrient.NutrientStatus cachedStatus = IPlayerNutrient.NutrientStatus.SAFE;

    public DefaultNutritionalBalancePlayer()
    {
        for (Nutrient nutrient:WorldNutrients.get())
        {
            this.playerNutrients.add(new DefaultPlayerNutrient(nutrient));
        }
    }

    @Override
    public List<IPlayerNutrient> getPlayerNutrients()
    {
        return this.playerNutrients;
    }

    @Override
    public IPlayerNutrient getPlayerNutrientByName(String name) {
        for (IPlayerNutrient iPlayerNutrient:this.playerNutrients)
        {
            if (iPlayerNutrient.getNutrient().name.equals(name))
                return iPlayerNutrient;
        }
        return null;
    }

    public void processSaturationChange(float currentSaturation)
    {
        if (currentSaturation==this.savedSaturation)
            return;

        if (currentSaturation<this.savedSaturation)
        {
            float decrementer = Config.NUTRIENT_DECAY_RATE.get().floatValue()*(currentSaturation-savedSaturation)/this.playerNutrients.size();
            for (IPlayerNutrient playernutrient:this.playerNutrients )
            {
                playernutrient.changeValue(decrementer);
            }
        }

        this.resetSavedSaturation(currentSaturation);
    }
    public void resetSavedSaturation(float savedSaturation)
    {
        this.savedSaturation = savedSaturation;
    }

    @Override
    public void consume(ItemStack itemStack, Level world) {
        Item item = itemStack.getItem();
        FoodProperties food = item.getFoodProperties();

        if (food!=null) {
            List<Nutrient> nutrients = WorldNutrients.getNutrients(item, world);
            for (Nutrient nutrient : nutrients) {
                float nutrientUnits = WorldNutrients.getEffectiveFoodQuality(food, nutrients.size()) * Config.NUTRIENT_INCREMENT_RATE.get().floatValue() / nutrients.size();
                this.getPlayerNutrientByName(nutrient.name).changeValue(nutrientUnits);
            }
        }
    }

    @Override
    public void consume(List<IPlayerNutrient> nutrients, float health, float saturation) {
        for (IPlayerNutrient nutrient:nutrients) {
            float nutrientUnits=WorldNutrients.getEffectiveFoodQuality(health, saturation, nutrients.size()) * Config.NUTRIENT_INCREMENT_RATE.get().floatValue() / nutrients.size();
            this.getPlayerNutrientByName(nutrient.getNutrientName()).changeValue(nutrientUnits);
        }
    }

    public IPlayerNutrient.NutrientStatus getStatus()
    {
        boolean safeFound = false;
        for (IPlayerNutrient playernutrient : this.playerNutrients)
        {
            IPlayerNutrient.NutrientStatus nutrientStatus = playernutrient.getStatus();
            if (nutrientStatus==IPlayerNutrient.NutrientStatus.MALNOURISHED) {
                this.cachedStatus =IPlayerNutrient.NutrientStatus.MALNOURISHED;
                return IPlayerNutrient.NutrientStatus.MALNOURISHED;
            }
            if (nutrientStatus==IPlayerNutrient.NutrientStatus.ENGORGED) {
                this.cachedStatus =IPlayerNutrient.NutrientStatus.ENGORGED;
                return IPlayerNutrient.NutrientStatus.ENGORGED;
            }
            if (nutrientStatus==IPlayerNutrient.NutrientStatus.SAFE) {
                safeFound = true;
            }
        }

        if (!safeFound) {
            this.cachedStatus =IPlayerNutrient.NutrientStatus.ON_TARGET;
            return IPlayerNutrient.NutrientStatus.ON_TARGET;
        }
        else {
            this.cachedStatus =IPlayerNutrient.NutrientStatus.SAFE;
            return IPlayerNutrient.NutrientStatus.SAFE;
        }
    }

    public IPlayerNutrient.NutrientStatus getCachedStatus()
    {
        return this.cachedStatus;
    }
}
