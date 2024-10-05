package me.cubixor.sheepquest.game.events;

import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;
import me.cubixor.minigamesapi.spigot.integrations.PlaceholderParseEvent;
import me.cubixor.sheepquest.arena.SQArena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlaceholderManager implements Listener {

    private final ArenasRegistry arenasRegistry;

    public PlaceholderManager(ArenasRegistry arenasRegistry) {
        this.arenasRegistry = arenasRegistry;
    }

    @EventHandler
    public void onPlaceholderParse(PlaceholderParseEvent evt) {
        if (!(evt.getPlayer() instanceof Player)) return;
        Player player = (Player) evt.getPlayer();

        if (evt.getParams().equals("team")) {
            SQArena arena = (SQArena) arenasRegistry.getPlayerLocalArena(player);
            if (arena == null) {
                evt.setParsed("");
                return;
            }

            evt.setParsed(arena.getPlayerTeam().get(player).getName());
        }
    }
}
