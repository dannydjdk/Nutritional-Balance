package com.dannyandson.nutritionalbalance.nutrients;

import com.dannyandson.nutritionalbalance.Config;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITagCollection;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WorldNutrients
{
    private static List<Nutrient> nutrients = new ArrayList<>();

    public static void register()
    {
        reset();
        ITagCollection<Item> itemITagCollection = ItemTags.getCollection();
        Collection<ResourceLocation> resourceLocationCollection =  itemITagCollection.getRegisteredTags();
        //loop through nutrient/* tags
        for (ResourceLocation resourceLocation : resourceLocationCollection)
        {
            String namespace = resourceLocation.getNamespace();
            String path = resourceLocation.getPath();

            if (namespace.equals("forge") && path.startsWith("nutrients/"))
            {
                nutrients.add(new Nutrient(path.substring(10)));
            }
        }

        //add meat nutrient if not already added because minecraft already flags meats, so itemtags are not necessary
        if (nutrients.size()>0 && getByName("proteins")==null)
            nutrients.add(new Nutrient("proteins"));

    }

    // Return all nutrients
    public static List<Nutrient> get() {
        if (nutrients.size()==0)
        {
            register();
        }
        return nutrients;
    }

    // Return nutrient by name (null if not found)
    public static Nutrient getByName(String name) {
        for (Nutrient nutrient : get()) {
            if (nutrient.name.equals(name))
                return nutrient;
        }
        return null;
    }


    //Return nutrients of a food item
    public static List<Nutrient> getNutrients(Item item, World world)
    {
        return getNutrients(item,world,1);
    }

    private static List<Nutrient> getNutrients(Item item, World world, int iteration)
    {
        List<Nutrient> nutrientList = new ArrayList<>();

        //Get all the ItemTags for the item and find any tags with namespace:path matching forge:nutrients/*
        Collection<ResourceLocation> tags = ItemTags.getCollection().getOwningTags(item);
        for (ResourceLocation tag: tags)
        {
                if (tag.getNamespace().equals("forge") && tag.getPath().startsWith("nutrients/"))
                {
                    Nutrient nutrient = WorldNutrients.getByName(tag.getPath().substring(10));
                    if (nutrient!=null && !nutrientList.contains(nutrient))
                        nutrientList.add(nutrient);
                }
                //Check against tags in config file
                else if (Config.LIST_Fruits.get().contains("#"+tag.getNamespace()+":"+tag.getPath()) && !nutrientList.contains(WorldNutrients.getByName("fruits")))
                {
                    nutrientList.add(WorldNutrients.getByName("fruits"));
                }
                else if (Config.LIST_PROTEINS.get().contains("#"+tag.getNamespace()+":"+tag.getPath()) && !nutrientList.contains(WorldNutrients.getByName("proteins")))
                {
                    nutrientList.add(WorldNutrients.getByName("proteins"));
                }
                else if (Config.LIST_CARBS.get().contains("#"+tag.getNamespace()+":"+tag.getPath()) && !nutrientList.contains(WorldNutrients.getByName("carbs")))
                {
                    nutrientList.add(WorldNutrients.getByName("carbs"));
                }
                else if (Config.LIST_VEGETABLES.get().contains("#"+tag.getNamespace()+":"+tag.getPath()) && !nutrientList.contains(WorldNutrients.getByName("vegetables")))
                {
                    nutrientList.add(WorldNutrients.getByName("vegetables"));
                }
                else if (Config.LIST_SUGARS.get().contains("#"+tag.getNamespace()+":"+tag.getPath()) && !nutrientList.contains(WorldNutrients.getByName("sugars")))
                {
                    nutrientList.add(WorldNutrients.getByName("sugars"));
                }
        }

        //Check nutrient lists from configs
        String itemRegistryName = item.getRegistryName().getNamespace() + ":" + item.getRegistryName().getPath();
        if (Config.LIST_Fruits.get().contains(itemRegistryName) && !nutrientList.contains(WorldNutrients.getByName("fruits")))
            nutrientList.add(WorldNutrients.getByName("fruits"));
        if (Config.LIST_PROTEINS.get().contains(itemRegistryName) && !nutrientList.contains(WorldNutrients.getByName("proteins")))
            nutrientList.add(WorldNutrients.getByName("proteins"));
        if (Config.LIST_CARBS.get().contains(itemRegistryName) && !nutrientList.contains(WorldNutrients.getByName("carbs")))
            nutrientList.add(WorldNutrients.getByName("carbs"));
        if (Config.LIST_VEGETABLES.get().contains(itemRegistryName) && !nutrientList.contains(WorldNutrients.getByName("vegetables")))
            nutrientList.add(WorldNutrients.getByName("vegetables"));
        if (Config.LIST_SUGARS.get().contains(itemRegistryName) && !nutrientList.contains(WorldNutrients.getByName("sugars")))
            nutrientList.add(WorldNutrients.getByName("vegetables"));


        //If no nutrient tags were set for this item,
        // first check if it is a meat item. If not,
        // traverse through it's recipe items and check those for nutrient tags
        if (nutrientList.size()==0 && iteration<5)
        {
            if (item.getFood() != null && item.getFood().isMeat())
            {
                Nutrient proteinNutrient = getByName("proteins");
                if (proteinNutrient != null && !nutrientList.contains(proteinNutrient))
                    nutrientList.add(proteinNutrient);
            }else if (world!=null){

                Collection<IRecipe<?>> recipes = world.getRecipeManager().getRecipes();

                for (IRecipe<?> recipe : recipes) {
                    if (recipe.getRecipeOutput().getItem() == item) {
                        NonNullList<Ingredient> ingredients = recipe.getIngredients();
                        for (Ingredient ingredient : ingredients) {
                            ItemStack[] itemStacks = ingredient.getMatchingStacks();
                            if (itemStacks.length > 0) {
                                List<Nutrient> ingredientNutrients = getNutrients(itemStacks[0].getItem(), world,iteration + 1);
                                for (Nutrient ingredientNutrient : ingredientNutrients) {
                                    if (!nutrientList.contains(ingredientNutrient)) {
                                        nutrientList.add(ingredientNutrient);
                                    }
                                }
                            }
                        }
                        String s = recipe.toString();
                    }
                }

            }
        }

        return nutrientList;
    }

    public static float getEffectiveFoodQuality(Food foodItem)
    {
        float foodPoints = foodItem.getHealing();
        float saturation = foodPoints * foodItem.getSaturation() *2;
        return foodPoints+saturation;
    }

    public static void reset() {
        nutrients.clear();
    }
}
