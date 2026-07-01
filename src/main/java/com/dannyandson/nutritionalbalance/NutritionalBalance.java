package com.dannyandson.nutritionalbalance;

import com.dannyandson.nutritionalbalance.network.NutrientDataBulkSync;
import com.dannyandson.nutritionalbalance.nutrients.PlayerNutritionData;
import com.dannyandson.nutritionalbalance.nutrients.WorldNutrients;
import com.dannyandson.nutritionalbalance.commands.ModCommands;
import com.dannyandson.nutritionalbalance.events.*;
import com.dannyandson.nutritionalbalance.keybinding.ModInputHandler;
import com.dannyandson.nutritionalbalance.network.ModNetworkHandler;
import com.dannyandson.nutritionalbalance.setup.ClientSetup;
import com.dannyandson.nutritionalbalance.setup.Registration;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.*;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mod("nutritionalbalance")
public class NutritionalBalance
{
    public static final String MODID = "nutritionalbalance";
    public static final Logger LOGGER = LogManager.getLogger();

    // Listener key for AddServerReloadListenersEvent.addListener.
    private static final Identifier RELOAD_LISTENER_ID =
            Identifier.fromNamespaceAndPath(MODID, "nutrient_cache");

    public static boolean modEffectsLoaded = false;

    public NutritionalBalance(IEventBus modEventBus, ModContainer modContainer) {

        Registration.register(modEventBus);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::doClientStuff);
        if(FMLEnvironment.getDist().isClient()) {
            modEventBus.addListener(ClientSetup::init);
            modEventBus.addListener(ClientSetup::addCreative);
            modEventBus.addListener(ClientSetup::registerSpecialRenderers);
        }

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new EventUseItem());
        NeoForge.EVENT_BUS.register(new EventPlayerTick());
        NeoForge.EVENT_BUS.register(new EventPlayerJoin());
        NeoForge.EVENT_BUS.register(new EventPlayerClone());
        NeoForge.EVENT_BUS.register(new EventRightClickBlock());
        NeoForge.EVENT_BUS.register(new ModInputHandler());

        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_CONFIG);
        modContainer.registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);

        modEventBus.addListener(ModNetworkHandler::registerMessages);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(new EventTooltip());
        NeoForge.EVENT_BUS.register(new EventNutrientButton());
    }

    @SubscribeEvent
    public void serverStarted(ServerStartedEvent event){
        PlayerNutritionData.init(event.getServer().overworld());
        // Pre-resolve nutrients before any player connects so first-login isn't paying the cost.
        WorldNutrients.prewarm(event.getServer().overworld());
    }

    /**
     * Registers our reload listener. Runs after vanilla listeners by default, so tags and recipes are
     * committed when our apply phase fires. This handles /reload — rebuilds the cache and re-broadcasts
     * to all connected clients so modpack builders don't see tooltip flicker after tweaking nutrients.
     */
    @SubscribeEvent
    public void addServerReloadListeners(AddServerReloadListenersEvent event) {
        event.addListener(RELOAD_LISTENER_ID, new NutrientReloadListener());
    }

    /**
     * Reload listener for the nutrient cache. Real work happens in apply (after every vanilla listener),
     * deferred to the next server tick so we don't race the reload pipeline.
     */
    private static class NutrientReloadListener implements PreparableReloadListener {
        @Override
        public CompletableFuture<Void> reload(SharedState sharedState,
                                              Executor backgroundExecutor,
                                              PreparationBarrier barrier,
                                              Executor mainThreadExecutor) {
            return CompletableFuture
                    .supplyAsync(() -> (Void) null, backgroundExecutor)
                    .thenCompose(barrier::wait)
                    .thenAcceptAsync(unused -> {
                        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                        if (server == null) return;  // initial datapack load, before server is constructed

                        // Defer to next tick so we run after the reload pipeline is fully done.
                        server.execute(() -> {
                            ServerLevel overworld = server.overworld();
                            if (overworld == null) return;

                            // Cache was cleared by onTagsUpdated -> register() -> reset(). Rebuild it now,
                            // even with no players connected, so future joiners hit a warm cache.
                            WorldNutrients.prewarm(overworld);

                            // Skip snapshot allocation if there's no one to broadcast to.
                            var players = server.getPlayerList().getPlayers();
                            if (players.isEmpty()) return;

                            NutrientDataBulkSync packet = new NutrientDataBulkSync(WorldNutrients.snapshotForSync());
                            for (ServerPlayer player : players) {
                                ModNetworkHandler.sendToClient(packet, player);
                            }
                        });
                    }, mainThreadExecutor);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onTagsUpdated(TagsUpdatedEvent event) {
        WorldNutrients.register();
        modEffectsLoaded = false;
    }

}