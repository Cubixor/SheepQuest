package me.cubixor.sheepquest.arena;

import me.cubixor.minigamesapi.spigot.game.arena.LocalArena;
import me.cubixor.sheepquest.game.kits.KitType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.stream.Collectors;

public class SQArena extends LocalArena {

    private final Map<Player, Team> playerTeam = new HashMap<>();
    private final Map<Player, KitType> playerKit = new HashMap<>();
    private final Map<Team, Integer> points = new HashMap<>();
    private final Map<Entity, BukkitTask> sheep = new HashMap<>();
    private final Map<Player, Integer> respawnTimer = new HashMap<>();
    private final Map<Team, TeamRegion> teamRegions;
    private int sheepTimer;
    private Inventory teamChooseInv;
    //private SpecialEventsData specialEventsData;


    public SQArena(String name, String server) {
        super(name, server);
        this.teamRegions = new EnumMap<>(Team.class);
    }

    public SQArena(String name, String server, boolean active, boolean vip, int minPlayers, int maxPlayers, Map<Team, TeamRegion> teamRegions) {
        super(name, server, active, vip, minPlayers, maxPlayers);
        this.teamRegions = teamRegions;
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

    public Map<Player, KitType> getPlayerKit() {
        return playerKit;
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

    public boolean isInRegion(Entity entity, Team team) {
        return getTeamRegions().get(team).isInRegion(entity);
    }

    public boolean isInSheepSpawn(Entity entity) {
        return entity.getLocation().distance(getTeamRegions().get(Team.NONE).getLoc()) < 10;
    }
}
