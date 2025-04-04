package org.notionsmp.dreiMotd.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.notionsmp.dreiMotd.DreiMotd;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.notionsmp.dreiMotd.listeners.PlayerJoinListener;

@CommandAlias("dreimotd|dmotd")
public class DreiMotdCommand extends BaseCommand {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Default
    public void onDefault(CommandSender sender) {
        sender.sendMessage(miniMessage.deserialize("""
    <gradient:gold:yellow>DreiMotd Help</gradient>
    <gray>/dreimotd reload</gray> - Reload config
    <gray>/dreimotd test</gray> - Test MOTD
    <gray>/dreimotd list</gray> - List all MOTDs"""));
    }

    @Subcommand("reload|rl")
    @CommandPermission("dreimotd.reload")
    public void onReload(CommandSender sender) {
        DreiMotd.getInstance().getConfigManager().loadConfig();
        sender.sendMessage(miniMessage.deserialize("<green>Config reloaded!</green>"));
    }

    @Subcommand("test")
    @CommandPermission("dreimotd.test")
    public void onTest(Player player) {
        player.sendMessage(miniMessage.deserialize("<gradient:gold:yellow>Testing MOTD...</gradient>"));
        new PlayerJoinListener().sendMotd(player);
    }

    @Subcommand("list|ls")
    @CommandPermission("dreimotd.list")
    public void onList(CommandSender sender) {
        new ListCommand().execute(sender);
    }
}