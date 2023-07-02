package com.dannyandson.nutritionalbalance.setup;

import com.dannyandson.nutritionalbalance.NutritionalBalance;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItemRenderer;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = NutritionalBalance.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    public static void init(final FMLClientSetupEvent event) {
        MenuScreens.register(Registration.LUNCHBOX_MENU_TYPE.get(), LunchBoxScreen::new);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getAtlas().location().equals(InventoryMenu.BLOCK_ATLAS)) {
            return;
        }
        event.addSprite(LunchBoxItemRenderer.LUNCHBOX);
    }
}
