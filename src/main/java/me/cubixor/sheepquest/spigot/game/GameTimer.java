package me.cubixor.sheepquest.spigot.game;

import com.cryptomorin.xseries.XSound;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
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
                LocalArena localArena = plugin.getLocalArenas().get(arenaString);

                if (localArena == null) {
                    this.cancel();
                    return;
                }
                if (!localArena.getState().equals(GameState.GAME)) {
                    this.cancel();
                    return;
                }

                if (localArena.getTimer() > 0) {
                    if (localArena.getSheepTimer() == 0) {
                        localArena.setSheepTimer(plugin.getConfig().getInt("sheep-time"));

                        spawnSheep(localArena);
                    }

                    Scoreboard scoreboard = new Scoreboards().getGameScoreboard(localArena);

                    for (Player p : localArena.getPlayerTeam().keySet()) {
                        p.setScoreboard(scoreboard);
                    }


                    if (timePoints.contains(localArena.getTimer())) {
                        for (Player p : localArena.getPlayerTeam().keySet()) {
                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(plugin.getMessage("game.time-left").replace("%time%", Integer.toString(localArena.getTimer()))));
                            p.playSound(p.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.ending-countdown")).get().parseSound(), 100, 1);
                        }

                    }

                    localArena.setTimer(localArena.getTimer() - 1);
                    localArena.setSheepTimer(localArena.getSheepTimer() - 1);
                } else {
                    new End().gameEnd(localArena);
                    this.cancel();
                }
            }

        }.runTaskTimer(plugin, 0, 20);
    }

    public void spawnSheep(LocalArena localArena) {
        String arenaString = localArena.getName();

        Location loc = ConfigUtils.getLocation(arenaString, ConfigField.SHEEP_SPAWN);
        Sheep sheep = loc.getWorld().spawn(loc, Sheep.class);
        sheep.setColor(DyeColor.WHITE);
        sheep.setInvulnerable(true);

        Utils.playSound(localArena, loc, XSound.matchXSound(plugin.getConfig().getString("sounds.sheep-spawn")).get().parseSound(), 1, 1);
        loc.getWorld().spawnParticle(Particle.valueOf(plugin.getConfig().getString("particles.sheep-spawn")), loc.getX(), loc.getY() + 1, loc.getZ(), 50, 1, 1, 1, 0.1);

        PathFinding.walkToLocation(sheep, loc, plugin.getConfig().getDouble("sheep-speed"), localArena, Team.NONE);
    }

}
