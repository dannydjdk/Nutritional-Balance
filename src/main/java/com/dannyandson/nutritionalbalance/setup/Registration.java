package com.dannyandson.nutritionalbalance.setup;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.effects.ModMobAffects;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItem;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxMenu;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Registration {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, NutritionalBalance.MODID);
    public static final RegistryObject<ModMobAffects.Engorged> ENGORGED_EFFECT = EFFECTS.register("engorged_effect", ModMobAffects.Engorged::new);
    public static final RegistryObject<ModMobAffects.MalNourished> MALNOURISHED_EFFECT = EFFECTS.register("malnourished_effect", ModMobAffects.MalNourished::new);
    public static final RegistryObject<ModMobAffects.Nourished> NOURISHED_EFFECT = EFFECTS.register("nourished_effect", ModMobAffects.Nourished::new);
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, NutritionalBalance.MODID);
    public static final RegistryObject<MenuType<LunchBoxMenu>> LUNCHBOX_MENU_TYPE = MENU_TYPES.register("lunchbbox", () -> new MenuType<>(LunchBoxMenu::createMenu));
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NutritionalBalance.MODID);
    public static final RegistryObject<Item> LUNCHBOX_ITEM = ITEMS.register("lunchbox", LunchBoxItem::new);

    public static void register(){
        EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        MENU_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());

    }
}
