package org.notionsmp.dreiMotd.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.notionsmp.dreiMotd.DreiMotd;
import org.notionsmp.dreiMotd.utils.MessageUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerJoinListener implements Listener {
    private final MessageUtils messageUtils;

    public PlayerJoinListener() {
        this.messageUtils = new MessageUtils();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!DreiMotd.getInstance().getConfigManager().isMotdEnabled()) return;
        sendMotd(event.getPlayer());
    }

    public void sendMotd(Player player) {
        sendMotd(player, null);
    }

    public void sendMotd(Player player, List<String> permissionsToTest) {
        List<Map<String, Object>> motds = DreiMotd.getInstance().getConfigManager().getMotds();

        if (permissionsToTest != null && !permissionsToTest.isEmpty()) {
            motds = motds.stream()
                    .filter(motd -> {
                        String motdPermission = (String) motd.getOrDefault("permission", "");

                        return motdPermission.isEmpty() || permissionsToTest.contains(motdPermission);
                    })
                    .collect(Collectors.toList());
        }

        for (Map<String, Object> motd : motds) {
            String permission = (String) motd.getOrDefault("permission", "");
            if (!permission.isEmpty() && !player.hasPermission(permission)) {
                continue;
            }

            Object message = motd.get("message");
            if (message instanceof List) {
                for (String line : (List<String>) message) {
                    messageUtils.sendFormattedMessage(player, line);
                }
            } else if (message instanceof String) {
                messageUtils.sendFormattedMessage(player, (String) message);
            }
        }
    }
}