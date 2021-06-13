package me.cubixor.sheepquest.spigot.game;

import com.cryptomorin.xseries.XSound;
import com.google.common.collect.Iterables;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class Countdown {

    private final SheepQuest plugin;

    public Countdown() {
        plugin = SheepQuest.getInstance();
    }


    public void time(String arenaString) {
        for (Player p : plugin.getLocalArenas().get(arenaString).getPlayerTeam().keySet()) {
            p.sendMessage(plugin.getMessage("game.countdown-started"));
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                LocalArena localArena = plugin.getLocalArenas().get(arenaString);
                if (localArena == null) {
                    this.cancel();
                    return;
                }
                if (!localArena.getState().equals(GameState.STARTING)) {
                    this.cancel();
                    return;
                }

                if (localArena.getTimer() > 0) {

                    Scoreboard scoreboard = new Scoreboards().getStartingScoreboard(localArena);

                    List<Integer> t1 = new ArrayList<Integer>() {{
                        add(60);
                        add(30);
                        add(10);
                    }};
                    List<Integer> t2 = new ArrayList<Integer>() {{
                        add(5);
                        add(4);
                        add(3);
                        add(2);
                        add(1);
                    }};

                    int msgTitle = localArena.getTimer();
                    float pitch = 0;

                    if (t1.contains(msgTitle)) {
                        pitch = 0.5f;
                        for (Player p : localArena.getPlayerTeam().keySet()) {
                            p.sendTitle(plugin.getMessage("game.countdown-" + msgTitle + "s-title"), plugin.getMessage("game.countdown-" + msgTitle + "s-subtitle"), 10, 50, 10);
                        }
                    } else if (t2.contains(msgTitle)) {
                        pitch = 1;
                        for (Player p : localArena.getPlayerTeam().keySet()) {
                            p.sendTitle(plugin.getMessage("game.countdown-" + msgTitle + "s-title"), plugin.getMessage("game.countdown-" + msgTitle + "s-subtitle"), 0, 50, 0);
                        }
                        if (msgTitle == 1) {
                            pitch = 2;
                        }
                    }

                    for (Player p : localArena.getPlayerTeam().keySet()) {
                        p.setScoreboard(scoreboard);
                        p.setLevel(localArena.getTimer());
                        if (pitch != 0) {
                            p.playSound(p.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.countdown")).get().parseSound(), 100, pitch);
                        }
                    }

                    localArena.setTimer(localArena.getTimer() - 1);
                } else {
                    new Start().start(localArena);
                    this.cancel();
                }

            }

        }.runTaskTimer(plugin, 0, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                LocalArena localArena = plugin.getLocalArenas().get(arenaString);
                if (localArena == null) {
                    this.cancel();
                    return;
                }
                if (!localArena.getState().equals(GameState.STARTING)) {
                    this.cancel();
                    return;
                }


                if (localArena.getTimer() >= 0) {
                    float exp = Iterables.getFirst(localArena.getPlayerTeam().keySet(), null).getExp();
                    if (exp <= 0) {
                        exp = 0.94F;
                    } else {
                        exp -= 0.05F;
                    }
                    if (exp < 0) {
                        exp = 0;
                    }


                    for (Player p : localArena.getPlayerTeam().keySet()) {
                        p.setExp(exp);
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

}
