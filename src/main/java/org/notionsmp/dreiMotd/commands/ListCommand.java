package org.notionsmp.dreiMotd.commands;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.notionsmp.dreiMotd.DreiMotd;

import java.util.List;
import java.util.Map;

public class ListCommand {
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public void execute(CommandSender sender) {
        sender.sendMessage(miniMessage.deserialize("<gradient:gold:yellow>DreiMotd List</gradient>"));

        List<Map<String, Object>> motds = DreiMotd.getInstance().getConfigManager().getMotds();
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
}