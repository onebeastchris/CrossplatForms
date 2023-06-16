package dev.kejona.crossplatforms.handler;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.network.ServerPlayerConnection;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FabricPlayer implements FormPlayer {

    private final ServerPlayer handle;

    public FabricPlayer(ServerPlayer player) {
        this.handle = player;
    }

    @Override
    public UUID getUuid() {
        return handle.getUUID();
    }

    @Override
    public String getName() {
        return handle.getDisplayName().getString();
    }

    @Override
    public boolean hasPermission(String permission) {
        return ; // TODO
    }

    @Nullable
    @Override
    public String getEncodedSkinData() {
        GameProfile profile = handle.getGameProfile();
        for (Property textures : profile.getProperties().get("textures")) {
            String value = textures.getValue();
            if (!value.isEmpty()) {
                return value;
            }
        }
        return null;
    }

    @Override
    public void sendRaw(Component component) {
        handle.sendSystemMessage((net.minecraft.network.chat.Component) component);
    }

    @Override
    public void sendMessage(String message) {
        FormPlayer.super.sendMessage(message);
    }

    @Override
    public void sendMessage(TextComponent component) {
        FormPlayer.super.sendMessage(component);
    }

    @Override
    public void warn(String message) {
        FormPlayer.super.warn(message);
    }

    @Override
    public boolean switchBackendServer(String server) {
        return false;
    }

    @Override
    public <T> T getHandle(Class<T> asType) throws ClassCastException {
        return null;
    }
}
