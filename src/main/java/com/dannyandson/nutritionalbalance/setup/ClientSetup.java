package com.dannyandson.nutritionalbalance.setup;

import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxItemRenderer;
import com.dannyandson.nutritionalbalance.lunchbox.LunchBoxScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class ClientSetup {
    public static void init(final RegisterMenuScreensEvent event) {
        event.register(Registration.LUNCHBOX_MENU_TYPE.get(), LunchBoxScreen::new);
    }

    public static void addCreative(BuildCreativeModeTabContentsEvent event){
        if(event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS)
        {
            event.accept(Registration.LUNCHBOX_ITEM.get());
        }
    }

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return new LunchBoxItemRenderer(
                        Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                        Minecraft.getInstance().getEntityModels()
                );
            }
        }, Registration.LUNCHBOX_ITEM.get());
    }

    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(LunchBoxItemRenderer.LUNCHBOX_3D_MODEL);
    }
}