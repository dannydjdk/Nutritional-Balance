package com.dannyandson.nutritionalbalance;

import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.dannyandson.nutritionalbalance.capabilities.CapabilityNutritionalBalancePlayer;
import com.dannyandson.nutritionalbalance.capabilities.NutrientEventHandler;
import com.dannyandson.nutritionalbalance.commands.ModCommands;
import com.dannyandson.nutritionalbalance.events.*;
import com.dannyandson.nutritionalbalance.keybinding.ModInputHandler;
import com.dannyandson.nutritionalbalance.keybinding.ModKeyBindings;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("nutritionalbalance")
public class NutritionalBalance
{
    //TODO:
    // Configurable feedback to player when crossing threshold and/or advancements
    // HUD?
    // config for affects
    // config for colors?
    // custom effect handling?
    // slow down eating when engorged?
    // Upgrade forge

    public static final String MODID = "nutritionalbalance";
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public NutritionalBalance() {

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EventUseItem());
        MinecraftForge.EVENT_BUS.register(new EventPlayerTick());
        MinecraftForge.EVENT_BUS.register(new EventPlayerJoin());
        MinecraftForge.EVENT_BUS.register(new EventPlayerDeath());
        MinecraftForge.EVENT_BUS.register(new EventRightClickBlock());
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.register(new ModInputHandler());

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);


    }

    private void setup(final FMLCommonSetupEvent event)
    {
        CapabilityNutritionalBalancePlayer.register();
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class,NutrientEventHandler::onAttachCapabilitiesEvent);
        ModNetworkHandler.registerMessages();
    }

    private void registerCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // register tooltips and keybinding on client only
        MinecraftForge.EVENT_BUS.register(new EventTooltip());
        ModKeyBindings.register();
        MinecraftForge.EVENT_BUS.register(new EventNutrientButton());

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onReload(AddReloadListenerEvent event)
    {
        WorldNutrients.register();
    }

}
