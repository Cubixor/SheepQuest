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
        arena.getPlayerKit().put(player, KitType.getFirstEnabled());
        bossBarManager.addTeamBossBar(player, Team.NONE);
        bossBarManager.addKitBossBar(player, KitType.STANDARD);

        if (MinigamesAPI.getPlugin().getConfig().getBoolean("allow-team-choosing")) {
            itemsRegistry.getTeamItem().give(player);
        }

        itemsRegistry.getKitsItem().give(player);
    }

    @EventHandler
    public void onGameLeave(GameLeaveEvent evt) {
        Player player = evt.getPlayer();
        SQArena arena = (SQArena) evt.getLocalArena();

        arena.getPlayerTeam().remove(player);
        arena.getPlayerKit().remove(player);
        arena.getRespawnTimer().remove(player);
        arena.getPlayerGameStats().remove(player);
        bossBarManager.removeTeamBossBar(player);
        bossBarManager.removeKitBossBar(player);

        for (Player p : arena.getBukkitPlayers()) {
            p.showPlayer(player);
            player.showPlayer(p);
        }
    }
}
