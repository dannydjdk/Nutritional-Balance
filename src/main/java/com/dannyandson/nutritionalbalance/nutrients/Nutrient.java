package com.dannyandson.nutritionalbalance.nutrients;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class Nutrient {
    public String name;

    public Nutrient(String name)
    {
        this.name=name;
    }
    public List<Item> getFoodItems()
    {
        return ItemTags.getCollection().get(new ResourceLocation("forge","nutrients/"+this.name)).getAllElements();
    }

    public String getLocalizedName()
    {
        return I18n.format("nutritionalbalance.nutrient."+this.name);
    }

}
