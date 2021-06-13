package me.cubixor.sheepquest.spigot.game;

import me.cubixor.sheepquest.spigot.SheepQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Cooldown {

    public static void addCooldown(Player player) {
        SheepQuest plugin = SheepQuest.getInstance();
        if (plugin.getPlayerInfo().get(player).isCooldown()) return;
        plugin.getPlayerInfo().get(player).setCooldown(true);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (plugin.getPlayerInfo().get(player) != null) {
                plugin.getPlayerInfo().get(player).setCooldown(false);
            }
        }, Math.round(plugin.getConfig().getDouble("cooldown") * 20));
    }

    public static boolean checkCooldown(Player player) {
        SheepQuest plugin = SheepQuest.getInstance();
        if (plugin.isDisabled()) {
            return true;
        }
        if (plugin.getPlayerInfo().get(player).isCooldown()) {
            player.sendMessage(plugin.getMessage("general.too-fast"));
            return true;
        }
        return false;
    }

}
