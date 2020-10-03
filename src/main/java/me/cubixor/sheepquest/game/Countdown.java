package me.cubixor.sheepquest.game;

import com.cryptomorin.xseries.XSound;
import com.google.common.collect.Iterables;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.TitleAPI;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.GameState;
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
        for (Player p : plugin.getArenas().get(arenaString).getPlayers().keySet()) {
            p.sendMessage(plugin.getMessage("game.countdown-started"));
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                Arena arena = plugin.getArenas().get(arenaString);
                if (arena == null) {
                    this.cancel();
                    return;
                }
                if (!arena.getState().equals(GameState.STARTING)) {
                    this.cancel();
                    return;
                }

                if (arena.getTimer() > 0) {

                    Scoreboard scoreboard = new Scoreboards().getStartingScoreboard(arena);

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

                    int msgTitle = arena.getTimer();
                    float pitch = 0;

                    if (t1.contains(msgTitle)) {
                        pitch = 0.5f;
                        TitleAPI.sendAll(arena, 10, 50, 10,
                                plugin.getMessage("game.countdown-" + msgTitle + "s-title"), plugin.getMessage("game.countdown-" + msgTitle + "s-subtitle"));
                    } else if (t2.contains(msgTitle)) {
                        pitch = 1;
                        TitleAPI.sendAll(arena, 0, 50, 0,
                                plugin.getMessage("game.countdown-" + msgTitle + "s-title"), plugin.getMessage("game.countdown-" + msgTitle + "s-subtitle"));
                        if (msgTitle == 1) {
                            pitch = 2;
                        }
                    }

                    for (Player p : arena.getPlayers().keySet()) {
                        p.setScoreboard(scoreboard);
                        p.setLevel(arena.getTimer());
                        if (pitch != 0) {
                            p.playSound(p.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.countdown")).get().parseSound(), 100, pitch);
                        }
                    }

                    arena.setTimer(arena.getTimer() - 1);
                } else {
                    new Start().start(arena);
                    this.cancel();
                }

            }

        }.runTaskTimer(plugin, 0, 20);

        new BukkitRunnable() {
            @Override
            public void run() {
                Arena arena = plugin.getArenas().get(arenaString);
                if (arena == null) {
                    this.cancel();
                    return;
                }
                if (!arena.getState().equals(GameState.STARTING)) {
                    this.cancel();
                    return;
                }


                if (arena.getTimer() >= 0) {
                    float exp = Iterables.getFirst(arena.getPlayers().keySet(), null).getExp();
                    if (exp <= 0) {
                        exp = 0.94F;
                    } else {
                        exp -= 0.05F;
                    }
                    if (exp < 0) {
                        exp = 0;
                    }


                    for (Player p : arena.getPlayers().keySet()) {
                        p.setExp(exp);
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

}
