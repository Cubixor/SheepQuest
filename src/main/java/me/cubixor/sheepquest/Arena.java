package me.cubixor.sheepquest;

import me.cubixor.sheepquest.game.Signs;
import me.cubixor.sheepquest.game.Teams;
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

    public GameState state = GameState.WAITING;
    public HashMap<Player, Team> playerTeam = new HashMap<>();
    public int timer = -1;
    public int sheepTimer;
    public Inventory teamInventory;
    public HashMap<Team, Integer> points = new HashMap<>();
    public HashMap<Sheep, BukkitTask> sheep = new HashMap<>();
    public HashMap<Player, Integer> respawnTimer = new HashMap<>();
    public List<Sign> signs = new ArrayList<>();
    public HashMap<Player, PlayerGameStats> playerStats = new HashMap<>();
    public HashMap<Team, BossBar> teamBossBars = new HashMap<>();

    public Arena(SheepQuest plugin) {
        teamInventory = Bukkit.createInventory(null, 9, plugin.getMessage("game.team-menu-name"));
        new Teams(plugin).loadBossBars(this);
        new Signs(plugin).loadArenaSigns(new Utils(plugin).getArenaString(this));
    }
}
