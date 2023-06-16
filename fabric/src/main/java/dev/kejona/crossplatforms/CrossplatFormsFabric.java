package dev.kejona.crossplatforms;

import com.google.inject.Module;
import dev.kejona.crossplatforms.config.ConfigManager;
import dev.kejona.crossplatforms.handler.FabricHandler;
import dev.kejona.crossplatforms.handler.FabricPermissions;
import dev.kejona.crossplatforms.handler.PlaceholderAPIHandler;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.permission.LuckPermsHook;
import dev.kejona.crossplatforms.permission.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerLoginNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.bstats.charts.CustomChart;

import java.util.List;

public class CrossplatFormsFabric implements ModInitializer, CrossplatFormsBootstrap {

    private MinecraftServer server;

    static {
        // load information from build.properties
        Constants.fetch();
    }

    protected Logger logger;

    @Override
    public void onInitialize() {
        logger = new JavaUtilLogger(java.util.logging.Logger.getLogger("CrossplatFormsFabric"));

        // ServerLoginNetworking.registerGlobalReceiver("BungeeCord", this);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            logger.info("Server started");
            this.server = server;
        });
        ServerHandler serverHandler = new FabricHandler(server);
        Permissions permissions = FabricLoader.getInstance().isModLoaded("LuckPerms") ? new LuckPermsHook() : new FabricPermissions(this);

        // TODO: COMMANDMANAGER

        //TODO: https://modrinth.com/mod/placeholder-api - PlaceholderAPI on fabric
        //Placeholders placeholders;
        //if (FabricLoader.getInstance().isModLoaded("PlaceholderAPI")) {
        //    placeholders = new ();
        //} else {
        //    placeholders = new Placeholders();
        //}
    }

    @Override
    public List<Module> configModules() {
        return null;
    }

    @Override
    public void preConfigLoad(ConfigManager configManager) {

    }

    @Override
    public void addCustomChart(CustomChart chart) {
        // no bstats impl yet
    }


}