package me.cubixor.sheepquest.game;

import com.cryptomorin.xseries.XSound;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.TitleAPI;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.commands.PlayCommands;
import me.cubixor.sheepquest.game.events.SpecialEvents;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.GameState;
import me.cubixor.sheepquest.gameInfo.PlayerGameStats;
import me.cubixor.sheepquest.gameInfo.Team;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Particle;
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
    private Team winnerTeam;

    public End() {
        plugin = SheepQuest.getInstance();
    }

    public void gameEnd(Arena arena) {

        arena.setState(GameState.ENDING);
        arena.setTimer(plugin.getConfig().getInt("ending-time"));

        HashMap<Team, Integer> winner = new HashMap<>();
        for (Team team : arena.getPoints().keySet()) {
            if (arena.getPlayers().containsValue(team)) {
                winner.put(team, arena.getPoints().get(team));
            }
        }
        LinkedHashMap<Team, Integer> winnerSorted = Utils.sortTeams(winner);
        winnerTeam = (new ArrayList<>(winnerSorted.keySet())).get(winnerSorted.size() - 1);
        String winnerTeamString = Utils.getTeamString(winnerTeam);

        int stay = 20 * plugin.getConfig().getInt("ending-time");

        for (Player p : arena.getPlayers().keySet()) {
            if (arena.getRespawnTimer().containsKey(p)) {
                new Kill().respawn(arena, p);
            }

            Utils.removeSheep(p);

            PlayerGameStats playerStats = arena.getPlayerStats().get(p);
            plugin.getStats().set("Players." + p.getName() + ".games-played", plugin.getStats().getInt("Players." + p.getName() + ".games-played") + 1);
            if (arena.getPlayers().get(p).equals(winnerTeam)) {
                plugin.getStats().set("Players." + p.getName() + ".wins", plugin.getStats().getInt("Players." + p.getName() + ".wins") + 1);
            } else {
                plugin.getStats().set("Players." + p.getName() + ".looses", plugin.getStats().getInt("Players." + p.getName() + ".looses") + 1);
            }
            plugin.getStats().set("Players." + p.getName() + ".sheep-taken", plugin.getStats().getInt("Players." + p.getName() + ".sheep-taken") + playerStats.getSheepTaken());
            plugin.getStats().set("Players." + p.getName() + ".bonus-sheep-taken", plugin.getStats().getInt("Players." + p.getName() + ".bonus-sheep-taken") + playerStats.getBonusSheepTaken());
            plugin.getStats().set("Players." + p.getName() + ".deaths", plugin.getStats().getInt("Players." + p.getName() + ".deaths") + playerStats.getDeaths());
            plugin.getStats().set("Players." + p.getName() + ".kills", plugin.getStats().getInt("Players." + p.getName() + ".kills") + playerStats.getKills());

            boolean won = arena.getPlayers().get(p).equals((new ArrayList<>(winnerSorted.keySet())).get(winnerSorted.size() - 1));


            if (won) {
                p.playSound(p.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.win")).get().parseSound(), 100, 1);
            } else {
                p.playSound(p.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.defeat")).get().parseSound(), 100, 1);
            }

            TitleAPI.sendTitle(p, 10, stay - 20, 20,
                    plugin.getMessage("game." + (won ? "win" : "defeat") + "-title"),
                    plugin.getMessage("game.win-defeat-subtitle")
                            .replace("%team%", plugin.getMessage("general.team-" + winnerTeamString)));

            for (String s : plugin.getMessageList("game.summary")) {
                s = s.replace("%red-points%", arena.getPoints().get(Team.RED).toString())
                        .replace("%green-points%", arena.getPoints().get(Team.GREEN).toString())
                        .replace("%blue-points%", arena.getPoints().get(Team.BLUE).toString())
                        .replace("%yellow-points%", arena.getPoints().get(Team.YELLOW).toString())
                        .replace("%kills%", Integer.toString(playerStats.getKills()))
                        .replace("%deaths%", Integer.toString(playerStats.getDeaths()))
                        .replace("%sheep%", Integer.toString(playerStats.getSheepTaken()));
                p.sendMessage(s);
            }

            if (plugin.getConfig().getBoolean("win-rewards")) {
                String place = null;
                if (won) {
                    place = "1";
                } else if (winnerSorted.size() - 2 > 0 && arena.getPlayers().get(p).equals((new ArrayList<>(winnerSorted.keySet())).get(winnerSorted.size() - 2))) {
                    place = "2";
                } else if (winnerSorted.size() - 3 > 0 && arena.getPlayers().get(p).equals((new ArrayList<>(winnerSorted.keySet())).get(winnerSorted.size() - 3))) {
                    place = "3";
                } else if (winnerSorted.size() - 4 > 0 && arena.getPlayers().get(p).equals((new ArrayList<>(winnerSorted.keySet())).get(winnerSorted.size() - 4))) {
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
        new Signs().updateSigns(arena);

        ending(Utils.getArenaString(arena));
    }

    private void ending(String arenaString) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Arena arena = plugin.getArenas().get(arenaString);
                if (arena == null) {
                    this.cancel();
                    return;
                }
                if (!arena.getState().equals(GameState.ENDING)) {
                    this.cancel();
                    return;
                }

                if (arena.getTimer() > 0) {
                    Scoreboard scoreboard = new Scoreboards().getEndingScoreboard(arena);
                    for (Player p : arena.getPlayers().keySet()) {
                        p.setScoreboard(scoreboard);
                        if (arena.getPlayers().get(p).equals(winnerTeam)) {
                            Firework firework = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
                            FireworkMeta fwm = firework.getFireworkMeta();
                            FireworkEffect.Type type = FireworkEffect.Type.BALL;
                            Color color = Utils.getColor(winnerTeam);
                            FireworkEffect fwe = FireworkEffect.builder().flicker(true).withColor(color).with(type).withTrail().build();
                            fwm.addEffect(fwe);
                            fwm.setPower(1);
                            firework.setFireworkMeta(fwm);

                            p.getWorld().spawnParticle(Particle.valueOf(plugin.getConfig().getString("particles.win")), p.getLocation().getX(), p.getLocation().getY() + 3, p.getLocation().getZ(), 30, 0.5, 0.5, 0.5, 0.1);
                        }
                    }

                    arena.setTimer(arena.getTimer() - 1);
                } else {
                    resetArena(arena);

                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void resetArena(Arena arena) {
        PlayCommands playCommands = new PlayCommands();
        String arenaString = Utils.getArenaString(arena);
        plugin.getArenas().replace(Utils.getArenaString(arena), new Arena());


        for (Sheep s : arena.getSheep().keySet()) {
            arena.getSheep().get(s).cancel();
            s.remove();
        }
        new SpecialEvents().reset(arena);


        for (Player p : arena.getPlayers().keySet()) {
            playCommands.kickPlayer(p, arenaString);
            Utils.removeBossBars(p, arena);
            if (arena.getPlayerStats().get(p) != null && arena.getPlayerStats().get(p).getSheepCooldown() != null) {
                arena.getPlayerStats().get(p).getSheepCooldown().cancel();
            }
            if (plugin.getConfig().getBoolean("auto-join-on-end")) {
                playCommands.putInRandomArena(p);
            }
        }


        new Signs().loadArenaSigns(arenaString);
        new Signs().updateSigns(plugin.getArenas().get(arenaString));
    }
}
