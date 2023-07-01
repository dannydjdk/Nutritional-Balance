package com.dannyandson.nutritionalbalance.setup;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NutritionalBalance.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        MenuScreens.register(Registration.LUNCHBOX_MENU_TYPE.get(), LunchBoxScreen::new);
    }

    public static void addCreative(BuildCreativeModeTabContentsEvent event){
        if(event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS)
        {
            event.accept(Registration.LUNCHBOX_ITEM.get());
        }
    }
}
