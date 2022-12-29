package me.cubixor.sheepquest.spigot.game;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Chat implements Listener {

    private final SheepQuest plugin;

    public Chat() {
        plugin = SheepQuest.getInstance();
    }

    @EventHandler
    public void blockCommand(PlayerCommandPreprocessEvent evt) {
        if (Utils.getLocalArena(evt.getPlayer()) == null) {
            return;
        }
        if (evt.getPlayer().hasPermission("sheepquest.bypass")) {
            return;
        }
        if (plugin.getConfig().getString("command-blocker").equals("BLACKLIST")) {
            for (String s : plugin.getConfig().getStringList("command-blocker-list")) {
                if (evt.getMessage().startsWith("/" + s)) {
                    evt.setCancelled(true);
                    evt.getPlayer().sendMessage(plugin.getMessage("game.command-blocked"));
                }
            }
        } else if (plugin.getConfig().getString("command-blocker").equals("WHITELIST")) {
            boolean cancel = true;

            if (evt.getMessage().startsWith("/sq") || evt.getMessage().startsWith("/sheepquest")) {
                return;
            }

            for (String s : plugin.getConfig().getStringList("command-blocker-list")) {
                if (evt.getMessage().startsWith("/" + s)) {
                    cancel = false;
                    break;
                }
            }
            if (cancel) {
                evt.setCancelled(true);
                evt.getPlayer().sendMessage(plugin.getMessage("game.command-blocked"));
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent evt) {
        if (Utils.getLocalArena(evt.getPlayer()) == null) {
            return;
        }
        if (!plugin.getConfig().getBoolean("game-chat")) {
            return;
        }
        evt.setCancelled(true);

        LocalArena localArena = Utils.getLocalArena(evt.getPlayer());

        String team = localArena.getPlayerTeam().get(evt.getPlayer()).getChatColor() + "";

        for (Player p : localArena.getPlayerTeam().keySet()) {
            p.sendMessage(plugin.getMessage("game.chat-format").replace("%player%", evt.getPlayer().getName()).replace("%message%", evt.getMessage()).replace("%color%", team));
        }

    }

}
