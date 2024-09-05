package me.cubixor.sheepquest.arena;

import me.cubixor.minigamesapi.spigot.game.arena.LocalArena;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SQArena extends LocalArena {

    private final HashMap<Player, Team> playerTeam = new HashMap<>();
    //private final HashMap<Player, KitType> playerKit = new HashMap<>();
    private final HashMap<Team, Integer> points = new HashMap<>();
    private final HashMap<Entity, BukkitTask> sheep = new HashMap<>();
    private final HashMap<Player, Integer> respawnTimer = new HashMap<>();
    private final HashMap<Team, TeamRegion> teamRegions = new HashMap<>();
    private int teamsCount;
    private int sheepTimer;
    private Inventory teamChooseInv;
    //private SpecialEventsData specialEventsData;


    public SQArena(String name, String server) {
        super(name, server);
    }

    public SQArena(String name, String server, boolean active, boolean vip, int minPlayers, int maxPlayers, List<Team> teams) {
        super(name, server, active, vip, minPlayers, maxPlayers);

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

    public Inventory getTeamChooseInv() {
        return teamChooseInv;
    }

    public void setTeamChooseInv(Inventory teamChooseInv) {
        this.teamChooseInv = teamChooseInv;
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

    public List<Team> getTeams() {
        return new ArrayList<>(getTeamRegions().keySet());
    }

    public Map<Team, Integer> getTeamPlayers() {
        return getPlayerTeam().values().stream()
                .collect(Collectors.groupingBy(
                        t -> t, // Group by team
                        Collectors.summingInt(t -> 1) // Count the number of players in each team
                ));
    }
}
