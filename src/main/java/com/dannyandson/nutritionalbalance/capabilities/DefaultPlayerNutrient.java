package com.dannyandson.nutritionalbalance.capabilities;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.api.IPlayerNutrient;
import com.dannyandson.nutritionalbalance.nutrients.Nutrient;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import net.minecraft.client.resources.I18n;
import java.util.List;

public class DefaultPlayerNutrient implements IPlayerNutrient
{
    private final String nutrientname;
    private float value;

    public DefaultPlayerNutrient(Nutrient nutrient)
    {
        this.nutrientname=nutrient.name;
        this.value= Config.NUTRIENT_INITIAL.get().floatValue();
    }

    @Override
    public Nutrient getNutrient() {
        return WorldNutrients.getByName(nutrientname);
    }

    @Override
    public String getNutrientName() { return this.nutrientname;}

    public String getLocalizedName()
    {
        return I18n.get("nutritionalbalance.nutrient."+this.nutrientname);
    }

    @Override
    public float getValue() {
        return this.value;
    }

    @Override
    public void changeValue(float i) {
         this.value+=i;
         if (this.value>Config.NUTRIENT_MAX.get().floatValue())
             this.value=Config.NUTRIENT_MAX.get().floatValue();
         else if (this.value<0)
             this.value=0;
    }

    @Override
    public void setValue(float value) {
        this.value=value;
    }

    @Override
    public NutrientStatus getStatus()
    {
        boolean badNutrient = false;
        for(String badNutrientName:((List<String>)Config.BAD_NUTRIENTS.get())){
            if (badNutrientName.equals(this.nutrientname))
                badNutrient=true;
        }
        boolean goodNutrient = false;
        for(String goodNutrientName:((List<String>)Config.GOOD_NUTRIENTS.get())){
            if (goodNutrientName.equals(this.nutrientname))
                goodNutrient=true;
        }

        if (this.value<Config.NUTRIENT_MALNOURISHED.get().floatValue() && !badNutrient)
            return NutrientStatus.MALNOURISHED;

        if (this.value>Config.NUTRIENT_ENGORGED.get().floatValue() && !goodNutrient)
            return NutrientStatus.ENGORGED;

        if ((this.value>Config.NUTRIENT_LOW_TARGET.get().floatValue() && this.value<Config.NUTRIENT_TARGET_HIGH.get())
                || ( goodNutrient && this.value>Config.NUTRIENT_LOW_TARGET.get().floatValue() )
                || ( badNutrient && this.value<Config.NUTRIENT_TARGET_HIGH.get() )
        )
            return NutrientStatus.ON_TARGET;

        return NutrientStatus.SAFE;
    }
}
