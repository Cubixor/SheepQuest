package me.cubixor.sheepquest.game;

import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.sheepquest.arena.Team;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.Map;

public class BossBarManager {

    private final Map<Team, BossBar> teamBossBars = new EnumMap<>(Team.class);

    public BossBarManager() {
        for (Team team : Team.values()) {
            BossBar bossBar = Bukkit.createBossBar(Messages.get("game.bossbar-team", "%team%", team.getName()), team.getBarColor(), BarStyle.SOLID);
            teamBossBars.put(team, bossBar);
        }
    }

    public void addPlayer(Player player, Team team) {
        removePlayer(player);
        teamBossBars.get(team).addPlayer(player);
    }

    public void removePlayer(Player player) {
        for (BossBar bossBar : teamBossBars.values()) {
            if (bossBar.getPlayers().contains(player)) {
                bossBar.removePlayer(player);
            }
        }
    }
}
