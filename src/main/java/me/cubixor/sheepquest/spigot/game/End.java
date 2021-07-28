package me.cubixor.sheepquest.spigot.game;

import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.api.Particles;
import me.cubixor.sheepquest.spigot.api.Sounds;
import me.cubixor.sheepquest.spigot.commands.PlayCommands;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.config.StatsField;
import me.cubixor.sheepquest.spigot.config.StatsUtils;
import me.cubixor.sheepquest.spigot.game.events.SpecialEvents;
import me.cubixor.sheepquest.spigot.gameInfo.*;
import me.cubixor.sheepquest.spigot.socket.SocketClientSender;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class End {

    private final SheepQuest plugin;
    private Team winnerTeam;

    public End() {
        plugin = SheepQuest.getInstance();
    }

    public void gameEnd(LocalArena localArena) {

        localArena.setState(GameState.ENDING);
        localArena.setTimer(plugin.getConfig().getInt("ending-time"));

        LinkedHashMap<Team, Integer> winner = new LinkedHashMap<>();
        for (Team team : localArena.getPoints().keySet()) {
            if (localArena.getPlayerTeam().containsValue(team)) {
                winner.put(team, localArena.getPoints().get(team));
            }
        }
        boolean noWin = checkNoWin(winner);

        LinkedHashMap<Team, Integer> winnerSorted = new LinkedHashMap<>();
        winner.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> winnerSorted.put(x.getKey(), x.getValue()));
        List<Team> teamsSorted = new ArrayList<>(winnerSorted.keySet());

        if (noWin) {
            winnerTeam = Team.NONE;
        } else {
            winnerTeam = (new ArrayList<>(winnerSorted.keySet())).get(0);
        }
        String winnerTeamString = winnerTeam.getCode();

        int stay = 20 * plugin.getConfig().getInt("ending-time");

        StringBuilder teamPoints = new StringBuilder();
        for (Team team : ConfigUtils.getTeamList(localArena.getName())) {
            teamPoints.append(plugin.getMessage("game.summary-points")
                    .replace("%team%", team.getName())
                    .replace("%points%", Integer.toString(localArena.getPoints().get(team))));
        }

        for (Player p : localArena.getPlayerTeam().keySet()) {
            if (localArena.getRespawnTimer().containsKey(p)) {
                new Kill().respawn(localArena, p);
            }
            ActionBar.clearActionBar(p);

            Utils.removeSheep(p);

            String playerName = p.getName();
            PlayerGameStats playerStats = localArena.getPlayerStats().get(p);
            StatsUtils.addStats(playerName, StatsField.GAMES_PLAYED, 1);
            if (localArena.getPlayerTeam().get(p).equals(winnerTeam)) {
                StatsUtils.addStats(playerName, StatsField.WINS, 1);
            } else {
                StatsUtils.addStats(playerName, StatsField.LOOSES, 1);
            }
            StatsUtils.addStats(playerName, StatsField.KILLS, playerStats.getKills());
            StatsUtils.addStats(playerName, StatsField.DEATHS, playerStats.getDeaths());
            StatsUtils.addStats(playerName, StatsField.SHEEP_TAKEN, playerStats.getSheepTaken());
            StatsUtils.addStats(playerName, StatsField.BONUS_SHEEP_TAKEN, playerStats.getBonusSheepTaken());

            boolean won = !noWin && localArena.getPlayerTeam().get(p).equals(winnerTeam);

            if (won) {
                Sounds.playSound(p, p.getLocation(), "win");
            } else {
                Sounds.playSound(p, p.getLocation(), "defeat");
            }

            Titles.sendTitle(p, 10, stay - 20, 20, plugin.getMessage("game." + (won ? "win" : "defeat") + "-title"),
                    plugin.getMessage("game." + (noWin ? "no-win" : "win-defeat") + "-subtitle")
                            .replace("%team%", plugin.getMessage("general.team-" + winnerTeamString)));


            for (String s : plugin.getMessageList("game.summary")) {
                s = s.replace("%teams%", teamPoints.toString())
                        .replace("%kills%", Integer.toString(playerStats.getKills()))
                        .replace("%deaths%", Integer.toString(playerStats.getDeaths()))
                        .replace("%sheep%", Integer.toString(playerStats.getSheepTaken()));
                p.sendMessage(s);
            }

            Team playerTeam = localArena.getPlayerTeam().get(p);

            if (plugin.getConfig().getBoolean("win-rewards") && !noWin) {
                String place = Integer.toString(teamsSorted.indexOf(playerTeam) + 1);
                if (!plugin.getConfig().getStringList("rewards." + place).isEmpty()) {
                    for (String s : plugin.getConfig().getStringList("rewards." + place)) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", p.getName()));
                    }
                }
            }
        }

        plugin.savePlayers();
        new Signs().updateSigns(localArena.getName());
        if (plugin.isBungee()) {
            Arena arena = new Arena(localArena.getName(), localArena.getServer(), localArena.getState(), localArena.getPlayers());
            new SocketClientSender().sendUpdateArenaPacket(arena);
        }

        ending(localArena.getName());
    }

    private void ending(String arenaString) {
        new BukkitRunnable() {
            @Override
            public void run() {
                LocalArena localArena = plugin.getLocalArenas().get(arenaString);
                if (localArena == null) {
                    this.cancel();
                    return;
                }
                if (!localArena.getState().equals(GameState.ENDING)) {
                    this.cancel();
                    return;
                }

                if (localArena.getTimer() > 0) {
                    for (Player p : localArena.getPlayerTeam().keySet()) {
                        p.setScoreboard(new Scoreboards().getEndingScoreboard(localArena, p));
                        if (localArena.getPlayerTeam().get(p).equals(winnerTeam)) {
                            Firework firework = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
                            FireworkMeta fwm = firework.getFireworkMeta();
                            FireworkEffect.Type type = FireworkEffect.Type.BALL;
                            Color color = winnerTeam.getColor();
                            FireworkEffect fwe = FireworkEffect.builder().flicker(true).withColor(color).with(type).withTrail().build();
                            fwm.addEffect(fwe);
                            fwm.setPower(1);
                            firework.setFireworkMeta(fwm);

                            Particles.spawnParticle(localArena, p.getLocation().add(0, 3, 0), "win");
                        }
                    }

                    localArena.setTimer(localArena.getTimer() - 1);
                } else {
                    resetArena(localArena, true);

                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void resetArena(LocalArena localArena, boolean end) {
        PlayCommands playCommands = new PlayCommands();
        String arenaString = localArena.getName();
        plugin.getLocalArenas().replace(arenaString, new LocalArena(arenaString));


        for (Sheep s : localArena.getSheep().keySet()) {
            localArena.getSheep().get(s).cancel();
            s.remove();
        }
        new SpecialEvents().reset(localArena);

        List<Player> players = new ArrayList<>(localArena.getPlayerTeam().keySet());
        for (Player p : players) {
            if (!end) {
                playCommands.kickFromLocalArena(p, localArena, true, false);
            } else {
                playCommands.kickFromLocalArenaSynchronized(p, localArena, true, true);
            }
            Utils.removeTeamBossBars(p, localArena);
            if (localArena.getPlayerStats().get(p) != null && localArena.getPlayerStats().get(p).getSheepCooldown() != null) {
                localArena.getPlayerStats().get(p).getSheepCooldown().cancel();
            }
        }

        new Signs().updateSigns(arenaString);

        if (plugin.isBungee()) {
            LocalArena newLocalArena = plugin.getLocalArenas().get(localArena.getName());
            Arena arena = new Arena(newLocalArena.getName(), newLocalArena.getServer(), newLocalArena.getState(), newLocalArena.getPlayers());
            new SocketClientSender().sendUpdateArenaPacket(arena);
        }
        if (end && plugin.getConfig().getBoolean("auto-join-on-end")) {
            for (Player p : players) {
                playCommands.putInRandomArena(p, false);
            }
        }
    }

    private boolean checkNoWin(LinkedHashMap<Team, Integer> winner) {
        if (winner.size() == 1) {
            return winner.values().toArray()[0].equals(0);
        } else {
            for (Team team : winner.keySet()) {
                if (!winner.get(team).equals(winner.values().toArray()[0])) {
                    return false;
                }
            }
            return true;
        }
    }
}
