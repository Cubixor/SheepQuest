package me.cubixor.sheepquest.spigot.api;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigUpdater {

    public final SheepQuest plugin;

    public ConfigUpdater() {
        plugin = SheepQuest.getInstance();
    }

    public void updateConfigs() {
        if (!plugin.getConfig().getString("config-version").equals(plugin.getDescription().getVersion())) {
            if (Float.parseFloat(plugin.getConfig().getString("config-version")) < 1.4) {
                plugin.getArenasConfig().set("Signs", null);
            }

            if (Float.parseFloat(plugin.getConfig().getString("config-version")) < 1.6) {

                Path path = plugin.getArenasFile().toPath();
                File oldArenasFile = new File(plugin.getDataFolder(), "arenasOLD.yml");
                Path pathOld = oldArenasFile.toPath();

                try {
                    Files.copy(path, pathOld, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileConfiguration oldArenasConfig = YamlConfiguration.loadConfiguration(oldArenasFile);
                plugin.saveResource("arenas.yml", true);
                plugin.setArenasConfig(YamlConfiguration.loadConfiguration(plugin.getArenasFile()));

                List<OldArena> oldArenas = new ArrayList<>();

                for (String arena : oldArenasConfig.getConfigurationSection("Arenas").getKeys(false)) {
                    OldArena oldArena = new OldArena(arena);
                    oldArena.setActive(oldArenasConfig.getBoolean("Arenas." + arena + ".active"));
                    oldArena.setMinPlayers(oldArenasConfig.getInt("Arenas." + arena + ".min-players"));
                    oldArena.setMaxPlayers(oldArenasConfig.getInt("Arenas." + arena + ".max-players"));
                    oldArena.setWaitingLobby((Location) oldArenasConfig.get("Arenas." + arena + ".waiting-lobby"));
                    oldArena.setMainLobby((Location) oldArenasConfig.get("Arenas." + arena + ".main-lobby"));
                    oldArena.setSheepSpawn((Location) oldArenasConfig.get("Arenas." + arena + ".sheep-spawn"));
                    oldArena.setRedSpawn((Location) oldArenasConfig.get("Arenas." + arena + ".teams.red-spawn"));
                    oldArena.setGreenSpawn((Location) oldArenasConfig.get("Arenas." + arena + ".teams.green-spawn"));
                    oldArena.setBlueSpawn((Location) oldArenasConfig.get("Arenas." + arena + ".teams.blue-spawn"));
                    oldArena.setYellowSpawn((Location) oldArenasConfig.get("Arenas." + arena + ".teams.yellow-spawn"));
                    oldArena.setRedAreaMin((Location) oldArenasConfig.get("Arenas." + arena + ".teams-area.red.min-point"));
                    oldArena.setRedAreaMax((Location) oldArenasConfig.get("Arenas." + arena + ".teams-area.red.max-point"));
                    oldArena.setGreenAreaMin((Location) oldArenasConfig.get("Arenas." + arena + ".teams-area.green.min-point"));
                    oldArena.setGreenAreaMax((Location) oldArenasConfig.get("Arenas." + arena + ".teams-area.green.max-point"));
                    oldArena.setBlueAreaMin((Location) oldArenasConfig.get("Arenas." + arena + ".teams-area.blue.min-point"));
                    oldArena.setBlueAreaMax((Location) oldArenasConfig.get("Arenas." + arena + ".teams-area.blue.max-point"));
                    oldArena.setYellowAreaMin((Location) oldArenasConfig.get("Arenas." + arena + ".teams-area.yellow.min-point"));
                    oldArena.setYellowAreaMax((Location) oldArenasConfig.get("Arenas." + arena + ".teams-area.yellow.max-point"));

                    oldArenas.add(oldArena);

                }
                HashMap<String, List<Location>> signs = new HashMap<>();
                for (String arena : oldArenasConfig.getConfigurationSection("Signs").getKeys(false)) {
                    List<Location> arenaSigns = new ArrayList<>((List<Location>) oldArenasConfig.getList("Signs." + arena));
                    signs.put(arena, arenaSigns);
                }

                for (OldArena oldArena : oldArenas) {
                    String name = oldArena.getName();
                    ConfigUtils.insertArena(name);
                    ConfigUtils.updateField(name, ConfigField.ACTIVE, oldArena.isActive());
                    ConfigUtils.updateField(name, ConfigField.MIN_PLAYERS, oldArena.getMinPlayers());
                    ConfigUtils.updateField(name, ConfigField.MAX_PLAYERS, oldArena.getMaxPlayers());
                    if (oldArena.getWaitingLobby() != null) {
                        ConfigUtils.updateField(name, ConfigField.WAITING_LOBBY, ConfigUtils.locationToString(oldArena.getWaitingLobby()));
                    }
                    if (oldArena.getMainLobby() != null) {
                        ConfigUtils.updateField(name, ConfigField.MAIN_LOBBY, ConfigUtils.locationToString(oldArena.getMainLobby()));
                    }
                    if (oldArena.getSheepSpawn() != null) {
                        ConfigUtils.updateField(name, ConfigField.SHEEP_SPAWN, ConfigUtils.locationToString(oldArena.getSheepSpawn()));
                    }
                    if (oldArena.getRedSpawn() != null) {
                        ConfigUtils.updateField(name, ConfigField.RED_SPAWN, ConfigUtils.locationToString(oldArena.getRedSpawn()));
                    }
                    if (oldArena.getGreenSpawn() != null) {
                        ConfigUtils.updateField(name, ConfigField.GREEN_SPAWN, ConfigUtils.locationToString(oldArena.getGreenSpawn()));
                    }
                    if (oldArena.getBlueSpawn() != null) {
                        ConfigUtils.updateField(name, ConfigField.BLUE_SPAWN, ConfigUtils.locationToString(oldArena.getBlueSpawn()));
                    }
                    if (oldArena.getYellowSpawn() != null) {
                        ConfigUtils.updateField(name, ConfigField.YELLOW_SPAWN, ConfigUtils.locationToString(oldArena.getYellowSpawn()));
                    }
                    if (oldArena.getRedAreaMin() != null) {
                        ConfigUtils.updateField(name, ConfigField.RED_AREA, ConfigUtils.joinLocations(oldArena.getRedAreaMin(), oldArena.getRedAreaMax()));
                    }
                    if (oldArena.getGreenAreaMin() != null) {
                        ConfigUtils.updateField(name, ConfigField.GREEN_AREA, ConfigUtils.joinLocations(oldArena.getGreenAreaMin(), oldArena.getGreenAreaMax()));
                    }
                    if (oldArena.getBlueAreaMin() != null) {
                        ConfigUtils.updateField(name, ConfigField.BLUE_AREA, ConfigUtils.joinLocations(oldArena.getBlueAreaMin(), oldArena.getBlueAreaMax()));
                    }
                    if (oldArena.getYellowAreaMin() != null) {
                        ConfigUtils.updateField(name, ConfigField.YELLOW_AREA, ConfigUtils.joinLocations(oldArena.getYellowAreaMin(), oldArena.getYellowAreaMax()));
                    }

                }

                for (String arena : signs.keySet()) {
                    List<String> signList = new ArrayList<>();
                    for (Location loc : signs.get(arena)) {
                        signList.add(ConfigUtils.locationToString(loc));
                    }
                    plugin.getArenasConfig().set("signs." + arena, signList);
                }
                plugin.saveArenas();
            }

            plugin.getConfig().set("config-version", Float.parseFloat(plugin.getDescription().getVersion()));
            plugin.saveConfig();
        }

    }

    private static class OldArena {
        private final String name;
        private boolean active;
        private int minPlayers;
        private int maxPlayers;
        private Location waitingLobby;
        private Location mainLobby;
        private Location sheepSpawn;
        private Location redSpawn;
        private Location greenSpawn;
        private Location blueSpawn;
        private Location yellowSpawn;
        private Location redAreaMin;
        private Location redAreaMax;
        private Location greenAreaMin;
        private Location greenAreaMax;
        private Location blueAreaMin;
        private Location blueAreaMax;
        private Location yellowAreaMin;
        private Location yellowAreaMax;

        public OldArena(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public int getMinPlayers() {
            return minPlayers;
        }

        public void setMinPlayers(int minPlayers) {
            this.minPlayers = minPlayers;
        }

        public int getMaxPlayers() {
            return maxPlayers;
        }

        public void setMaxPlayers(int maxPlayers) {
            this.maxPlayers = maxPlayers;
        }

        public Location getWaitingLobby() {
            return waitingLobby;
        }

        public void setWaitingLobby(Location waitingLobby) {
            this.waitingLobby = waitingLobby;
        }

        public Location getMainLobby() {
            return mainLobby;
        }

        public void setMainLobby(Location mainLobby) {
            this.mainLobby = mainLobby;
        }

        public Location getSheepSpawn() {
            return sheepSpawn;
        }

        public void setSheepSpawn(Location sheepSpawn) {
            this.sheepSpawn = sheepSpawn;
        }

        public Location getRedSpawn() {
            return redSpawn;
        }

        public void setRedSpawn(Location redSpawn) {
            this.redSpawn = redSpawn;
        }

        public Location getGreenSpawn() {
            return greenSpawn;
        }

        public void setGreenSpawn(Location greenSpawn) {
            this.greenSpawn = greenSpawn;
        }

        public Location getBlueSpawn() {
            return blueSpawn;
        }

        public void setBlueSpawn(Location blueSpawn) {
            this.blueSpawn = blueSpawn;
        }

        public Location getYellowSpawn() {
            return yellowSpawn;
        }

        public void setYellowSpawn(Location yellowSpawn) {
            this.yellowSpawn = yellowSpawn;
        }

        public Location getRedAreaMin() {
            return redAreaMin;
        }

        public void setRedAreaMin(Location redAreaMin) {
            this.redAreaMin = redAreaMin;
        }

        public Location getRedAreaMax() {
            return redAreaMax;
        }

        public void setRedAreaMax(Location redAreaMax) {
            this.redAreaMax = redAreaMax;
        }

        public Location getGreenAreaMin() {
            return greenAreaMin;
        }

        public void setGreenAreaMin(Location greenAreaMin) {
            this.greenAreaMin = greenAreaMin;
        }

        public Location getGreenAreaMax() {
            return greenAreaMax;
        }

        public void setGreenAreaMax(Location greenAreaMax) {
            this.greenAreaMax = greenAreaMax;
        }

        public Location getBlueAreaMin() {
            return blueAreaMin;
        }

        public void setBlueAreaMin(Location blueAreaMin) {
            this.blueAreaMin = blueAreaMin;
        }

        public Location getBlueAreaMax() {
            return blueAreaMax;
        }

        public void setBlueAreaMax(Location blueAreaMax) {
            this.blueAreaMax = blueAreaMax;
        }

        public Location getYellowAreaMin() {
            return yellowAreaMin;
        }

        public void setYellowAreaMin(Location yellowAreaMin) {
            this.yellowAreaMin = yellowAreaMin;
        }

        public Location getYellowAreaMax() {
            return yellowAreaMax;
        }

        public void setYellowAreaMax(Location yellowAreaMax) {
            this.yellowAreaMax = yellowAreaMax;
        }
    }

}
