package com.dannyandson.nutritionalbalance;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.*;

@Mod.EventBusSubscriber
public class Config {

    public static ForgeConfigSpec SERVER_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_NUTRIENT_LEVELS = "nutrient_levels";
    public static final String CATEGORY_EFFECTS = "nutrition_affects";
    public static final String CATEGORY_EFFECTS_NOURISHED = "nourished_affects";
    public static final String CATEGORY_EFFECTS_MALNOURISHED = "malnourished_affects";
    public static final String CATEGORY_EFFECTS_ENGORGED = "engorged_affects";
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

    public static ForgeConfigSpec.DoubleValue NUTRIENT_MAX_FOOD_VALUE;

    public static ForgeConfigSpec.ConfigValue<List<String>> BAD_NUTRIENTS;
    public static ForgeConfigSpec.ConfigValue<List<String>> GOOD_NUTRIENTS;

    public static ForgeConfigSpec.ConfigValue<List<String>> LIST_VEGETABLES;
    public static ForgeConfigSpec.ConfigValue<List<String>> LIST_SUGARS;
    public static ForgeConfigSpec.ConfigValue<List<String>> LIST_CARBS;
    public static ForgeConfigSpec.ConfigValue<List<String>> LIST_PROTEINS;
    public static ForgeConfigSpec.ConfigValue<List<String>> LIST_Fruits;

    public static ForgeConfigSpec.IntValue NUTRIENT_BUTTON_X;
    public static ForgeConfigSpec.IntValue NUTRIENT_BUTTON_Y;
    public static ForgeConfigSpec.BooleanValue NUTRIENT_BUTTON_ENABLED;
    public static ForgeConfigSpec.BooleanValue SHOW_THRESHOLD_TOAST;

    //common config
    public static ForgeConfigSpec.DoubleValue NOURISHED_MAX_HEALTH;
    public static ForgeConfigSpec.DoubleValue NOURISHED_KNOCKBACK_RESISTANCE;
    public static ForgeConfigSpec.DoubleValue NOURISHED_MOVEMENT_SPEED;
    public static ForgeConfigSpec.DoubleValue NOURISHED_ATTACK_DAMAGE;
    public static ForgeConfigSpec.DoubleValue NOURISHED_ATTACK_KNOCKBACK;
    public static ForgeConfigSpec.DoubleValue NOURISHED_ATTACK_SPEED;

    public static ForgeConfigSpec.DoubleValue MALNOURISHED_MAX_HEALTH;
    public static ForgeConfigSpec.DoubleValue MALNOURISHED_KNOCKBACK_RESISTANCE;
    public static ForgeConfigSpec.DoubleValue MALNOURISHED_MOVEMENT_SPEED;
    public static ForgeConfigSpec.DoubleValue MALNOURISHED_ATTACK_DAMAGE;
    public static ForgeConfigSpec.DoubleValue MALNOURISHED_ATTACK_KNOCKBACK;
    public static ForgeConfigSpec.DoubleValue MALNOURISHED_ATTACK_SPEED;

    public static ForgeConfigSpec.DoubleValue ENGORGED_MAX_HEALTH;
    public static ForgeConfigSpec.DoubleValue ENGORGED_KNOCKBACK_RESISTANCE;
    public static ForgeConfigSpec.DoubleValue ENGORGED_MOVEMENT_SPEED;
    public static ForgeConfigSpec.DoubleValue ENGORGED_ATTACK_DAMAGE;
    public static ForgeConfigSpec.DoubleValue ENGORGED_ATTACK_KNOCKBACK;
    public static ForgeConfigSpec.DoubleValue ENGORGED_ATTACK_SPEED;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();

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
        NUTRIENT_MAX_FOOD_VALUE = SERVER_BUILDER.comment("Max food value a single nutrient for a food item can contain. Helps prevent OP food mods from making it impossible to keep nutrients balanced. (default:20.0)")
                .defineInRange("nutrient_max_food_value",20.0,1.0, Double.MAX_VALUE);
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

        SERVER_BUILDER.comment("Nourished Effects - effects to apply when the player reaches all nutrient targets.").push(CATEGORY_EFFECTS_NOURISHED);
        NOURISHED_MAX_HEALTH = SERVER_BUILDER.comment("Max Health - number of health points (1/2 hearts) to add or subtract. (default: 10.0)")
                .defineInRange("nourished_max_health",10d,-40d,40d);
        NOURISHED_KNOCKBACK_RESISTANCE = SERVER_BUILDER.comment("Knockback Resistance - knockback resistance to add. 1.0 is 100%. (default: 0.0)")
                .defineInRange("nourished_knockback_resistance",0d,0d,1d);
        NOURISHED_MOVEMENT_SPEED = SERVER_BUILDER.comment("Movement Speed - increase or reduce player movement speed. Speed I is 0.2. Slowness I is -0.15. (default: 0.2)")
                .defineInRange("nourished_movement_speed", 0.2d,-0.7d,2.0d);
        NOURISHED_ATTACK_DAMAGE = SERVER_BUILDER.comment("Attack Damage - increase or reduce attack damage caused by player. Strength I is 0.2. Weakness I is -0.15. (default: 0.2)")
                .defineInRange("nourished_attack_damage",0.2d,-2.0d, 4.0d);
        NOURISHED_ATTACK_KNOCKBACK  = SERVER_BUILDER.comment("Attack Knockback - increase the amount of knockback caused by the player. (default: 0.0)")
                .defineInRange("nourished_knockback", 0d,0d,4d);
        NOURISHED_ATTACK_SPEED = SERVER_BUILDER.comment("Mining Speed - increase or decrease mining speed of the player. Haste I is 0.1. Mining Fatique I is -0.1. (default: 0.1)")
                .defineInRange("nourished_mining_speed",0.1d,-4.0d,4.0d);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Malnourished Effects").push(CATEGORY_EFFECTS_MALNOURISHED);
        MALNOURISHED_MAX_HEALTH = SERVER_BUILDER.comment("Max Health - number of health points (1/2 hearts) to add or subtract. (default: -4.0)")
                .defineInRange("malnourished_max_health",-4d,-40d,40d);
        MALNOURISHED_KNOCKBACK_RESISTANCE = SERVER_BUILDER.comment("Knockback Resistance - knockback resistance to add. 1.0 is 100%. (default: 0.0)")
                .defineInRange("malnourished_knockback_resistance",0d,0d,1d);
        MALNOURISHED_MOVEMENT_SPEED = SERVER_BUILDER.comment("Movement Speed - increase or reduce player movement speed. Speed I is 0.2. Slowness I is -0.15. (default: -0.15)")
                .defineInRange("malnourished_movement_speed", -0.15d,-0.7d,2.0d);
        MALNOURISHED_ATTACK_DAMAGE = SERVER_BUILDER.comment("Attack Damage - increase or reduce attack damage caused by player. Strength I is 0.2. Weakness I is -0.15. (default: -0.15)")
                .defineInRange("malnourished_attack_damage",-0.15d,-2.0d, 4.0d);
        MALNOURISHED_ATTACK_KNOCKBACK  = SERVER_BUILDER.comment("Attack Knockback - increase the amount of knockback caused by the player. (default: 0.0)")
                .defineInRange("malnourished_knockback", 0d,0d,4d);
        MALNOURISHED_ATTACK_SPEED = SERVER_BUILDER.comment("Mining Speed - increase or decrease mining speed of the player. Haste I is 0.1. Mining Fatique I is -0.1. (default: 0.0)")
                .defineInRange("malnourished_mining_speed",0.0d,-4.0d,4.0d);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Engorged Effects").push(CATEGORY_EFFECTS_ENGORGED);
        ENGORGED_MAX_HEALTH = SERVER_BUILDER.comment("Max Health - number of health points (1/2 hearts) to add or subtract. (default: 0.0)")
                .defineInRange("engorged_max_health",0d,-40d,40d);
        ENGORGED_KNOCKBACK_RESISTANCE = SERVER_BUILDER.comment("Knockback Resistance - knockback resistance to add. 1.0 is 100%. (default: 0.0)")
                .defineInRange("engorged_knockback_resistance",0d,0d,1d);
        ENGORGED_MOVEMENT_SPEED = SERVER_BUILDER.comment("Movement Speed - increase or reduce player movement speed. Speed I is 0.2. Slowness I is -0.15. (default: -0.15)")
                .defineInRange("engorged_movement_speed", -0.15d,-0.7d,2.0d);
        ENGORGED_ATTACK_DAMAGE = SERVER_BUILDER.comment("Attack Damage - increase or reduce attack damage caused by player. Strength I is 0.2. Weakness I is -0.15. (default: 0.0)")
                .defineInRange("engorged_attack_damage",0.0d,-2.0d, 4.0d);
        ENGORGED_ATTACK_KNOCKBACK  = SERVER_BUILDER.comment("Attack Knockback - increase the amount of knockback caused by the player. (default: 0.0)")
                .defineInRange("engorged_knockback", 0d,0d,4d);
        ENGORGED_ATTACK_SPEED = SERVER_BUILDER.comment("Mining Speed - increase or decrease mining speed of the player. Haste I is 0.1. Mining Fatique I is -0.1. (default: -0.1)")
                .defineInRange("engorged_mining_speed",-0.1d,-4.0d,4.0d);
        SERVER_BUILDER.pop();

        SERVER_BUILDER.pop();

        SERVER_BUILDER.comment("Nutrient for modded foods: Add foods or tags here to add nutrients to the foods.\n" +
                "This is usually only needed for raw ingredients. Crafted and cooked foods will get nutrients from recipe ingredients.\n" +
                "This can be done with data packs by adding foods to item tags under forge:nutrients/<nutrientName>, but this config is here for convenience.\n" +
                "Data packs are required to define new nutrient categories.\n" +
                "These configs are additive on top of data packs.\n" +
                "Run the command /nutritionalbalance get_unassigned_foods to find any foods that do not have nutrients.").push(CATEGORY_FOODS);

        Map<String,List<String>> nutrientFoodsMap = new HashMap<>();

        String[] vegetables = {
                "#minecraft:flowers",
                "#forge:vegetables",
                "#nourish:vegetables"
        };
        List<String> vegetableList = new ArrayList<>();
        vegetableList.addAll(Arrays.asList(vegetables));
        LIST_VEGETABLES = SERVER_BUILDER.comment("List of vegetables.")
                .define("vegetables_item_list",vegetableList);


        String[] carbs = {
                "#forge:carbs",
                "#forge:grain",
                "#nourish:carbohydrates"
        };
        List<String> carbList = new ArrayList<>();
        carbList.addAll(Arrays.asList(carbs));
        LIST_CARBS = SERVER_BUILDER.comment("List of carbohydrates.")
                .define("carbs_item_list",carbList);

        String[] sugars = {
                "#nourish:sweets"
        };
        List<String> sugarsList = new ArrayList<>();
        sugarsList.addAll(Arrays.asList(sugars));
        LIST_SUGARS = SERVER_BUILDER.comment("List of simple sugars.")
                .define("sugars_item_list",sugarsList);

        String[] proteins = {
                "#forge:protein",
                "#forge:milk",
                "#forge:yogurt",
                "#forge:nuts",
                "#nourish:protein"
        };
        List<String> proteinsList = new ArrayList<>();
        proteinsList.addAll(Arrays.asList(proteins));
        LIST_PROTEINS = SERVER_BUILDER.comment("List of proteins.")
                .define("protein_item_list",proteinsList);

        String[] fruits = {
                "#forge:fruits",
                "#forge:fruits/berry",
                "#nourish:fruit"
        };
        List<String> fruitsList = new ArrayList<>();
        fruitsList.addAll(Arrays.asList(fruits));
        LIST_Fruits = SERVER_BUILDER.comment("List of fruits.")
                .define("fruits_item_list",fruitsList);




        SERVER_BUILDER.pop().pop();

        SERVER_CONFIG = SERVER_BUILDER.build();

        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();
        CLIENT_BUILDER.comment("General Settings").push(CATEGORY_GENERAL);

        NUTRIENT_BUTTON_ENABLED = CLIENT_BUILDER.comment("Show the nutrient button in the player inventory screen. (default:true)")
                .define("nutrient_button_enabled",true);

        CLIENT_BUILDER.comment("Nutrient Button Location").push("button");
        NUTRIENT_BUTTON_X = CLIENT_BUILDER.comment("X Offset of nutrition button relative to the upper left corner of the player inventory screen. (default:131)")
                .defineInRange("nutrient_button_x",131,0,512);
        NUTRIENT_BUTTON_Y = CLIENT_BUILDER.comment("Y Offset of nutrition button relative to the upper left corner of the player inventory screen. (default:61)")
                .defineInRange("nutrient_button_y",61,0,512);


        CLIENT_BUILDER.pop();

        SHOW_THRESHOLD_TOAST = CLIENT_BUILDER.comment("Show a Toast notification when player nutrition status changes. (default:true)")
                .define("show_threshold_toast",true);

        CLIENT_BUILDER.pop();
        CLIENT_CONFIG = CLIENT_BUILDER.build();

    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent) {

    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent) {
    }


}
