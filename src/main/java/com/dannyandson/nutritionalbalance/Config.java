package com.dannyandson.nutritionalbalance;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.*;

@Mod.EventBusSubscriber
public class Config {

    public static ForgeConfigSpec SERVER_CONFIG;
//    public static ForgeConfigSpec CLIENT_CONFIG;

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_NUTRIENT_LEVELS = "nutrient_levels";
    public static final String CATEGORY_EFFECTS = "nutrition_affects";
    public static final String CATEGORY_FOODS = "nutrient_foods";

    //Server config values
    public static ForgeConfigSpec.DoubleValue NUTRIENT_INITIAL;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_MALNOURISHED;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_LOW_TARGET;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_TARGET_HIGH;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_ENGORGED;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_MAX;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_INCREMENT_RATE;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_DECAY_RATE;
    public static ForgeConfigSpec.DoubleValue NUTRIENT_DEATH_LOSS;

    public static ForgeConfigSpec.ConfigValue<List<String>> BAD_NUTRIENTS;
    public static ForgeConfigSpec.ConfigValue<List<String>> GOOD_NUTRIENTS;

    public static ForgeConfigSpec.ConfigValue<List<String>> LIST_VEGETABLES;
    public static ForgeConfigSpec.ConfigValue<List<String>> LIST_SUGARS;
    public static ForgeConfigSpec.ConfigValue<List<String>> LIST_CARBS;
    public static ForgeConfigSpec.ConfigValue<List<String>> LIST_PROTEINS;
    public static ForgeConfigSpec.ConfigValue<List<String>> LIST_Fruits;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
//        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        //Server config building
        SERVER_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);
        SERVER_BUILDER.comment("Nutrient Levels").push(CATEGORY_NUTRIENT_LEVELS);
        NUTRIENT_MALNOURISHED = SERVER_BUILDER.comment("Malnourishment value, below which bad effects can occur. (default:1.0)")
                .defineInRange("nutrient_level_malnourished",1.0,0.0,Double.MAX_VALUE);
        NUTRIENT_INITIAL = SERVER_BUILDER.comment("Initial nutrient level for new player. (default:50.0")
                .defineInRange("nutrient_initial",50.0,0.0,Double.MAX_VALUE);
        NUTRIENT_LOW_TARGET = SERVER_BUILDER.comment("Boundary between low and target, above which good affects can occur. (default:100.0")
                .defineInRange("nutrient_level_low_target",100.0,0.0,Double.MAX_VALUE);
        NUTRIENT_TARGET_HIGH = SERVER_BUILDER.comment("Boundary between target and high, below which good affects can occur. (default:120.0)")
                .defineInRange("nutrient_level_target_high",120.0,0.0,Double.MAX_VALUE);
        NUTRIENT_ENGORGED = SERVER_BUILDER.comment("Engorgement value, above which bad affects can occur.(default:170.0)")
                .defineInRange("nutrient_level_engorged",170.0,0.0,Double.MAX_VALUE);
        NUTRIENT_MAX = SERVER_BUILDER.comment("Maximum nutrient level. nutrients will cap out at this value no matter how much you eat. (default:180.0)")
                .defineInRange("nutrient_level_max",180.0,0.0,Double.MAX_VALUE);
        NUTRIENT_INCREMENT_RATE = SERVER_BUILDER.comment("Nutrient Increment Rate - rate of nutrient increase relative to food saturation and points. (default:1.0)")
                .defineInRange("nutrient_increment_rate",1.0,0.0,Double.MAX_VALUE);
        NUTRIENT_DECAY_RATE = SERVER_BUILDER.comment("Nutrient Decay Rate - base rate of nutrient reduction over time relative to player saturation and food level decay. (default:1.0)")
                .defineInRange("nutrient_decay_rate",1.0,0.0,Double.MAX_VALUE);
        NUTRIENT_DEATH_LOSS = SERVER_BUILDER.comment("Nutrient Loss On Death - number of nutrition point lost from each nutrient on death (bottoms out at initial value). (default:10.0)")
                .defineInRange("nutrient_death_loss",10.0,0.0,Double.MAX_VALUE);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Effects").push(CATEGORY_EFFECTS);

        List<String> badnutrients = new ArrayList<>();
        badnutrients.add("sugars");
        BAD_NUTRIENTS = SERVER_BUILDER.comment("List of nutrients which only give bad effects when engorged, are not required for a balanced diet and do not cause malnourishment when low (default:sugar).")
                .define("effect_bad_nutrients",badnutrients);

        List<String> goodnutrients = new ArrayList<>();
        goodnutrients.add("vegetables");
        GOOD_NUTRIENTS = SERVER_BUILDER.comment("List of nutrients which only give good effects, are not required for a balanced diet and do not cause engorgement. You can eat as much as you want (default:vegetables).")
                .define("effect_good_nutrients",goodnutrients);

        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Nutrient for modded foods: Add foods or tags here to add nutrients to the foods.\n" +
                "This is usually only needed for raw ingredients. Crafted and cooked foods will get nutrients from recipe ingredients.\n" +
                "This can be done with data packs by adding foods to item tags under forge:nutrient/<nutrientName>, but this config is here for convenience.\n" +
                "Data packs are required to define new nutrient categories.\n" +
                "These configs are additive on top of data packs.\n" +
                "Run the command /nutritionalbalance get_unassigned_foods to find any foods that do not have nutrients.").push(CATEGORY_FOODS);

        Map<String,List<String>> nutrientFoodsMap = new HashMap<>();

        String[] vegetables = {
                "#minecraft:flowers",
                "#forge:vegetables"
        };
        List<String> vegetableList = new ArrayList<>();
        vegetableList.addAll(Arrays.asList(vegetables));
        LIST_VEGETABLES = SERVER_BUILDER.comment("List of vegetables.")
                .define("vegetables_item_list",vegetableList);


        String[] carbs = {
                "#forge:carbs",
                "#forge:grain"
        };
        List<String> carbList = new ArrayList<>();
        carbList.addAll(Arrays.asList(carbs));
        LIST_CARBS = SERVER_BUILDER.comment("List of carbohydrates.")
                .define("carbs_item_list",carbList);

        String[] sugars = {

        };
        List<String> sugarsList = new ArrayList<>();
        sugarsList.addAll(Arrays.asList(sugars));
        LIST_SUGARS = SERVER_BUILDER.comment("List of simple sugars.")
                .define("sugars_item_list",sugarsList);

        String[] proteins = {
                "#forge:protein",
                "#forge:milk",
                "#forge:yogurt",
                "#forge:nuts"
        };
        List<String> proteinsList = new ArrayList<>();
        proteinsList.addAll(Arrays.asList(proteins));
        LIST_PROTEINS = SERVER_BUILDER.comment("List of proteins.")
                .define("protein_item_list",proteinsList);

        String[] fruits = {
                "#forge:fruits"
        };
        List<String> fruitsList = new ArrayList<>();
        fruitsList.addAll(Arrays.asList(fruits));
        LIST_Fruits = SERVER_BUILDER.comment("List of proteins.")
                .define("fruits_item_list",fruitsList);




        SERVER_BUILDER.pop().pop();

        SERVER_CONFIG = SERVER_BUILDER.build();
        //CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfig.Loading configEvent) {

    }

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
    }


}
