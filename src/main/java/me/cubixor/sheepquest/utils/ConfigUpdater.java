package me.cubixor.sheepquest.utils;

import me.cubixor.minigamesapi.spigot.config.CustomConfig;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigUpdater {

    public void updateTo2(FileConfiguration config) {
        if (config.getDouble("config-version") < 2.0) {
            config.set("kits", null);
            config.set("special-events", null);
        }
    }

    public void updateArenasTo2(CustomConfig arenasConfig) {
        FileConfiguration fileConfiguration = arenasConfig.get();

        if (fileConfiguration.get("signs") == null) {
            fileConfiguration.createSection("signs.quickjoin");
        }

        if (fileConfiguration.get("arenas") == null) {
            fileConfiguration.createSection("arenas");
        }

        arenasConfig.save();
    }
}
