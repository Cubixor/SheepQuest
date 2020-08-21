package me.cubixor.sheepquest.game;

import me.cubixor.sheepquest.Arena;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class Chat implements Listener {

    private final SheepQuest plugin;

    public Chat(SheepQuest sq) {
        plugin = sq;
    }

    @EventHandler
    public void blockCommand(PlayerCommandPreprocessEvent evt) {
        Utils utils = new Utils(plugin);
        if (utils.getArena(evt.getPlayer()) == null) {
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

    @EventHandler
    public void onChat(AsyncPlayerChatEvent evt) {
        Utils utils = new Utils(plugin);
        if (utils.getArena(evt.getPlayer()) == null) {
            return;
        }
        if (!plugin.getConfig().getBoolean("game-chat")) {
            return;
        }
        evt.setCancelled(true);

        Arena arena = utils.getArena(evt.getPlayer());

        String team = plugin.getMessage("general." + utils.getTeamString(arena.playerTeam.get(evt.getPlayer())) + "-color");

        for (Player p : arena.playerTeam.keySet()) {
            p.sendMessage(plugin.getMessage("game.chat-format").replace("%player%", evt.getPlayer().getName()).replace("%message%", evt.getMessage()).replace("%color%", team));
        }

    }

}
