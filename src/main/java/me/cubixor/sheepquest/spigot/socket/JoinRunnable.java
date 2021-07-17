package me.cubixor.sheepquest.spigot.socket;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.commands.PlayCommands;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class JoinRunnable {

    private final SheepQuest plugin;
    int timesChecked = 200;

    public JoinRunnable() {
        plugin = SheepQuest.getInstance();
    }

    public void runTask(String playerName, Arena arena) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayerExact(playerName);
                if (player != null && Bukkit.getOnlinePlayers().contains(player)) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> new PlayCommands().putInLocalArena(player, plugin.getLocalArenas().get(arena.getName())));
                    this.cancel();
                } else if (timesChecked > 0) {
                    timesChecked--;
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }
}
