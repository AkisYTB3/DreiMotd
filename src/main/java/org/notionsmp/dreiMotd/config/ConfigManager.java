package org.notionsmp.dreiMotd.config;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Nullable;
import org.notionsmp.dreiMotd.DreiMotd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class ConfigManager {
    @Getter
    private List<Map<String, Object>> motds;
    private boolean motdEnabled;

    public ConfigManager() {
        loadConfig();
    }

    public void setupDefaultConfig() {
        FileConfiguration config = DreiMotd.getInstance().getConfig();

        addSetting("enabled", true);
        addSetting("convert-legacy-to-modern", true);
        addSetting("delay", 0);

        config.options().copyDefaults(true);
        DreiMotd.getInstance().saveConfig();
    }

    private void addSetting(String setting, Object value) {
        DreiMotd.getInstance().getConfig().addDefault("settings." + setting, value);
    }

    public @Nullable Object getSetting(String setting) {
        return DreiMotd.getInstance().getConfig().get("settings." + setting);
    }

    public void loadConfig() {
        DreiMotd.getInstance().reloadConfig();
        FileConfiguration config = DreiMotd.getInstance().getConfig();

        if (config.contains("enabled")) {
            motdEnabled = config.getBoolean("enabled");
            config.set("settings.enabled", motdEnabled);
            config.set("enabled", null);
            DreiMotd.getInstance().saveConfig();
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
}