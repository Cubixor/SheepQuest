package me.cubixor.sheepquest.game;

import me.cubixor.sheepquest.*;
import me.cubixor.sheepquest.commands.PlayCommands;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class End {

    private final SheepQuest plugin;

    public End(SheepQuest s) {
        plugin = s;
    }

    private Team winnerTeam;

    public void gameEnd(Arena arena) {
        Utils utils = new Utils(plugin);

        arena.state = GameState.ENDING;
        arena.timer = plugin.getConfig().getInt("ending-time");

        HashMap<Team, Integer> winner = new HashMap<>();
        for (Team team : arena.points.keySet()) {
            if (arena.playerTeam.containsValue(team)) {
                winner.put(team, arena.points.get(team));
            }
        }
        LinkedHashMap<Team, Integer> winnerSorted = utils.sortTeams(winner);
        winnerTeam = (new ArrayList<>(winnerSorted.keySet())).get(winnerSorted.size() - 1);
        String winnerTeamString = utils.getTeamString(winnerTeam);

        int stay = 20 * plugin.getConfig().getInt("ending-time");

        for (Player p : arena.playerTeam.keySet()) {
            utils.removeSheep(p);

            p.sendTitle(plugin.getMessage("game." + winnerTeamString + "-win-title"), plugin.getMessage("game." + winnerTeamString + "-win-subtitle"));


            plugin.getStats().set("Players." + p.getName() + ".games-played", plugin.getStats().getInt("Players." + p.getName() + ".games-played") + 1);
            if (arena.playerTeam.get(p).equals(winnerTeam)) {
                plugin.getStats().set("Players." + p.getName() + ".wins", plugin.getStats().getInt("Players." + p.getName() + ".wins") + 1);
            } else {
                plugin.getStats().set("Players." + p.getName() + ".looses", plugin.getStats().getInt("Players." + p.getName() + ".looses") + 1);
            }
            plugin.getStats().set("Players." + p.getName() + ".sheep-taken", plugin.getStats().getInt("Players." + p.getName() + ".sheep-taken") + arena.playerStats.get(p).sheepTaken);
            plugin.getStats().set("Players." + p.getName() + ".deaths", plugin.getStats().getInt("Players." + p.getName() + ".deaths") + arena.playerStats.get(p).deaths);
            plugin.getStats().set("Players." + p.getName() + ".kills", plugin.getStats().getInt("Players." + p.getName() + ".kills") + arena.playerStats.get(p).kills);


            if (plugin.getConfig().getBoolean("win-rewards")) {
                String place = null;
                if (arena.playerTeam.get(p).equals((new ArrayList<>(winnerSorted.keySet())).get(winnerSorted.size() - 1))) {
                    place = "1";
                } else if (winnerSorted.size() - 2 > 0 && arena.playerTeam.get(p).equals((new ArrayList<>(winnerSorted.keySet())).get(winnerSorted.size() - 2))) {
                    place = "2";
                } else if (winnerSorted.size() - 3 > 0 && arena.playerTeam.get(p).equals((new ArrayList<>(winnerSorted.keySet())).get(winnerSorted.size() - 3))) {
                    place = "3";
                } else if (winnerSorted.size() - 4 > 0 && arena.playerTeam.get(p).equals((new ArrayList<>(winnerSorted.keySet())).get(winnerSorted.size() - 4))) {
                    place = "4";
                }
                if (!plugin.getConfig().getStringList("rewards." + place).isEmpty()) {
                    for (String s : plugin.getConfig().getStringList("rewards." + place)) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", p.getName()));
                    }
                }
            }
        }

        plugin.savePlayers();
        new Signs(plugin).updateSigns(arena);

        ending(utils.getArenaString(arena));
    }

    private void ending(String arenaString) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Utils utils = new Utils(plugin);
                Arena arena = plugin.arenas.get(arenaString);
                if (arena == null) {
                    this.cancel();
                    return;
                }
                if (!arena.state.equals(GameState.ENDING)) {
                    this.cancel();
                    return;
                }

                if (arena.timer > 0) {
                    Scoreboard scoreboard = new Scoreboards(plugin).getEndingScoreboard(arena);
                    for (Player p : arena.playerTeam.keySet()) {
                        p.setScoreboard(scoreboard);
                        if (arena.playerTeam.get(p).equals(winnerTeam)) {
                            Firework firework = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
                            FireworkMeta fwm = firework.getFireworkMeta();
                            FireworkEffect.Type type = FireworkEffect.Type.BALL;
                            Color color = utils.getColor(winnerTeam);
                            FireworkEffect fwe = FireworkEffect.builder().flicker(true).withColor(color).with(type).withTrail().build();
                            fwm.addEffect(fwe);
                            fwm.setPower(1);
                            firework.setFireworkMeta(fwm);
                        }
                    }

                    arena.timer--;
                } else {
                    resetArena(arena);

                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void resetArena(Arena arena) {
        Utils utils = new Utils(plugin);
        PlayCommands playCommands = new PlayCommands(plugin);
        String arenaString = utils.getArenaString(arena);
        plugin.arenas.replace(utils.getArenaString(arena), new Arena(plugin));


        for (Sheep s : arena.sheep.keySet()) {
            arena.sheep.get(s).cancel();
            s.remove();
        }


        for (Player p : arena.playerTeam.keySet()) {
            playCommands.kickPlayer(p, arenaString);
            utils.removeBossBars(p, arena);
            if (arena.playerStats.get(p) != null && arena.playerStats.get(p).sheepCooldown != null) {
                arena.playerStats.get(p).sheepCooldown.cancel();
            }
            if (plugin.getConfig().getBoolean("auto-join-on-end")) {
                playCommands.putInRandomArena(p);
            }
        }


        new Signs(plugin).loadArenaSigns(arenaString);
        new Signs(plugin).updateSigns(plugin.arenas.get(arenaString));
    }
}
