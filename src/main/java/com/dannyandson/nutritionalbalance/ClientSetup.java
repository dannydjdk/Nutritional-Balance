package com.dannyandson.nutritionalbalance;

import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NutritionalBalance.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        MenuScreens.register(NutritionalBalance.LUNCHBOX_MENU_TYPE.get(), LunchBoxScreen::new);
    }
}
