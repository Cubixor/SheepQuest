package me.cubixor.sheepquest.game;

import com.cryptomorin.xseries.XSound;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.GameState;
import me.cubixor.sheepquest.gameInfo.Team;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.List;

public class GameTimer {

    private final SheepQuest plugin;

    GameTimer() {
        plugin = SheepQuest.getInstance();
    }

    public void gameTime(String arenaString) {
        List<Integer> timePoints = new ArrayList<Integer>() {{
            add(30);
            add(10);
            add(5);
            add(4);
            add(3);
            add(2);
            add(1);

        }};

        new BukkitRunnable() {
            public void run() {
                Arena arena = plugin.getArenas().get(arenaString);

                if (arena == null) {
                    this.cancel();
                    return;
                }
                if (!arena.getState().equals(GameState.GAME)) {
                    this.cancel();
                    return;
                }

                if (arena.getTimer() > 0) {
                    if (arena.getSheepTimer() == 0) {
                        arena.setSheepTimer(plugin.getConfig().getInt("sheep-time"));

                        spawnSheep(arena);
                    }

                    Scoreboard scoreboard = new Scoreboards().getGameScoreboard(arena);

                    for (Player p : arena.getPlayers().keySet()) {
                        p.setScoreboard(scoreboard);
                    }


                    if (timePoints.contains(arena.getTimer())) {
                        for (Player p : arena.getPlayers().keySet()) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(plugin.getMessage("game.time-left").replace("%time%", Integer.toString(arena.getTimer()))));
                            p.playSound(p.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.ending-countdown")).get().parseSound(), 100, 1);
                        }

                    }

                    arena.setTimer(arena.getTimer() - 1);
                    arena.setSheepTimer(arena.getSheepTimer() - 1);
                } else {
                    new End().gameEnd(arena);
                    this.cancel();
                }
            }

        }.runTaskTimer(plugin, 0, 20);
    }

    public void spawnSheep(Arena arena) {
        String arenaString = Utils.getArenaString(arena);

        Location loc = (Location) plugin.getArenasConfig().get("Arenas." + arenaString + ".sheep-spawn");
        Sheep sheep = loc.getWorld().spawn(loc, Sheep.class);
        sheep.setColor(DyeColor.WHITE);
        sheep.setInvulnerable(true);

        Utils.playSound(arena, loc, XSound.matchXSound(plugin.getConfig().getString("sounds.sheep-spawn")).get().parseSound(), 1, 1);
        loc.getWorld().spawnParticle(Particle.valueOf(plugin.getConfig().getString("particles.sheep-spawn")), loc.getX(), loc.getY() + 1, loc.getZ(), 50, 1, 1, 1, 0.1);

        new PathFinding().walkToLocation(sheep, loc, plugin.getConfig().getDouble("sheep-speed"), arena, Team.NONE);
    }

}
