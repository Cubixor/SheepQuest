package me.cubixor.sheepquest.spigot.gameInfo;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.game.Teams;
import me.cubixor.sheepquest.spigot.game.events.SpecialEventsData;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

import java.io.Serializable;
import java.util.HashMap;

public class LocalArena extends Arena implements Serializable {

    private HashMap<Player, Team> playerTeam = new HashMap<>();
    private int timer = -1;
    private int sheepTimer;
    private Inventory teamChooseInv;
    private HashMap<Team, Integer> points = new HashMap<>();
    private HashMap<Sheep, BukkitTask> sheep = new HashMap<>();
    private HashMap<Player, Integer> respawnTimer = new HashMap<>();
    private HashMap<Player, PlayerGameStats> playerStats = new HashMap<>();
    private HashMap<Team, BossBar> teamBossBars = new HashMap<>();
    private SpecialEventsData specialEventsData;
    private HashMap<Player, PlayerData> playerData = new HashMap<>();

    public LocalArena(String name) {
        super(name, SheepQuest.getInstance().getServerName());

        SheepQuest plugin = SheepQuest.getInstance();

        setTeamChooseInv(Bukkit.createInventory(null, 9, plugin.getMessage("game.team-menu-name")));
        new Teams().loadBossBars(this);
    }

    @Override
    public String toString() {
        return "LocalArena{" +
                "playerTeam=" + playerTeam +
                ", timer=" + timer +
                ", sheepTimer=" + sheepTimer +
                ", teamChooseInv=" + teamChooseInv +
                ", points=" + points +
                ", sheep=" + sheep +
                ", respawnTimer=" + respawnTimer +
                ", playerStats=" + playerStats +
                ", teamBossBars=" + teamBossBars +
                ", specialEventsData=" + specialEventsData +
                ", playerData=" + playerData +
                '}';
    }

    public HashMap<Player, Team> getPlayerTeam() {
        return playerTeam;
    }

    public void setPlayerTeam(HashMap<Player, Team> playerTeam) {
        this.playerTeam = playerTeam;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
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

    public HashMap<Team, Integer> getPoints() {
        return points;
    }

    public void setPoints(HashMap<Team, Integer> points) {
        this.points = points;
    }

    public HashMap<Sheep, BukkitTask> getSheep() {
        return sheep;
    }

    public void setSheep(HashMap<Sheep, BukkitTask> sheep) {
        this.sheep = sheep;
    }

    public HashMap<Player, Integer> getRespawnTimer() {
        return respawnTimer;
    }

    public void setRespawnTimer(HashMap<Player, Integer> respawnTimer) {
        this.respawnTimer = respawnTimer;
    }


    public HashMap<Player, PlayerGameStats> getPlayerStats() {
        return playerStats;
    }

    public void setPlayerStats(HashMap<Player, PlayerGameStats> playerStats) {
        this.playerStats = playerStats;
    }

    public HashMap<Team, BossBar> getTeamBossBars() {
        return teamBossBars;
    }

    public void setTeamBossBars(HashMap<Team, BossBar> teamBossBars) {
        this.teamBossBars = teamBossBars;
    }

    public SpecialEventsData getSpecialEventsData() {
        return specialEventsData;
    }

    public void setSpecialEventsData(SpecialEventsData specialEventsData) {
        this.specialEventsData = specialEventsData;
    }

    public HashMap<Player, PlayerData> getPlayerData() {
        return playerData;
    }

    public void setPlayerData(HashMap<Player, PlayerData> playerData) {
        this.playerData = playerData;
    }
}
