package dev.kejona.crossplatforms.handler;

import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.command.DispatchableCommand;
import dev.kejona.crossplatforms.command.custom.InterceptCommandCache;
import net.fabricmc.loader.api.FabricLoader;
import net.kyori.adventure.audience.Audience;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class FabricHandler extends InterceptCommandCache implements ServerHandler {

    private final MinecraftServer server;

    public FabricHandler(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public FormPlayer getPlayer(UUID uuid) {
        return new FabricPlayer(server.getPlayerList().getPlayer(uuid));
    }

    @Override
    public FormPlayer getPlayer(String name) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getName().getString().equals(name)) {
                return new FabricPlayer(player);
            }
        }
        return null;
    }

    @Override
    public Stream<FormPlayer> getPlayers() {
        return server.getPlayerList().getPlayers().stream().map(FabricPlayer::new);
    }

    @Override
    public Stream<String> getPlayerNames() {
        return server.getPlayerList().getPlayers().stream().map(player -> player.getName().getString());
    }

    @NotNull
    @Override
    public Audience asAudience(CommandOrigin origin) {
        return null; // TODO
    }

    @Override
    public boolean isGeyserEnabled() {
        return FabricLoader.getInstance().isModLoaded("geyser");
    }

    @Override
    public boolean isFloodgateEnabled() {
        return FabricLoader.getInstance().isModLoaded("floodgate");
    }

    @Override
    public void dispatchCommand(DispatchableCommand command) {
        // TODO
    }

    @Override
    public void dispatchCommands(List<DispatchableCommand> commands) {
        // TODO
    }

    @Override
    public void dispatchCommand(UUID player, DispatchableCommand command) {
        // TODO
    }

    @Override
    public void dispatchCommands(UUID player, List<DispatchableCommand> commands) {
        // TODO
    }

    @Override
    public void executeSafely(Runnable runnable) {
        server.execute(runnable);
    }
}
