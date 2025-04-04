package org.notionsmp.dreiMotd;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class DreiMotd extends JavaPlugin implements Listener, TabExecutor {

    private MiniMessage miniMessage;
    private List<Map<String, Object>> motds;
    private boolean motdEnabled;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupDefaultConfig();
        miniMessage = MiniMessage.miniMessage();
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("dreimotd").setExecutor(this);
        getCommand("dreimotd").setTabCompleter(this);
    }

    private void setupDefaultConfig() {
        FileConfiguration config = getConfig();

        config.addDefault("settings.enabled", true);
        config.addDefault("settings.convert-legacy-to-modern", true);

        config.options().copyDefaults(true);
        saveConfig();
    }

    private void loadConfig() {
        reloadConfig();
        FileConfiguration config = getConfig();

        if (config.contains("enabled")) {
            motdEnabled = config.getBoolean("enabled");
            config.set("settings.enabled", motdEnabled);
            config.set("enabled", null);
            saveConfig();
        } else {
            motdEnabled = config.getBoolean("settings.enabled", true);
        }

        motds = new ArrayList<>();
        ConfigurationSection motdsSection = config.getConfigurationSection("motds");
        if (motdsSection != null) {
            for (String key : motdsSection.getKeys(false)) {
                motds.add(motdsSection.getConfigurationSection(key).getValues(true));
            }
        }

        if (motds.isEmpty()) {
            Object oldFormat = config.isList("motd") ? config.getStringList("motd") : config.getString("motd");
            if (oldFormat != null) {
                Map<String, Object> legacyMotd = Map.of(
                        "message", oldFormat,
                        "permission", ""
                );
                motds.add(legacyMotd);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!motdEnabled) return;
        sendMotd(event.getPlayer());
    }

    private void sendMotd(Player player) {
        String playerName = player.getName();

        for (Map<String, Object> motd : motds) {
            String permission = (String) motd.getOrDefault("permission", "");
            if (!permission.isEmpty() && !player.hasPermission(permission)) {
                continue;
            }

            Object message = motd.get("message");
            if (message instanceof List) {
                for (String line : (List<String>) message) {
                    sendFormattedMessage(player, line);
                }
            } else if (message instanceof String) {
                sendFormattedMessage(player, (String) message);
            }
        }
    }

    private void sendFormattedMessage(Player player, String message) {
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        if (getConfig().getBoolean("settings.convert-legacy-to-modern", true)) message = convertLegacyColors(message);

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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("dreimotd.reload")) {
                    sender.sendMessage(miniMessage.deserialize("<red>No permission!</red>"));
                    return true;
                }
                reloadConfig();
                loadConfig();
                sender.sendMessage(miniMessage.deserialize("<green>Config reloaded!</green>"));
                return true;

            case "test":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(miniMessage.deserialize("<red>Only players can test!</red>"));
                    return true;
                }
                if (!sender.hasPermission("dreimotd.test")) {
                    sender.sendMessage(miniMessage.deserialize("<red>No permission!</red>"));
                    return true;
                }
                testMotd((Player) sender);
                return true;

            case "list":
                if (!sender.hasPermission("dreimotd.list")) {
                    sender.sendMessage(miniMessage.deserialize("<red>No permission!</red>"));
                    return true;
                }
                listMotds(sender);
                return true;

            default:
                showHelp(sender);
                return true;
        }
    }

    private void listMotds(CommandSender sender) {
        sender.sendMessage(miniMessage.deserialize("<gradient:gold:yellow>DreiMotd List</gradient>"));

        for (int i = 0; i < motds.size(); i++) {
            Map<String, Object> motd = motds.get(i);
            String permission = (String) motd.getOrDefault("permission", "none");
            String preview = motd.get("message") instanceof List ?
                    ((List<?>) motd.get("message")).get(0).toString() :
                    motd.get("message").toString();

            if (preview.length() > 30) {
                preview = preview.substring(0, 27) + "...";
            }

            sender.sendMessage(miniMessage.deserialize(
                    "<gray>#"+ (i+1) + "</gray> - " +
                            "<white>" + preview + "</white>\n" +
                            "  <gray>Permission: " + permission + "</gray>"
            ));
        }
    }

    private void testMotd(Player player) {
        player.sendMessage(miniMessage.deserialize("<gradient:gold:yellow>Testing MOTD...</gradient>"));
        sendMotd(player);
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(miniMessage.deserialize(
                "<gradient:gold:yellow>DreiMotd Help</gradient>\n" +
                        "<gray>/dreimotd reload</gray> - Reload config\n" +
                        "<gray>/dreimotd test</gray> - Test MOTD\n" +
                        (sender.hasPermission("dreimotd.list") ? "<gray>/dreimotd list</gray> - List all MOTDs\n" : "")
        ));
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("dreimotd.reload")) completions.add("reload");
            if (sender.hasPermission("dreimotd.test") && sender instanceof Player) completions.add("test");
            if (sender.hasPermission("dreimotd.list")) completions.add("list");
            return completions;
        }
        return new ArrayList<>();
    }

    private String convertLegacyColors(String text) {
        if (text == null) return null;

        String result = text;

        result = result.replaceAll("(?i)&0", "<black>")
                .replaceAll("(?i)&1", "<dark_blue>")
                .replaceAll("(?i)&2", "<dark_green>")
                .replaceAll("(?i)&3", "<dark_aqua>")
                .replaceAll("(?i)&4", "<dark_red>")
                .replaceAll("(?i)&5", "<dark_purple>")
                .replaceAll("(?i)&6", "<gold>")
                .replaceAll("(?i)&7", "<gray>")
                .replaceAll("(?i)&8", "<dark_gray>")
                .replaceAll("(?i)&9", "<blue>")
                .replaceAll("(?i)&a", "<green>")
                .replaceAll("(?i)&b", "<aqua>")
                .replaceAll("(?i)&c", "<red>")
                .replaceAll("(?i)&d", "<light_purple>")
                .replaceAll("(?i)&e", "<yellow>")
                .replaceAll("(?i)&f", "<white>")
                .replaceAll("(?i)&k", "<obf>")
                .replaceAll("(?i)&l", "<b>")
                .replaceAll("(?i)&m", "<st>")
                .replaceAll("(?i)&n", "<u>")
                .replaceAll("(?i)&o", "<i>")
                .replaceAll("(?i)&r", "<r>");

        result = result.replaceAll("(?i)&#([0-9a-f]{6})", "<#$1>");

        return result;
    }
}