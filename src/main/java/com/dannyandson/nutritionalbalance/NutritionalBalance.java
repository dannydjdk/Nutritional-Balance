package com.dannyandson.nutritionalbalance;

import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.dannyandson.nutritionalbalance.commands.ModCommands;
import com.dannyandson.nutritionalbalance.events.*;
import com.dannyandson.nutritionalbalance.keybinding.ModInputHandler;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.setup.ClientSetup;
import com.dannyandson.nutritionalbalance.setup.Registration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("nutritionalbalance")
public class NutritionalBalance
{
    public static final String MODID = "nutritionalbalance";
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public static boolean modEffectsLoaded = false;

    public NutritionalBalance() {

        Registration.register();

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        if(FMLEnvironment.dist.isClient()) {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
        }

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EventUseItem());
        MinecraftForge.EVENT_BUS.register(new EventPlayerTick());
        MinecraftForge.EVENT_BUS.register(new EventPlayerJoin());
        MinecraftForge.EVENT_BUS.register(new EventPlayerClone());
        MinecraftForge.EVENT_BUS.register(new EventRightClickBlock());
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.addListener(this::serverStarted);
        MinecraftForge.EVENT_BUS.register(new ModInputHandler());

        Registration.COMMAND_ARGUMENT_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);


    }

    private void setup(final FMLCommonSetupEvent event)
    {
        ModNetworkHandler.registerMessages();
    }


    private void registerCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // register tooltips and keybinding on client only
        MinecraftForge.EVENT_BUS.register(new EventTooltip());
        MinecraftForge.EVENT_BUS.register(new EventNutrientButton());

    }

    @SubscribeEvent
    public void serverStarted(ServerStartedEvent event){
        PlayerNutritionData.init(event.getServer().overworld());
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onReload(AddReloadListenerEvent event) {
        WorldNutrients.register();
        modEffectsLoaded = false;
    }

}
