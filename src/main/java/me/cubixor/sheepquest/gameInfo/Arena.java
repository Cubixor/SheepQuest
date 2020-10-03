package me.cubixor.sheepquest.gameInfo;

import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.game.Signs;
import me.cubixor.sheepquest.game.Teams;
import me.cubixor.sheepquest.game.events.SpecialEventsData;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Arena {

    private GameState state = GameState.WAITING;
    private HashMap<Player, Team> players = new HashMap<>();
    private int timer = -1;
    private int sheepTimer;
    private Inventory teamChooseInv;
    private HashMap<Team, Integer> points = new HashMap<>();
    private HashMap<Sheep, BukkitTask> sheep = new HashMap<>();
    private HashMap<Player, Integer> respawnTimer = new HashMap<>();
    private List<Sign> signs = new ArrayList<>();
    private HashMap<Player, PlayerGameStats> playerStats = new HashMap<>();
    private HashMap<Team, BossBar> teamBossBars = new HashMap<>();
    private SpecialEventsData specialEventsData;

    public Arena() {
        SheepQuest plugin = SheepQuest.getInstance();
        setTeamChooseInv(Bukkit.createInventory(null, 9, plugin.getMessage("game.team-menu-name")));
        new Teams().loadBossBars(this);
        new Signs().loadArenaSigns(Utils.getArenaString(this));
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public HashMap<Player, Team> getPlayers() {
        return players;
    }

    public void setPlayers(HashMap<Player, Team> players) {
        this.players = players;
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

    public List<Sign> getSigns() {
        return signs;
    }

    public void setSigns(List<Sign> signs) {
        this.signs = signs;
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
}
