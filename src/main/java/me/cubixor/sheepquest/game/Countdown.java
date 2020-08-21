package me.cubixor.sheepquest.game;

import com.google.common.collect.Iterables;
import me.cubixor.sheepquest.Arena;
import me.cubixor.sheepquest.GameState;
import me.cubixor.sheepquest.Scoreboards;
import me.cubixor.sheepquest.SheepQuest;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

public class Countdown {

    private final SheepQuest plugin;

    public Countdown(SheepQuest s) {
        plugin = s;
    }

    public void time(String arenaString) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Arena arena = plugin.arenas.get(arenaString);
                if (arena == null) {
                    this.cancel();
                    return;
                }
                if (!arena.state.equals(GameState.STARTING)) {
                    this.cancel();
                    return;
                }


                if (arena.timer > 0) {

                    Scoreboard scoreboard = new Scoreboards(plugin).getStartingScoreboard(arena);

                    for (Player p : arena.playerTeam.keySet()) {
                        p.setScoreboard(scoreboard);
                        p.setLevel(arena.timer);
                    }

                    arena.timer--;
                } else {
                    new Start(plugin).start(arena);
                    this.cancel();
                }

            }

        }.runTaskTimer(plugin, 0, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                Arena arena = plugin.arenas.get(arenaString);
                if (arena == null) {
                    this.cancel();
                    return;
                }
                if (!arena.state.equals(GameState.STARTING)) {
                    this.cancel();
                    return;
                }


                if (arena.timer > 0) {
                    float exp = Iterables.getFirst(arena.playerTeam.keySet(), null).getExp();
                    if (exp <= 0) {
                        exp = 0.94F;
                    } else {
                        exp -= 0.05F;
                    }
                    if (exp < 0) {
                        exp = 0;
                    }


                    for (Player p : arena.playerTeam.keySet()) {
                        p.setExp(exp);
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

}
