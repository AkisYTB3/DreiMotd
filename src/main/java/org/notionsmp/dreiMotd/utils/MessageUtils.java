package org.notionsmp.dreiMotd.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.notionsmp.dreiMotd.DreiMotd;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageUtils {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    public void sendFormattedMessage(Player player, String message) {
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        if (DreiMotd.getInstance().getConfig().getBoolean("settings.convert-legacy-to-modern", true)) {
            message = convertLegacyColors(message);
        }

        player.sendMessage(miniMessage.deserialize(message,
                Placeholder.unparsed("player", player.getName()),
                Placeholder.unparsed("displayname", miniMessage.serialize(player.displayName())),
                Placeholder.unparsed("world", player.getWorld().getName()),
                Placeholder.unparsed("online_players", String.valueOf(Bukkit.getOnlinePlayers().size())),
                Placeholder.unparsed("max_players", String.valueOf(Bukkit.getMaxPlayers())),
                Placeholder.unparsed("server_name", Bukkit.getServer().getName()),
                Placeholder.unparsed("server_version", Bukkit.getServer().getVersion()),
                Placeholder.unparsed("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))),
                Placeholder.unparsed("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
        ));
    }

    private String convertLegacyColors(String text) {
        if (text == null) return null;

        String result = text;

        result = result.replaceAll("(?i)[&§]0", "<black>")
                .replaceAll("(?i)[&§]1", "<dark_blue>")
                .replaceAll("(?i)[&§]2", "<dark_green>")
                .replaceAll("(?i)[&§]3", "<dark_aqua>")
                .replaceAll("(?i)[&§]4", "<dark_red>")
                .replaceAll("(?i)[&§]5", "<dark_purple>")
                .replaceAll("(?i)[&§]6", "<gold>")
                .replaceAll("(?i)[&§]7", "<gray>")
                .replaceAll("(?i)[&§]8", "<dark_gray>")
                .replaceAll("(?i)[&§]9", "<blue>")
                .replaceAll("(?i)[&§]a", "<green>")
                .replaceAll("(?i)[&§]b", "<aqua>")
                .replaceAll("(?i)[&§]c", "<red>")
                .replaceAll("(?i)[&§]d", "<light_purple>")
                .replaceAll("(?i)[&§]e", "<yellow>")
                .replaceAll("(?i)[&§]f", "<white>")
                .replaceAll("(?i)[&§]k", "<obf>")
                .replaceAll("(?i)[&§]l", "<b>")
                .replaceAll("(?i)[&§]m", "<st>")
                .replaceAll("(?i)[&§]n", "<u>")
                .replaceAll("(?i)[&§]o", "<i>")
                .replaceAll("(?i)[&§]r", "<r>");

        result = result.replaceAll("(?i)[&§]#([0-9a-f]{6})", "<#$1>");

        return result;
    }
}