package me.cubixor.sheepquest.spigot.api;

import com.cryptomorin.xseries.XSound;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Sounds {

    public static boolean playSound(Player player, Location loc, String path) {
        SheepQuest plugin = SheepQuest.getInstance();

        if (!plugin.getConfig().getBoolean("sounds." + path + ".enabled")) {
            return false;
        }

        Sound sound = XSound.matchXSound(plugin.getConfig().getString("sounds." + path + ".sound")).get().parseSound();
        float volume = (float) plugin.getConfig().getDouble("sounds." + path + ".volume");
        float pitch = (float) plugin.getConfig().getDouble("sounds." + path + ".pitch");

        player.playSound(loc, sound, volume, pitch);
        return true;
    }

    public static void playSound(LocalArena localArena, Location loc, String path) {
        for (Player p : localArena.getPlayerTeam().keySet()) {
            if (!playSound(p, loc, path)) return;
        }
    }
}
