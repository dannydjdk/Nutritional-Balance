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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
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

import java.util.List;

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
        NeoForge.EVENT_BUS.addListener(this::onDatapackSync);
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
        // Pre-resolve nutrients for every food item now, while no player is connected, so the cost is
        // absorbed into world load instead of into the first player's login. With the recipe-output
        // index in WorldNutrients this is fast even on heavy modpacks.
        WorldNutrients.prewarm(event.getServer().overworld());
    }

    /**
     * Fires after a player joins (per-player) and after /reload (with all online players relevant).
     * Used for two things:
     *   1) Re-warming the nutrient cache after /reload, since TagsUpdatedEvent already cleared it.
     *   2) Sending the entire pre-resolved cache to the client(s) in one bulk packet, so JEI/REI/EMI
     *      tooltip indexing on the client doesn't trigger a flood of per-item NutrientDataSyncTrigger
     *      round-trips back to the server.
     */
    @SubscribeEvent
    public void onDatapackSync(OnDatapackSyncEvent event) {
        List<ServerPlayer> players = event.getRelevantPlayers().toList();
        if (players.isEmpty()) return;
        MinecraftServer server = players.get(0).getServer();
        if (server == null) return;

        // Idempotent — only does work if reset() has cleared prewarmComplete (i.e. after /reload).
        WorldNutrients.prewarm(server.overworld());

        NutrientDataBulkSync packet = new NutrientDataBulkSync(WorldNutrients.snapshotForSync());
        for (ServerPlayer player : players) {
            ModNetworkHandler.sendToClient(packet, player);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onTagsUpdated(TagsUpdatedEvent event) {
        WorldNutrients.register();
        modEffectsLoaded = false;
    }

}