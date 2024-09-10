package me.cubixor.sheepquest.game;

import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.sheepquest.arena.Team;
import me.cubixor.sheepquest.game.kits.KitType;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.Map;

public class BossBarManager {

    private final Map<Team, BossBar> teamBossBars = new EnumMap<>(Team.class);
    private final Map<KitType, BossBar> kitBossBars = new EnumMap<>(KitType.class);

    public BossBarManager() {
        for (Team team : Team.values()) {
            BossBar bossBar = Bukkit.createBossBar(Messages.get("game.bossbar-team", "%team%", team.getName()), team.getBarColor(), BarStyle.SOLID);
            teamBossBars.put(team, bossBar);
        }

        for (KitType kitType : KitType.values()) {
            BossBar bossBar = Bukkit.createBossBar(Messages.get("kits.bossbar-kit", "%kit%", kitType.getName()), BarColor.WHITE, BarStyle.SOLID);
            bossBar.setProgress(0);
            kitBossBars.put(kitType, bossBar);
        }
    }

    public void addTeamBossBar(Player player, Team team) {
        removeTeamBossBar(player);
        teamBossBars.get(team).addPlayer(player);
    }

    public void removeTeamBossBar(Player player) {
        for (BossBar bossBar : teamBossBars.values()) {
            if (bossBar.getPlayers().contains(player)) {
                bossBar.removePlayer(player);
            }
        }
    }

    public void addKitBossBar(Player player, KitType kitType) {
        removeKitBossBar(player);
        kitBossBars.get(kitType).addPlayer(player);
    }

    public void removeKitBossBar(Player player) {
        for (BossBar bossBar : kitBossBars.values()) {
            if (bossBar.getPlayers().contains(player)) {
                bossBar.removePlayer(player);
            }
        }
    }
}
