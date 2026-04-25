package com.dannyandson.nutritionalbalance.nutrients;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;

public class WorldNutrients
{
    private static final List<Nutrient> nutrients = new ArrayList<>();
    private static final Map<Item,List<Nutrient>> nutrientMap = new HashMap<>();

    // Inverse recipe index: output Item -> recipes that produce it.
    // Built lazily on first server-side need, cleared in reset() so it rebuilds after datapack/tags reload.
    private static Map<Item, List<RecipeHolder<?>>> recipesByOutput = null;
    // Identity of the RecipeManager that built the current index; used to detect a stale index across world reloads.
    private static RecipeManager indexedRecipeManager = null;

    // Cycle/in-flight guard for the server-side recursive resolver. Items currently being resolved by an outer
    // call are skipped by inner calls so circular recipe graphs don't redo work or cache partial results.
    private static final Set<Item> resolving = new HashSet<>();

    // Client-side de-duplication for outgoing NutrientDataSyncTrigger packets. An item we've already asked the
    // server about doesn't get re-asked until the server's response lands in nutrientMap (and clears it via
    // setItemNutrients).
    private static final Set<Item> pendingClientSyncs = new HashSet<>();

    // Has the server-side cache been pre-warmed for the current set of recipes/tags? Cleared in reset() so
    // a datapack reload (which fires TagsUpdatedEvent -> register() -> reset()) forces a fresh pre-warm.
    private static boolean prewarmComplete = false;

    public static void register()
    {
        reset();
        // loop through nutrient/* tags - now using c: namespace instead of forge:
        for (TagKey<Item> tagKey: BuiltInRegistries.ITEM.getTagNames().sorted((o1,o2)->o2.location().getPath().compareTo(o1.location().getPath())).toList()) {
            ResourceLocation resourceLocation = tagKey.location();
            String namespace = resourceLocation.getNamespace();
            String path = resourceLocation.getPath();

            if (namespace.equals("c") && path.startsWith("nutrients/"))
            {
                nutrients.add(new Nutrient(path.substring(10)));
            }
        }

        // add meat nutrient if not already added
        if (nutrients.size()>0 && getByName("proteins")==null)
            nutrients.add(new Nutrient("proteins"));
    }

    public static List<Nutrient> get() {
        if (nutrients.size()==0)
        {
            register();
        }
        return nutrients;
    }

    public static Nutrient getByName(String name) {
        for (Nutrient nutrient : get()) {
            if (nutrient.name.equals(name))
                return nutrient;
        }
        return null;
    }

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
        if (item == null) return new ArrayList<>();

        if (!nutrientMap.containsKey(item.getItem())) {

            if (world!=null && world.isClientSide()) {
                // Only ask the server once per uncached item until we hear back.
                if (pendingClientSyncs.add(item.getItem())) {
                    ModNetworkHandler.sendToServer(new NutrientDataSyncTrigger(item.getItem()));
                }
            }else if(world!=null) {

                List<Nutrient> nutrientList = new ArrayList<>();

                // Get all the ItemTags for the item - now using c: namespace
                for (TagKey<Item> tagKey : item.getTags().sorted((o1, o2) -> o1.location().getPath().compareTo(o2.location().getPath())).toList()) {
                    ResourceLocation tag = tagKey.location();
                    Nutrient nutrient = null;
                    if (tag.getNamespace().equals("c") && tag.getPath().startsWith("nutrients/")) {
                        nutrient = WorldNutrients.getByName(tag.getPath().substring(10));
                    }
                    // Check against tags in config file
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

                // Check nutrient lists from configs
                String itemRegistryName = BuiltInRegistries.ITEM.getKey(item.getItem()).getNamespace() + ":" + BuiltInRegistries.ITEM.getKey(item.getItem()).getPath();
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

                // If no nutrient tags, check if meat (via tags in 1.21) or traverse recipes
                boolean depthLimited = false;
                if (nutrientList.size() == 0 && iteration < 5) {
                    // In 1.21, isMeat() is removed. Check for c:foods/meat tag or similar
                    boolean isMeat = false;
                    for (TagKey<Item> tagKey : item.getTags().toList()) {
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
                    } else if (resolving.add(item.getItem())) {
                        // Use the inverse recipe index instead of streaming + sorting all recipes per call.
                        // Pre-built index is keyed by output item and pre-sorted to match prior iteration order.
                        try {
                            for (RecipeHolder<?> recipeHolder : getRecipesForOutput(world, item.getItem())) {
                                Recipe<?> recipe = recipeHolder.value();
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
                        } finally {
                            resolving.remove(item.getItem());
                        }
                    } else {
                        // We're already resolving this item further up the recursion stack (cycle).
                        // Don't cache a partial empty result; let the outer call complete and cache the real one.
                        depthLimited = true;
                    }
                }
                if (!depthLimited) {
                    nutrientMap.put(item.getItem(), nutrientList);
                } else {
                    return nutrientList;
                }
            }
        }

        return (nutrientMap.containsKey(item.getItem()))?nutrientMap.get(item.getItem()): new ArrayList<>();
    }

    /**
     * Returns the (small, pre-sorted) list of recipes whose output is the given item, building the inverse
     * index lazily on first call after a reload. This replaces a per-call scan + sort over the entire
     * RecipeManager that was the dominant cost for first-login lag on large modpacks.
     */
    private static List<RecipeHolder<?>> getRecipesForOutput(Level world, Item item) {
        RecipeManager rm = world.getRecipeManager();
        if (recipesByOutput == null || indexedRecipeManager != rm) {
            buildRecipeIndex(world, rm);
        }
        List<RecipeHolder<?>> bucket = recipesByOutput.get(item);
        return bucket != null ? bucket : Collections.emptyList();
    }

    private static void buildRecipeIndex(Level world, RecipeManager rm) {
        long start = System.currentTimeMillis();
        Map<Item, List<RecipeHolder<?>>> idx = new HashMap<>();
        for (RecipeHolder<?> recipeHolder : rm.getRecipes()) {
            try {
                ItemStack out = recipeHolder.value().getResultItem(world.registryAccess());
                if (out != null && !out.isEmpty()) {
                    idx.computeIfAbsent(out.getItem(), k -> new ArrayList<>()).add(recipeHolder);
                }
            } catch (Throwable t) {
                // Some modded recipes throw when getResultItem is called outside a real crafting context.
                // Skip them rather than break the whole index.
            }
        }
        // Preserve the previous deterministic ordering (descending by recipe id path).
        for (List<RecipeHolder<?>> bucket : idx.values()) {
            bucket.sort((a, b) -> b.id().getPath().compareTo(a.id().getPath()));
        }
        recipesByOutput = idx;
        indexedRecipeManager = rm;
        NutritionalBalance.LOGGER.info("Nutritional Balance: built recipe-output index ({} output items, {} ms)",
                idx.size(), System.currentTimeMillis() - start);
    }

    /**
     * Walks the item registry once and resolves nutrients for every food item, populating nutrientMap so
     * subsequent client lookups hit the cache directly. Idempotent; safe to call repeatedly. Designed to
     * be called from ServerStartedEvent (initial server start) and OnDatapackSyncEvent (after /reload),
     * so the work happens during world load instead of during a player's first login.
     */
    public static void prewarm(Level world) {
        if (prewarmComplete || world == null) return;
        long start = System.currentTimeMillis();
        int considered = 0, succeeded = 0;
        for (Item item : BuiltInRegistries.ITEM) {
            try {
                FoodProperties food = item.getFoodProperties(item.getDefaultInstance(), null);
                if (food != null) {
                    considered++;
                    getNutrients(item, world);
                    succeeded++;
                }
            } catch (Throwable t) {
                // Some modded items NPE / throw in getFoodProperties when called with a null entity. Skip
                // them; they'll fall back to lazy lookup if a player ever encounters them.
            }
        }
        prewarmComplete = true;
        NutritionalBalance.LOGGER.info("Nutritional Balance: pre-warmed nutrient cache for {}/{} food items in {} ms",
                succeeded, considered, System.currentTimeMillis() - start);
    }

    /**
     * Snapshots the current nutrientMap as a wire-friendly Map<ResourceLocation, List<String>>, suitable
     * for shipping to a client in a single bulk packet. Items with empty nutrient lists are included so
     * the client knows not to re-query them.
     */
    public static Map<ResourceLocation, List<String>> snapshotForSync() {
        Map<ResourceLocation, List<String>> snapshot = new HashMap<>(nutrientMap.size());
        for (Map.Entry<Item, List<Nutrient>> entry : nutrientMap.entrySet()) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(entry.getKey());
            if (itemId == null) continue;
            List<String> names = new ArrayList<>(entry.getValue().size());
            for (Nutrient n : entry.getValue()) names.add(n.name);
            snapshot.put(itemId, names);
        }
        return snapshot;
    }

    /**
     * Applies a bulk-sync payload received from the server, populating the local nutrientMap. Items not
     * present in the local registry are silently skipped (shouldn't happen with mod-parity, but is safe
     * to ignore if it does).
     */
    public static void applyBulkSync(Map<ResourceLocation, List<String>> data) {
        int applied = 0;
        for (Map.Entry<ResourceLocation, List<String>> entry : data.entrySet()) {
            ResourceLocation key = entry.getKey();
            if (!BuiltInRegistries.ITEM.containsKey(key)) continue;
            Item item = BuiltInRegistries.ITEM.get(key);
            if (item == null) continue;
            List<Nutrient> nuts = new ArrayList<>(entry.getValue().size());
            for (String name : entry.getValue()) {
                if (name.isEmpty()) continue;
                Nutrient n = getByName(name);
                if (n != null) nuts.add(n);
            }
            nutrientMap.put(item, nuts);
            pendingClientSyncs.remove(item);
            applied++;
        }
        NutritionalBalance.LOGGER.info("Nutritional Balance: applied bulk nutrient sync for {} items", applied);
    }

    public static float getEffectiveFoodQuality(FoodProperties foodItem, int numberOfNutrients)
    {
        return getEffectiveFoodQuality(foodItem.nutrition(), foodItem.saturation(), numberOfNutrients);
    }

    public static float getEffectiveFoodQuality(float healing, float saturation, int numberOfNutrients)
    {
        return Math.min(healing+saturation, Config.NUTRIENT_MAX_FOOD_VALUE.get().floatValue()*numberOfNutrients);
    }

    public static void setItemNutrients(Item item, List<Nutrient> nutrients){
        nutrientMap.put(item,nutrients);
        // Server's response landed for this item; future calls will hit the cache directly, but clear the
        // pending flag so a later cache-eviction-then-relookup doesn't get stuck.
        pendingClientSyncs.remove(item);
    }

    public static void reset() {
        nutrients.clear();
        nutrientMap.clear();
        recipesByOutput = null;
        indexedRecipeManager = null;
        resolving.clear();
        pendingClientSyncs.clear();
        prewarmComplete = false;
    }
}