package com.dannyandson.nutritionalbalance.nutrients;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItem;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.NutrientDataSyncTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.*;

public class WorldNutrients
{
    private static final List<Nutrient> nutrients = new ArrayList<>();
    private static final Map<Item,List<Nutrient>> nutrientMap = new HashMap<>();

    public static void register()
    {
        reset();
         //loop through nutrient/* tags
        for (TagKey<Item> tagKey: BuiltInRegistries.ITEM.getTagNames().sorted((o1,o2)->o2.location().getPath().compareTo(o1.location().getPath())).toList()) {
            ResourceLocation resourceLocation = tagKey.location();
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
    public static List<Nutrient> getNutrients(Item item, @Nullable Level world)
    {
        return getNutrients(item.getDefaultInstance(),world,1);
    }
    public static List<Nutrient> getNutrients(ItemStack itemStack, @Nullable Level world)
    {
        return getNutrients(itemStack,world,1);
    }

    private static List<Nutrient> getNutrients(ItemStack item, @Nullable Level world, int iteration) {

        if (item.getItem() instanceof LunchBoxItem lunchBoxItem){
            item = lunchBoxItem.getActiveFoodItemStack(item);
        }
        if (!nutrientMap.containsKey(item.getItem())) {

            if (world!=null && world.isClientSide()) {
                ModNetworkHandler.sendToServer(new NutrientDataSyncTrigger(item.getItem()));
            }else if(world!=null) {

                List<Nutrient> nutrientList = new ArrayList<>();

                //Get all the ItemTags for the item and find any tags with namespace:path matching forge:nutrients/*
                for (TagKey<Item> tagKey : item.getTags().sorted((o1, o2) -> o1.location().getPath().compareTo(o2.location().getPath())).toList()) {
                    ResourceLocation tag = tagKey.location();
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
                String itemRegistryName = ForgeRegistries.ITEMS.getKey(item.getItem()).getNamespace() + ":" + ForgeRegistries.ITEMS.getKey(item.getItem()).getPath();
                if (Config.LIST_CARBS.get().contains(itemRegistryName) && !nutrientList.contains(WorldNutrients.getByName("carbs"))) {
                    Nutrient nutrient = WorldNutrients.getByName("carbs");
                    if (nutrient != null)
                        nutrientList.add(nutrient);
                }
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
                if (Config.LIST_SUGARS.get().contains(itemRegistryName) && !nutrientList.contains(WorldNutrients.getByName("sugars"))) {
                    Nutrient nutrient = WorldNutrients.getByName("sugars");
                    if (nutrient != null)
                        nutrientList.add(nutrient);
                }
                if (Config.LIST_VEGETABLES.get().contains(itemRegistryName) && !nutrientList.contains(WorldNutrients.getByName("vegetables"))) {
                    Nutrient nutrient = WorldNutrients.getByName("vegetables");
                    if (nutrient != null)
                        nutrientList.add(nutrient);
                }


                //If no nutrient tags were set for this item,
                // first check if it is a meat item. If not,
                // traverse through its recipe items and check those for nutrient tags
                if (nutrientList.size() == 0 && iteration < 5) {
                    if (item.getFoodProperties(null) != null && item.getFoodProperties(null).isMeat()) {
                        Nutrient proteinNutrient = getByName("proteins");
                        if (proteinNutrient != null && !nutrientList.contains(proteinNutrient))
                            nutrientList.add(proteinNutrient);
                    } else {

                        Collection<Recipe<?>> recipes = world.getRecipeManager().getRecipes().stream().sorted((o1, o2) -> o2.getId().getPath().compareTo(o1.getId().getPath())).toList();

                        for (Recipe<?> recipe : recipes) {
                            ItemStack recipeItemStack = recipe.getResultItem(world.registryAccess());
                            if (recipeItemStack != null && recipeItemStack.getItem() == item.getItem()) {
                                NonNullList<Ingredient> ingredients = recipe.getIngredients();
                                for (Ingredient ingredient : ingredients) {
                                    ItemStack[] itemStacks = ingredient.getItems();
                                    if (itemStacks.length > 0) {
                                        List<Nutrient> ingredientNutrients = getNutrients(itemStacks[0], world, iteration + 1);
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
                nutrientMap.put(item.getItem(), nutrientList);
            }
        }

        return (nutrientMap.keySet().contains(item.getItem()))?nutrientMap.get(item.getItem()): new ArrayList<>();
    }

    public static float getEffectiveFoodQuality(FoodProperties foodItem, int numberOfNutrients)
    {
        return getEffectiveFoodQuality(foodItem.getNutrition(),foodItem.getSaturationModifier(),numberOfNutrients);
    }

    public static float getEffectiveFoodQuality(float healing, float saturation, int numberOfNutrients)
    {
        float saturation1 = healing * saturation *2;
        return Math.min(healing+saturation1, Config.NUTRIENT_MAX_FOOD_VALUE.get().floatValue()*numberOfNutrients);
    }

    public static void setItemNutrients(Item item, List<Nutrient> nutrients){
        nutrientMap.put(item,nutrients);
    }

    public static void reset() {
        nutrients.clear();
        nutrientMap.clear();
    }
}
