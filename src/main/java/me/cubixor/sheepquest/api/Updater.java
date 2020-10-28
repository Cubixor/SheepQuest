package me.cubixor.sheepquest.api;

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

    public void runUpdater() {
        getVersion(version -> {
            if (!plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
                plugin.getLogger().warning("There is a new update of SheepQuest available!");
                plugin.getLogger().warning("Your version: " + plugin.getDescription().getVersion());
                plugin.getLogger().warning("New version: " + version);
                plugin.getLogger().warning("Go to spigotmc.org and download it!");
            }
        });
    }

    public void getVersion(final Consumer<String> consumer) {
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
