package com.dannyandson.nutritionalbalance.nutrients;

import com.dannyandson.nutritionalbalance.Config;
import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItem;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.network.NutrientDataSyncTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
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

    // Inverse recipe index: output Item -> recipes that produce it. Built lazily; cleared by reset().
    private static @Nullable Map<Item, List<RecipeHolder<?>>> recipesByOutput = null;

    // Identity check to detect a stale index across world reloads.
    private static @Nullable Object indexedRecipeAccess = null;

    // Cycle guard for the recursive resolver — prevents partial results from being cached on circular recipe graphs.
    private static final Set<Item> resolving = new HashSet<>();

    // Client-side dedup: don't re-ask the server about an item while a request is in flight.
    private static final Set<Item> pendingClientSyncs = new HashSet<>();

    // Set true after prewarm completes; cleared by reset() so /reload forces a fresh pre-warm.
    private static boolean prewarmComplete = false;

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
                // Only ask the server once per uncached item until we hear back.
                if (pendingClientSyncs.add(item.getItem())) {
                    ModNetworkHandler.sendToServer(new NutrientDataSyncTrigger(item.getItem()));
                }
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
                boolean depthLimited = false;
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
                        // Use the inverse index so we don't scan every recipe per uncached lookup.
                        if (resolving.add(item.getItem())) {
                            try {
                                for (RecipeHolder<?> recipeHolder : getRecipesForOutput(serverLevel, item.getItem())) {
                                    Recipe<?> recipe = recipeHolder.value();
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
                            } finally {
                                resolving.remove(item.getItem());
                            }
                        } else {
                            // Cycle: don't cache an empty result. The outer call will cache the real one.
                            depthLimited = true;
                        }
                    }
                }
                if (!depthLimited) {
                    nutrientMap.put(item.getItem(), nutrientList);
                } else {
                    return nutrientList;
                }
            }
        }

        return (nutrientMap.containsKey(item.getItem())) ? nutrientMap.get(item.getItem()) : new ArrayList<>();
    }

    /**
     * Returns recipes whose output is the given item, building the inverse index on first call.
     * Replaces a per-call full RecipeManager scan that dominated first-login cost on large modpacks.
     */
    private static List<RecipeHolder<?>> getRecipesForOutput(ServerLevel level, Item item) {
        var recipeAccess = level.recipeAccess();
        if (recipesByOutput == null || indexedRecipeAccess != recipeAccess) {
            buildRecipeIndex(level);
        }
        List<RecipeHolder<?>> bucket = recipesByOutput.get(item);
        return bucket != null ? bucket : Collections.emptyList();
    }

    private static void buildRecipeIndex(ServerLevel level) {
        long start = System.currentTimeMillis();
        var recipeAccess = level.recipeAccess();
        Map<Item, List<RecipeHolder<?>>> idx = new HashMap<>();
        Set<Item> seenForThisRecipe = new HashSet<>();
        for (RecipeHolder<?> recipeHolder : recipeAccess.getRecipes()) {
            try {
                seenForThisRecipe.clear();
                for (RecipeDisplay display : recipeHolder.value().display()) {
                    collectOutputItems(display.result(), seenForThisRecipe);
                }
                for (Item out : seenForThisRecipe) {
                    idx.computeIfAbsent(out, k -> new ArrayList<>()).add(recipeHolder);
                }
            } catch (Throwable t) {
                // Some modded recipes throw on display() outside a crafting context. Skip rather than break the index.
            }
        }
        recipesByOutput = idx;
        indexedRecipeAccess = recipeAccess;
        NutritionalBalance.LOGGER.info("Nutritional Balance: built recipe-output index ({} output items, {} ms)",
                idx.size(), System.currentTimeMillis() - start);
    }

    /** Walk a SlotDisplay tree and collect every Item it can resolve to. */
    private static void collectOutputItems(SlotDisplay slotDisplay, Set<Item> sink) {
        if (slotDisplay instanceof SlotDisplay.ItemSlotDisplay itemDisplay) {
            sink.add(itemDisplay.item().value());
        } else if (slotDisplay instanceof SlotDisplay.ItemStackSlotDisplay stackDisplay) {
            sink.add(stackDisplay.stack().item().value());
        } else if (slotDisplay instanceof SlotDisplay.Composite composite) {
            for (SlotDisplay inner : composite.contents()) {
                collectOutputItems(inner, sink);
            }
        }
    }

    /**
     * Resolves nutrients for every food item, populating nutrientMap. Idempotent.
     * Designed to be called from ServerStartedEvent so the cost lands during world load, not first login.
     */
    public static void prewarm(ServerLevel level) {
        if (prewarmComplete || level == null) return;
        long start = System.currentTimeMillis();
        int considered = 0, succeeded = 0;
        for (Item item : BuiltInRegistries.ITEM) {
            try {
                if (item.getDefaultInstance().has(DataComponents.FOOD)) {
                    considered++;
                    getNutrients(item, level);
                    succeeded++;
                }
            } catch (Throwable t) {
                // Skip items that throw on getDefaultInstance(); they fall back to lazy lookup if hit.
            }
        }
        prewarmComplete = true;
        NutritionalBalance.LOGGER.info("Nutritional Balance: pre-warmed nutrient cache for {}/{} food items in {} ms",
                succeeded, considered, System.currentTimeMillis() - start);
    }

    /**
     * Snapshots nutrientMap as a wire-friendly map of identifier strings for bulk client sync.
     * Items with empty nutrient lists are included so the client knows not to re-query them.
     */
    public static Map<String, List<String>> snapshotForSync() {
        Map<String, List<String>> snapshot = new HashMap<>(nutrientMap.size());
        for (Map.Entry<Item, List<Nutrient>> entry : nutrientMap.entrySet()) {
            Identifier itemId = BuiltInRegistries.ITEM.getKey(entry.getKey());
            if (itemId == null) continue;
            List<String> names = new ArrayList<>(entry.getValue().size());
            for (Nutrient n : entry.getValue()) names.add(n.name);
            snapshot.put(itemId.toString(), names);
        }
        return snapshot;
    }

    /** Applies a bulk-sync payload from the server. Items not in the local registry are skipped. */
    public static void applyBulkSync(Map<String, List<String>> data) {
        int applied = 0;
        for (Map.Entry<String, List<String>> entry : data.entrySet()) {
            Identifier key;
            try {
                key = Identifier.parse(entry.getKey());
            } catch (Exception e) {
                continue;
            }
            Item item = BuiltInRegistries.ITEM.getValue(key);
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
        // Clear pending so a later cache-eviction-then-relookup isn't blocked by a stale flag.
        pendingClientSyncs.remove(item);
    }

    public static void reset() {
        nutrients.clear();
        nutrientMap.clear();
        recipesByOutput = null;
        indexedRecipeAccess = null;
        resolving.clear();
        pendingClientSyncs.clear();
        prewarmComplete = false;
    }
}