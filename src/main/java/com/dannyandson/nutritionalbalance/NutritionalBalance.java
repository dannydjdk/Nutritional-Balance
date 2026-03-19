package com.dannyandson.nutritionalbalance;

import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.dannyandson.nutritionalbalance.commands.ModCommands;
import com.dannyandson.nutritionalbalance.events.*;
import com.dannyandson.nutritionalbalance.keybinding.ModInputHandler;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.setup.ClientSetup;
import com.dannyandson.nutritionalbalance.setup.Registration;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.*;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("nutritionalbalance")
public class NutritionalBalance
{
    public static final String MODID = "nutritionalbalance";
    public static final Logger LOGGER = LogManager.getLogger();

    public static boolean modEffectsLoaded = false;

    public NutritionalBalance(IEventBus modEventBus, ModContainer modContainer) {

        Registration.register(modEventBus);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);
        if(FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(ClientSetup::init);
            modEventBus.addListener(ClientSetup::addCreative);
            modEventBus.addListener(ClientSetup::registerClientExtensions);
            modEventBus.addListener(ClientSetup::registerAdditionalModels);
        }

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new EventUseItem());
        NeoForge.EVENT_BUS.register(new EventPlayerTick());
        NeoForge.EVENT_BUS.register(new EventPlayerJoin());
        NeoForge.EVENT_BUS.register(new EventPlayerClone());
        NeoForge.EVENT_BUS.register(new EventRightClickBlock());
        NeoForge.EVENT_BUS.addListener(this::registerCommands);
        NeoForge.EVENT_BUS.addListener(this::serverStarted);
        NeoForge.EVENT_BUS.register(new ModInputHandler());

        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

        modEventBus.addListener(ModNetworkHandler::registerMessages);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    }

    private void registerCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new EventTooltip());
        NeoForge.EVENT_BUS.register(new EventNutrientButton());
    }

    @SubscribeEvent
    public void serverStarted(ServerStartedEvent event){
        PlayerNutritionData.init(event.getServer().overworld());
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onTagsUpdated(TagsUpdatedEvent event) {
        WorldNutrients.register();
        modEffectsLoaded = false;
    }

}