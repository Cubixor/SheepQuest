package me.cubixor.sheepquest.game.events;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.events.GameJoinEvent;
import me.cubixor.minigamesapi.spigot.events.GameLeaveEvent;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.arena.Team;
import me.cubixor.sheepquest.game.BossBarManager;
import me.cubixor.sheepquest.game.kits.KitType;
import me.cubixor.sheepquest.items.SQItemsRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameJoinLeaveHandler implements Listener {

    private final SQItemsRegistry itemsRegistry;
    private final BossBarManager bossBarManager;

    public GameJoinLeaveHandler(SQItemsRegistry itemsRegistry, BossBarManager bossBarManager) {
        this.itemsRegistry = itemsRegistry;
        this.bossBarManager = bossBarManager;
    }

    @EventHandler
    public void onGameJoin(GameJoinEvent evt) {
        Player player = evt.getPlayer();
        SQArena arena = (SQArena) evt.getLocalArena();

        arena.getPlayerTeam().put(player, Team.NONE);
        arena.getPlayerKit().put(player, KitType.STANDARD);
        bossBarManager.addPlayer(player, Team.NONE);

        if (MinigamesAPI.getPlugin().getConfig().getBoolean("allow-team-choosing")) {
            itemsRegistry.getTeamItem().give(player);
        }
        if (MinigamesAPI.getPlugin().getConfig().getBoolean("kits.enabled")) {
            itemsRegistry.getKitsItem().give(player);
        }
    }

    @EventHandler
    public void onGameLeave(GameLeaveEvent evt) {
        Player player = evt.getPlayer();
        SQArena arena = (SQArena) evt.getLocalArena();

        arena.getPlayerTeam().remove(player);
        arena.getPlayerKit().remove(player);
        bossBarManager.removePlayer(player);

        //TODO Respawn
    }
}
