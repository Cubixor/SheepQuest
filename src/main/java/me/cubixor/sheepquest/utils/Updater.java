package me.cubixor.sheepquest.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class Updater {

    private final Plugin plugin;
    private final int resourceId;

    public Updater(Plugin plugin, int resourceId) {
        this.plugin = plugin;
        this.resourceId = resourceId;
    }

    public void runUpdaterTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, this::runUpdater, 0, 1728000);
    }

    private void runUpdater() {
        getVersion(version -> {
            if (!plugin.getDescription().getVersion().equals(version)) {
                plugin.getLogger().warning("There is a new update of SheepQuest available!");
                plugin.getLogger().warning("Your version: " + plugin.getDescription().getVersion());
                plugin.getLogger().warning("New version: " + version);
                plugin.getLogger().warning("Go to https://www.spigotmc.org/resources/83005/ and download it!");
            }
        });
    }

    private void getVersion(final Consumer<String> consumer) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + this.resourceId).openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    consumer.accept(scanner.next());
                }
            } catch (IOException exception) {
                this.plugin.getLogger().info("Cannot look for updates: " + exception.getMessage());
            }
        });
    }
}
