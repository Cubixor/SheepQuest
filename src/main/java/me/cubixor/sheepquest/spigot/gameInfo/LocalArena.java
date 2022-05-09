package me.cubixor.sheepquest.spigot.gameInfo;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.BossBar;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.game.Scoreboards;
import me.cubixor.sheepquest.spigot.game.events.SpecialEventsData;
import me.cubixor.sheepquest.spigot.game.kits.KitType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

import java.io.Serializable;
import java.util.HashMap;

public class LocalArena extends Arena implements Serializable {

    private int timer = -1;
    private int sheepTimer;
    private Inventory teamChooseInv;
    private SpecialEventsData specialEventsData;
    private final HashMap<Player, Team> playerTeam = new HashMap<>();
    private final HashMap<Player, KitType> playerKit = new HashMap<>();
    private final HashMap<Team, Integer> points = new HashMap<>();
    private final HashMap<Entity, BukkitTask> sheep = new HashMap<>();
    private final HashMap<Player, Integer> respawnTimer = new HashMap<>();
    private final HashMap<Player, PlayerGameStats> playerStats = new HashMap<>();
    private final HashMap<Team, BossBar> teamBossBars = new HashMap<>();
    private final HashMap<Player, PlayerData> playerData = new HashMap<>();
    private final HashMap<Team, TeamRegion> teamRegions = new HashMap<>();

    private final HashMap<Player, Scoreboard> playerScoreboards = new HashMap<>();

    public LocalArena(String name) {
        super(name, SheepQuest.getInstance().getServerName());

        SheepQuest plugin = SheepQuest.getInstance();

        int invSlots;
        if (ConfigUtils.getTeamList(name).size() > 9) {
            invSlots = 18;
        } else {
            invSlots = 9;
        }
        setTeamChooseInv(Bukkit.createInventory(null, invSlots, plugin.getMessage("game.team-menu-name")));

        for (Team team : ConfigUtils.getTeamList(name)) {
            getTeamBossBars().put(team, new BossBar(plugin.getMessage("game.bossbar-team").replace("%team%", team.getName()), team));
            getTeamRegions().put(team, new TeamRegion(name, team));
        }
        getTeamBossBars().put(Team.NONE, new BossBar(plugin.getMessage("game.bossbar-team").replace("%team%", Team.NONE.getName()), Team.NONE));
        getTeamRegions().put(Team.NONE, new TeamRegion(name, Team.NONE));

    }

    public HashMap<Player, Team> getPlayerTeam() {
        return playerTeam;
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

    public HashMap<Entity, BukkitTask> getSheep() {
        return sheep;
    }

    public HashMap<Player, Integer> getRespawnTimer() {
        return respawnTimer;
    }

    public HashMap<Player, PlayerGameStats> getPlayerStats() {
        return playerStats;
    }

    public HashMap<Team, BossBar> getTeamBossBars() {
        return teamBossBars;
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

    public HashMap<Player, KitType> getPlayerKit() {
        return playerKit;
    }

    public HashMap<Team, TeamRegion> getTeamRegions() {
        return teamRegions;
    }

    public HashMap<Player, Scoreboard> getPlayerScoreboards() {
        return playerScoreboards;
    }

    public void setState(GameState state) {
        setGameState(state);
        new Scoreboards().changeScoreboardSize(this);
    }
}
