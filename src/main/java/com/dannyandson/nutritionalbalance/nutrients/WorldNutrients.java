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

import javax.annotation.Nullable;
import java.util.*;

public class WorldNutrients
{
    private static final List<Nutrient> nutrients = new ArrayList<>();
    private static final Map<Item,List<Nutrient>> nutrientMap = new HashMap<>();

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
    public static List<Nutrient> getNutrients(Item item, @Nullable World world)
    {
        return getNutrients(item,world,1);
    }

    private static List<Nutrient> getNutrients(Item item, @Nullable World world, int iteration) {

        if (!nutrientMap.containsKey(item)) {

            List<Nutrient> nutrientList = new ArrayList<>();

            //Get all the ItemTags for the item and find any tags with namespace:path matching forge:nutrients/*
            Collection<ResourceLocation> tags = ItemTags.getCollection().getOwningTags(item);
            for (ResourceLocation tag : tags) {
                Nutrient nutrient = null;
                if (tag.getNamespace().equals("forge") && tag.getPath().startsWith("nutrients/")) {
                    nutrient = WorldNutrients.getByName(tag.getPath().substring(10));
                }
                //Check against tags in config file
                else if (Config.LIST_Fruits.get().contains("#" + tag.getNamespace() + ":" + tag.getPath()) && !nutrientList.contains(WorldNutrients.getByName("fruits"))) {
                    nutrient = WorldNutrients.getByName("fruits");
                } else if (Config.LIST_PROTEINS.get().contains("#" + tag.getNamespace() + ":" + tag.getPath()) && !nutrientList.contains(WorldNutrients.getByName("proteins"))) {
                    nutrient = WorldNutrients.getByName("proteins");
                } else if (Config.LIST_CARBS.get().contains("#" + tag.getNamespace() + ":" + tag.getPath()) && !nutrientList.contains(WorldNutrients.getByName("carbs"))) {
                    nutrient = WorldNutrients.getByName("carbs");
                } else if (Config.LIST_VEGETABLES.get().contains("#" + tag.getNamespace() + ":" + tag.getPath()) && !nutrientList.contains(WorldNutrients.getByName("vegetables"))) {
                    nutrient = WorldNutrients.getByName("vegetables");
                } else if (Config.LIST_SUGARS.get().contains("#" + tag.getNamespace() + ":" + tag.getPath()) && !nutrientList.contains(WorldNutrients.getByName("sugars"))) {
                    nutrient = WorldNutrients.getByName("sugars");
                }

                if (nutrient != null && !nutrientList.contains(nutrient))
                    nutrientList.add(nutrient);

            }

            //Check nutrient lists from configs
            String itemRegistryName = item.getRegistryName().getNamespace() + ":" + item.getRegistryName().getPath();
            if (Config.LIST_Fruits.get().contains(itemRegistryName) && !nutrientList.contains(WorldNutrients.getByName("fruits"))) {
                Nutrient nutrient = WorldNutrients.getByName("fruits");
                if (nutrient != null)
                    nutrientList.add(nutrient);
            }
            if (Config.LIST_PROTEINS.get().contains(itemRegistryName) && !nutrientList.contains(WorldNutrients.getByName("proteins"))) {
                Nutrient nutrient = WorldNutrients.getByName("proteins");
                if (nutrient != null)
                    nutrientList.add(nutrient);
            }
            if (Config.LIST_CARBS.get().contains(itemRegistryName) && !nutrientList.contains(WorldNutrients.getByName("carbs"))) {
                Nutrient nutrient = WorldNutrients.getByName("carbs");
                if (nutrient != null)
                    nutrientList.add(nutrient);
            }
            if (Config.LIST_VEGETABLES.get().contains(itemRegistryName) && !nutrientList.contains(WorldNutrients.getByName("vegetables"))) {
                Nutrient nutrient = WorldNutrients.getByName("vegetables");
                if (nutrient != null)
                    nutrientList.add(nutrient);
            }
            if (Config.LIST_SUGARS.get().contains(itemRegistryName) && !nutrientList.contains(WorldNutrients.getByName("sugars"))) {
                Nutrient nutrient = WorldNutrients.getByName("sugars");
                if (nutrient != null)
                    nutrientList.add(nutrient);
            }


            //If no nutrient tags were set for this item,
            // first check if it is a meat item. If not,
            // traverse through it's recipe items and check those for nutrient tags
            if (nutrientList.size() == 0 && iteration < 5) {
                if (item.getFood() != null && item.getFood().isMeat()) {
                    Nutrient proteinNutrient = getByName("proteins");
                    if (proteinNutrient != null && !nutrientList.contains(proteinNutrient))
                        nutrientList.add(proteinNutrient);
                } else if (world != null) {

                    Collection<IRecipe<?>> recipes = world.getRecipeManager().getRecipes();

                    for (IRecipe<?> recipe : recipes) {
                        if (recipe.getRecipeOutput().getItem() == item) {
                            NonNullList<Ingredient> ingredients = recipe.getIngredients();
                            for (Ingredient ingredient : ingredients) {
                                ItemStack[] itemStacks = ingredient.getMatchingStacks();
                                if (itemStacks.length > 0) {
                                    List<Nutrient> ingredientNutrients = getNutrients(itemStacks[0].getItem(), world, iteration + 1);
                                    for (Nutrient ingredientNutrient : ingredientNutrients) {
                                        if (!nutrientList.contains(ingredientNutrient)) {
                                            nutrientList.add(ingredientNutrient);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
            nutrientMap.put(item, nutrientList);
        }

        return nutrientMap.get(item);
    }

    public static float getEffectiveFoodQuality(Food foodItem)
    {
        return getEffectiveFoodQuality(foodItem.getHealing(),foodItem.getSaturation());
    }

    public static float getEffectiveFoodQuality(float healing, float saturation)
    {
        float saturation1 = healing * saturation *2;
        return healing+saturation1;
    }

    public static void reset() {
        nutrients.clear();
        nutrientMap.clear();
    }
}
