package org.notionsmp.dreiMotd.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.notionsmp.dreiMotd.DreiMotd;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.notionsmp.dreiMotd.listeners.PlayerJoinListener;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CommandAlias("dreimotd|dmotd")
public class DreiMotdCommand extends BaseCommand {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    @Default
    public void onDefault(CommandSender sender) {
        sender.sendMessage(miniMessage.deserialize("""
            <gradient:gold:yellow>DreiMotd Help</gradient>
            <gray>/dreimotd reload</gray> - Reload config
            <gray>/dreimotd test</gray> - Test MOTD <dark_gray>(optional args: -p <permissions>)</dark_gray>
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
    @CommandCompletion("-p @nothing")
    public void onTest(Player player, @Optional String[] args) {
        List<String> permissionsToTest = null;

        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("-p") && i + 1 < args.length) {
                    permissionsToTest = Arrays.stream(Arrays.copyOfRange(args, i + 1, args.length))
                            .collect(Collectors.toList());
                    break;
                }
            }
        }

        new PlayerJoinListener().sendMotd(player, permissionsToTest);
    }

    @Subcommand("list|ls")
    @CommandPermission("dreimotd.list")
    public void onList(CommandSender sender) {
        new ListCommand().execute(sender);
    }
}