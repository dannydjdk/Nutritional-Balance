package com.dannyandson.nutritionalbalance.nutrients;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItem;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.NutrientDataSyncTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;

import org.jspecify.annotations.Nullable;
import java.util.*;

public class WorldNutrients
{
    private static final List<Nutrient> nutrients = new ArrayList<>();
    private static final Map<Item,List<Nutrient>> nutrientMap = new HashMap<>();

    public static void register()
    {
        // Just clear caches here — during TagsUpdatedEvent, item components
        // aren't bound yet so getDefaultInstance() would crash.
        // Actual nutrient discovery is deferred to get() → discoverNutrients().
        reset();
    }

    public static List<Nutrient> get() {
        if (nutrients.size() == 0) {
            discoverNutrients();
        }
        return nutrients;
    }

    private static void discoverNutrients() {
        Set<String> foundNutrients = new TreeSet<>();
        for (Item item : BuiltInRegistries.ITEM) {
            ItemStack stack = item.getDefaultInstance();
            for (TagKey<Item> tagKey : stack.tags().toList()) {
                Identifier loc = tagKey.location();
                if (loc.getNamespace().equals("c") && loc.getPath().startsWith("nutrients/")) {
                    foundNutrients.add(loc.getPath().substring(10));
                }
            }
        }
        for (String name : foundNutrients) {
            nutrients.add(new Nutrient(name));
        }

        if (nutrients.size() > 0 && getByName("proteins") == null)
            nutrients.add(new Nutrient("proteins"));
    }

    public static Nutrient getByName(String name) {
        for (Nutrient nutrient : get()) {
            if (nutrient.name.equals(name)) return nutrient;
        }
        return null;
    }

    public static List<Nutrient> getNutrients(Item item, @Nullable Level world) {
        return getNutrients(item.getDefaultInstance(), world, 1);
    }

    public static List<Nutrient> getNutrients(ItemStack itemStack, @Nullable Level world) {
        return getNutrients(itemStack, world, 1);
    }

    private static List<Nutrient> getNutrients(ItemStack item, @Nullable Level world, int iteration) {

        if (item.getItem() instanceof LunchBoxItem lunchBoxItem) {
            item = lunchBoxItem.getActiveFoodItemStack(item);
        }
        if (item == null) return new ArrayList<>();

        if (!nutrientMap.containsKey(item.getItem())) {

            if (world != null && world.isClientSide()) {
                ModNetworkHandler.sendToServer(new NutrientDataSyncTrigger(item.getItem()));
            } else if (world != null) {

                List<Nutrient> nutrientList = new ArrayList<>();

                // Check item tags for nutrient assignments
                for (TagKey<Item> tagKey : item.tags().sorted((o1, o2) -> o1.location().getPath().compareTo(o2.location().getPath())).toList()) {
                    Identifier tag = tagKey.location();
                    Nutrient nutrient = null;
                    if (tag.getNamespace().equals("c") && tag.getPath().startsWith("nutrients/")) {
                        nutrient = WorldNutrients.getByName(tag.getPath().substring(10));
                    }
                    else if (Config.LIST_Fruits.get().contains("#" + tag.getNamespace() + ":" + tag.getPath()) && !nutrientList.contains(WorldNutrients.getByName("fruits")))
                        nutrient = WorldNutrients.getByName("fruits");
                    else if (Config.LIST_PROTEINS.get().contains("#" + tag.getNamespace() + ":" + tag.getPath()) && !nutrientList.contains(WorldNutrients.getByName("proteins")))
                        nutrient = WorldNutrients.getByName("proteins");
                    else if (Config.LIST_CARBS.get().contains("#" + tag.getNamespace() + ":" + tag.getPath()) && !nutrientList.contains(WorldNutrients.getByName("carbs")))
                        nutrient = WorldNutrients.getByName("carbs");
                    else if (Config.LIST_VEGETABLES.get().contains("#" + tag.getNamespace() + ":" + tag.getPath()) && !nutrientList.contains(WorldNutrients.getByName("vegetables")))
                        nutrient = WorldNutrients.getByName("vegetables");
                    else if (Config.LIST_SUGARS.get().contains("#" + tag.getNamespace() + ":" + tag.getPath()) && !nutrientList.contains(WorldNutrients.getByName("sugars")))
                        nutrient = WorldNutrients.getByName("sugars");

                    if (nutrient != null && !nutrientList.contains(nutrient))
                        nutrientList.add(nutrient);
                }

                // Config-based item registry name checks
                String itemRegistryName = BuiltInRegistries.ITEM.getKey(item.getItem()).toString();
                checkConfigList(Config.LIST_CARBS.get(), itemRegistryName, "carbs", nutrientList);
                checkConfigList(Config.LIST_Fruits.get(), itemRegistryName, "fruits", nutrientList);
                checkConfigList(Config.LIST_PROTEINS.get(), itemRegistryName, "proteins", nutrientList);
                checkConfigList(Config.LIST_SUGARS.get(), itemRegistryName, "sugars", nutrientList);
                checkConfigList(Config.LIST_VEGETABLES.get(), itemRegistryName, "vegetables", nutrientList);

                // If no nutrients found from tags/config, check meat tags or traverse recipes
                if (nutrientList.size() == 0 && iteration < 5) {
                    boolean isMeat = false;
                    for (TagKey<Item> tagKey : item.tags().toList()) {
                        String tagPath = tagKey.location().getPath();
                        String tagNs = tagKey.location().getNamespace();
                        if ((tagNs.equals("c") && (tagPath.contains("meat") || tagPath.contains("raw_meat"))) ||
                                (tagNs.equals("minecraft") && tagPath.equals("meat"))) {
                            isMeat = true;
                            break;
                        }
                    }

                    if (isMeat) {
                        Nutrient proteinNutrient = getByName("proteins");
                        if (proteinNutrient != null && !nutrientList.contains(proteinNutrient))
                            nutrientList.add(proteinNutrient);
                    } else if (world instanceof ServerLevel serverLevel) {
                        // 26.1: Level.getRecipeManager() removed.
                        // Use ServerLevel.recipeAccess() to get RecipeManager.
                        // recipe.getResultItem() removed — use recipe.display() to check result.
                        // recipe.getIngredients() removed — use recipe.placementInfo().ingredients().
                        // ingredient.getItems() removed — use ingredient.items() returning Stream<Holder<Item>>.
                        var recipeManager = serverLevel.recipeAccess();
                        Collection<RecipeHolder<?>> recipes = recipeManager.getRecipes();

                        for (RecipeHolder<?> recipeHolder : recipes) {
                            Recipe<?> recipe = recipeHolder.value();

                            // Check if recipe result matches our target item via display system
                            if (recipeProducesItem(recipe, item.getItem())) {
                                // Get ingredients via placementInfo
                                PlacementInfo info = recipe.placementInfo();
                                if (info != null && info != PlacementInfo.NOT_PLACEABLE && !info.isImpossibleToPlace()) {
                                    for (Ingredient ingredient : info.ingredients()) {
                                        List<Holder<Item>> ingredientItems = ingredient.items().toList();
                                        if (!ingredientItems.isEmpty()) {
                                            Item ingredientItem = ingredientItems.getFirst().value();
                                            List<Nutrient> ingredientNutrients = getNutrients(ingredientItem.getDefaultInstance(), world, iteration + 1);
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
                }
                nutrientMap.put(item.getItem(), nutrientList);
            }
        }

        return (nutrientMap.containsKey(item.getItem())) ? nutrientMap.get(item.getItem()) : new ArrayList<>();
    }

    /**
     * Check if a recipe produces the given item by examining its display outputs.
     * In 26.1, Recipe.getResultItem() was removed. The display system is the
     * public API for discovering what a recipe produces.
     */
    private static boolean recipeProducesItem(Recipe<?> recipe, Item targetItem) {
        try {
            for (RecipeDisplay display : recipe.display()) {
                SlotDisplay resultSlot = display.result();
                if (slotDisplayContainsItem(resultSlot, targetItem)) {
                    return true;
                }
            }
        } catch (Exception e) {
            // Some special recipes may not have proper displays
        }
        return false;
    }

    /**
     * Check if a SlotDisplay references the given item.
     */
    private static boolean slotDisplayContainsItem(SlotDisplay slotDisplay, Item targetItem) {
        if (slotDisplay instanceof SlotDisplay.ItemSlotDisplay itemDisplay) {
            return itemDisplay.item().value() == targetItem;
        } else if (slotDisplay instanceof SlotDisplay.ItemStackSlotDisplay stackDisplay) {
            return stackDisplay.stack().item().value() == targetItem;
        } else if (slotDisplay instanceof SlotDisplay.Composite composite) {
            for (SlotDisplay inner : composite.contents()) {
                if (slotDisplayContainsItem(inner, targetItem)) return true;
            }
        }
        return false;
    }

    private static void checkConfigList(List<String> configList, String itemRegistryName, String nutrientName, List<Nutrient> nutrientList) {
        if (configList.contains(itemRegistryName) && !nutrientList.contains(WorldNutrients.getByName(nutrientName))) {
            Nutrient nutrient = WorldNutrients.getByName(nutrientName);
            if (nutrient != null) nutrientList.add(nutrient);
        }
    }

    public static float getEffectiveFoodQuality(FoodProperties foodItem, int numberOfNutrients) {
        return getEffectiveFoodQuality(foodItem.nutrition(), foodItem.saturation(), numberOfNutrients);
    }

    public static float getEffectiveFoodQuality(float healing, float saturation, int numberOfNutrients) {
        return Math.min(healing + saturation, Config.NUTRIENT_MAX_FOOD_VALUE.get().floatValue() * numberOfNutrients);
    }

    public static void setItemNutrients(Item item, List<Nutrient> nutrients) {
        nutrientMap.put(item, nutrients);
    }

    public static void reset() {
        nutrients.clear();
        nutrientMap.clear();
    }
}