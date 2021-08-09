package com.dannyandson.nutritionalbalance.nutrients;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import java.util.List;

public class Nutrient {
    public String name;

    public Nutrient(String name)
    {
        this.name=name;
    }
    public List<Item> getFoodItems()
    {
        return ItemTags.getAllTags().getTag(new ResourceLocation("forge","nutrients/"+this.name)).getValues();
    }

    public String getLocalizedName()
    {
        return I18n.get("nutritionalbalance.nutrient."+this.name);
    }

}
