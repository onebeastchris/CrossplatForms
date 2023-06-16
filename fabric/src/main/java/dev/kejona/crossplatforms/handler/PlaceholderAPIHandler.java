package dev.kejona.crossplatforms.handler;

import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHandler implements Placeholders {

    @Override
    public String setPlaceholders(@NotNull FormPlayer player, @NotNull String text) {
        if (text.isEmpty()) {
            return text;
        }

        return PlaceholderAPI.setPlaceholders(player.getHandle(Player.class), text);
    }
}
