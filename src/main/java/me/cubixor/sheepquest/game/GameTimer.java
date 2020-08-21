package me.cubixor.sheepquest.game;

import me.cubixor.sheepquest.*;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

public class GameTimer {

    private final SheepQuest plugin;

    GameTimer(SheepQuest s) {
        plugin = s;
    }

    public void gameTime(String arenaString) {
        new BukkitRunnable() {
            public void run() {
                Arena arena = plugin.arenas.get(arenaString);

                if (arena == null) {
                    this.cancel();
                    return;
                }
                if (!arena.state.equals(GameState.GAME)) {
                    this.cancel();
                    return;
                }

                if (arena.timer > 0) {
                    Scoreboard scoreboard = new Scoreboards(plugin).getGameScoreboard(arena);

                    for (Player p : arena.playerTeam.keySet()) {
                        p.setScoreboard(scoreboard);
                    }


                    if (arena.sheepTimer == 0) {
                        arena.sheepTimer = plugin.getConfig().getInt("sheep-time");

                        Location loc = (Location) plugin.getArenasConfig().get("Arenas." + arenaString + ".sheep-spawn");
                        Sheep sheep = loc.getWorld().spawn(loc, Sheep.class);
                        sheep.setColor(DyeColor.WHITE);

                        new PathFinding(plugin).walkToLocation(sheep, loc, 0.7, arena, Team.NONE);
                    }

                    arena.timer--;
                    arena.sheepTimer--;
                } else {
                    new End(plugin).gameEnd(arena);
                    this.cancel();
                }
            }

        }.runTaskTimer(plugin, 0, 20);
    }
}
