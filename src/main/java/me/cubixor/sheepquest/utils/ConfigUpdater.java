package me.cubixor.sheepquest.utils;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigUpdater {

    public void updateTo2(FileConfiguration config) {
        if (config.getDouble("config-version") < 2.0) {
            config.set("kits", null);
            config.set("special-events", null);
        }
    }
}
