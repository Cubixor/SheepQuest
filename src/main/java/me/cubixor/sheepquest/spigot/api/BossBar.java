package me.cubixor.sheepquest.spigot.api;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.inventivetalent.bossbar.BossBarAPI;

public class BossBar {

    public final SheepQuest plugin;
    private final String name;
    private org.bukkit.boss.BossBar bukkitBossBar;

    public BossBar(String name, Team color) {
        plugin = SheepQuest.getInstance();
        this.name = name;

        if (!VersionUtils.is1_8()) {
            bukkitBossBar = Bukkit.createBossBar(name, color.getBarColor(), BarStyle.SOLID);
        }
    }

    public void addPlayer(Player player) {
        if (VersionUtils.is1_8()) {
            BossBarAPI.addBar(player, new TextComponent(name), BossBarAPI.Color.PURPLE, BossBarAPI.Style.PROGRESS, 1);
        } else {
            bukkitBossBar.addPlayer(player);
        }
    }

    public void removePlayer(Player player) {
        if (VersionUtils.is1_8()) {
            BossBarAPI.removeAllBars(player);
        } else {
            bukkitBossBar.removePlayer(player);
        }

    }

}
