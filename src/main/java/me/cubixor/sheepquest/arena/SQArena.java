package me.cubixor.sheepquest.arena;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.game.arena.GameState;
import me.cubixor.minigamesapi.spigot.game.arena.LocalArena;
import me.cubixor.sheepquest.game.kits.KitType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class SQArena extends LocalArena {

    private final Map<Player, Team> playerTeam = new HashMap<>();
    private final Map<Player, KitType> playerKit = new HashMap<>();
    private final Map<Team, Integer> points = new EnumMap<>(Team.class);
    private final Map<Entity, BukkitTask> sheep = new HashMap<>();
    private final Map<Player, Integer> respawnTimer = new HashMap<>();
    private final Map<Team, TeamRegion> teamRegions;
    private final Map<Player, PlayerGameStats> playerGameStats = new HashMap<>();
    private int sheepTimer = 1;
    //private SpecialEventsData specialEventsData;


    public SQArena(String name, String server) {
        super(name, server);
        this.teamRegions = new EnumMap<>(Team.class);
    }

    public SQArena(String name, String server, boolean active, boolean vip, int minPlayers, int maxPlayers, Map<Team, TeamRegion> teamRegions) {
        super(name, server, active, vip, minPlayers, maxPlayers);
        this.teamRegions = teamRegions;
    }

    public void resetArena() {
        playerTeam.clear();
        playerKit.clear();
        points.clear();
        sheep.clear();
        respawnTimer.clear();
        playerGameStats.clear();
        sheepTimer = 1;
    }

    public Map<Player, Team> getPlayerTeam() {
        return playerTeam;
    }

    public int getSheepTimer() {
        return sheepTimer;
    }

    public void setSheepTimer(int sheepTimer) {
        this.sheepTimer = sheepTimer;
    }

    public Map<Team, Integer> getPoints() {
        return points;
    }

    public Map<Entity, BukkitTask> getSheep() {
        return sheep;
    }

    public Map<Player, Integer> getRespawnTimer() {
        return respawnTimer;
    }

    public Map<Team, TeamRegion> getTeamRegions() {
        return teamRegions;
    }

    public Map<Player, KitType> getPlayerKit() {
        return playerKit;
    }

    public Map<Player, PlayerGameStats> getPlayerGameStats() {
        return playerGameStats;
    }

    public List<Team> getTeams() {
        return new ArrayList<>(getTeamRegions().keySet());
    }

    public Map<Team, Integer> countTeamPlayers() {
        return getPlayerTeam().values().stream()
                .collect(Collectors.groupingBy(
                        t -> t, // Group by team
                        Collectors.summingInt(t -> 1) // Count the number of players in each team
                ));
    }

    public List<Player> getTeamPlayers(Team team) {
        return getPlayerTeam()
                .entrySet()
                .stream()
                .filter(e -> e.getValue().equals(team))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public boolean isInRegion(Entity entity, Team team) {
        return getTeamRegions().get(team).isInRegion(entity);
    }


    public int getTimeLeft() {
        if (!getState().equals(GameState.GAME)) return getTimer();
        return MinigamesAPI.getPlugin().getConfig().getInt("game-time") - getTimer();
    }
}
