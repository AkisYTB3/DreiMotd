package org.notionsmp.dreiMotd;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.notionsmp.dreiMotd.commands.DreiMotdCommand;
import org.notionsmp.dreiMotd.config.ConfigManager;
import org.notionsmp.dreiMotd.listeners.PlayerJoinListener;
import org.notionsmp.dreiMotd.utils.Metrics;

@Getter
public final class DreiMotd extends JavaPlugin {
    @Getter
    private static DreiMotd instance;
    private ConfigManager configManager;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        instance = this;
        configManager = new ConfigManager();
        configManager.setupDefaultConfig();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new DreiMotdCommand());

        initMetrics();
    }

    private void initMetrics() {
        Metrics metrics = new Metrics(this, 25705);
    }
}