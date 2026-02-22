package com.dannyandson.nutritionalbalance.setup;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.commands.CommandSetNutrient;
import com.dannyandson.nutritionalbalance.effects.ModMobAffects;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItem;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxMenu;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class Registration {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, NutritionalBalance.MODID);
    public static final Supplier<ModMobAffects.Engorged> ENGORGED_EFFECT = EFFECTS.register("engorged_effect", ModMobAffects.Engorged::new);
    public static final Supplier<ModMobAffects.MalNourished> MALNOURISHED_EFFECT = EFFECTS.register("malnourished_effect", ModMobAffects.MalNourished::new);
    public static final Supplier<ModMobAffects.Nourished> NOURISHED_EFFECT = EFFECTS.register("nourished_effect", ModMobAffects.Nourished::new);

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, NutritionalBalance.MODID);
    public static final Supplier<MenuType<LunchBoxMenu>> LUNCHBOX_MENU_TYPE = MENU_TYPES.register("lunchbbox", () -> new MenuType<>(LunchBoxMenu::createMenu, FeatureFlags.DEFAULT_FLAGS));

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, NutritionalBalance.MODID);
    public static final Supplier<Item> LUNCHBOX_ITEM = ITEMS.register("lunchbox", LunchBoxItem::new);

    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, NutritionalBalance.MODID);
    public static final Supplier<CommandSetNutrient.NutrientStringArgumentType.Serializer> NUTRIENT_STRING_ARGUMENT_TYPE =
            COMMAND_ARGUMENT_TYPES.register(
                    "nutrient_argument_type",
                    () -> ArgumentTypeInfos.registerByClass(CommandSetNutrient.NutrientStringArgumentType.class, new CommandSetNutrient.NutrientStringArgumentType.Serializer())
            );

    public static void register(IEventBus modEventBus){
        EFFECTS.register(modEventBus);
        MENU_TYPES.register(modEventBus);
        ITEMS.register(modEventBus);
        COMMAND_ARGUMENT_TYPES.register(modEventBus);
    }
}
