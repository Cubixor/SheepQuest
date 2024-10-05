package me.cubixor.sheepquest.game;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.sheepquest.arena.Team;
import me.cubixor.sheepquest.game.kits.KitType;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.EnumMap;
import java.util.Map;

public class BossBarManager {

    private final boolean teamBossBarEnabled;
    private final boolean kitBossBarEnabled;
    private final Map<Team, BossBar> teamBossBars = new EnumMap<>(Team.class);
    private final Map<KitType, BossBar> kitBossBars = new EnumMap<>(KitType.class);

    public BossBarManager() {
        FileConfiguration config = MinigamesAPI.getPlugin().getConfig();
        teamBossBarEnabled = config.getBoolean("team-bossbar");
        kitBossBarEnabled = config.getBoolean("kit-bossbar");

        if (teamBossBarEnabled) {
            for (Team team : Team.values()) {
                BossBar bossBar = Bukkit.createBossBar(Messages.get("game.bossbar-team", "%team%", team.getName()), team.getBarColor(), BarStyle.SOLID);
                teamBossBars.put(team, bossBar);
            }
        }

        if (kitBossBarEnabled) {
            for (KitType kitType : KitType.values()) {
                BossBar bossBar = Bukkit.createBossBar(Messages.get("kits.bossbar-kit", "%kit%", kitType.getName()), BarColor.WHITE, BarStyle.SOLID);
                bossBar.setProgress(0);
                kitBossBars.put(kitType, bossBar);
            }
        }
    }

    public void addTeamBossBar(Player player, Team team) {
        if (!teamBossBarEnabled) return;

        removeTeamBossBar(player);
        teamBossBars.get(team).addPlayer(player);
    }

    public void removeTeamBossBar(Player player) {
        if (!teamBossBarEnabled) return;

        for (BossBar bossBar : teamBossBars.values()) {
            if (bossBar.getPlayers().contains(player)) {
                bossBar.removePlayer(player);
            }
        }
    }

    public void addKitBossBar(Player player, KitType kitType) {
        if (!kitBossBarEnabled) return;

        removeKitBossBar(player);
        kitBossBars.get(kitType).addPlayer(player);
    }

    public void removeKitBossBar(Player player) {
        if (!kitBossBarEnabled) return;

        for (BossBar bossBar : kitBossBars.values()) {
            if (bossBar.getPlayers().contains(player)) {
                bossBar.removePlayer(player);
            }
        }
    }
}
