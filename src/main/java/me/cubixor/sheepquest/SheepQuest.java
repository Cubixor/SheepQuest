package me.cubixor.sheepquest;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.base.Charsets;
import me.cubixor.sheepquest.api.PassengerFixReflection;
import me.cubixor.sheepquest.api.PlaceholderExpansion;
import me.cubixor.sheepquest.api.Updater;
import me.cubixor.sheepquest.commands.Command;
import me.cubixor.sheepquest.commands.PlayCommands;
import me.cubixor.sheepquest.commands.SetupWand;
import me.cubixor.sheepquest.commands.TabCompleter;
import me.cubixor.sheepquest.game.*;
import me.cubixor.sheepquest.gameInfo.*;
import me.cubixor.sheepquest.menu.ArenasMenu;
import me.cubixor.sheepquest.menu.SetupMenu;
import me.cubixor.sheepquest.menu.StaffMenu;
import me.cubixor.sheepquest.menu.StatsMenu;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class SheepQuest extends JavaPlugin {


    private final File arenasFile = new File(getDataFolder(), "arenas.yml");
    private final File players = new File(getDataFolder(), "players.yml");

    private FileConfiguration messagesConfig;
    private FileConfiguration arenasConfig;
    private FileConfiguration playersConfig;

    private final HashMap<Player, PlayerData> playerData = new HashMap<>();
    private final HashMap<Player, PlayerInfo> playerInfo = new HashMap<>();
    private final HashMap<Player, ArenaInventories> inventories = new HashMap<>();
    private HashMap<String, Arena> arenas = new HashMap<>();
    private Items items;
    private boolean passengerFix = false;

    private static SheepQuest instance;

    public static SheepQuest getInstance() {
        return instance;
    }


    @Override
    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveConfig();
        loadConfigs();
        if (getConfig().getDouble("config-version") < 1.4) {
            getConfig().set("Signs", null);
        }
        getConfig().set("config-version", getDescription().getVersion());
        saveConfig();

        getCommand("sheepquest").setExecutor(new Command());
        getCommand("t").setExecutor(new Command());
        getCommand("sheepquest").setTabCompleter(new TabCompleter());
        getServer().getPluginManager().registerEvents(new Chat(), this);
        getServer().getPluginManager().registerEvents(new ArenaProtection(), this);
        getServer().getPluginManager().registerEvents(new Kill(), this);
        getServer().getPluginManager().registerEvents(new SheepCarrying(), this);
        getServer().getPluginManager().registerEvents(new Signs(), this);
        getServer().getPluginManager().registerEvents(new SetupWand(), this);
        getServer().getPluginManager().registerEvents(new Teams(), this);
        getServer().getPluginManager().registerEvents(new SetupMenu(), this);
        getServer().getPluginManager().registerEvents(new StaffMenu(), this);
        getServer().getPluginManager().registerEvents(new ArenasMenu(), this);
        getServer().getPluginManager().registerEvents(new StatsMenu(), this);

        //Legacy material initialization
        XMaterial.matchXMaterial(getConfig().getString("sign-colors.inactive")).get().parseItem().getData();

        new PassengerFixReflection().setupPassengerFix();
        new Signs().loadSigns();

        new Updater(this, 83005).runUpdater();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderExpansion().register();
        }

        if (getConfig().getBoolean("send-stats")) {
            Metrics metrics = new Metrics(this, 9022);
            metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> getConfig().getString("language", "en")));
            metrics.addCustomChart(new Metrics.SingleLineChart("games", () -> getArenas().keySet().size()));
        }
    }

    @Override
    public void onDisable() {
        for (String arena : getArenas().keySet()) {
            for (Player p : getArenas().get(arena).getPlayers().keySet()) {
                new PlayCommands().kickPlayer(p, arena);
            }
        }
    }


    public void saveArenas() {
        try {
            arenasConfig.save(arenasFile);
            arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(getMessage("general.arenas-save-error"));
        }
    }

    public void savePlayers() {
        try {
            playersConfig.save(players);
            playersConfig = YamlConfiguration.loadConfiguration(players);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(getMessage("general.players-save-error"));
        }
    }

    public void loadConfigs() {
        reloadConfig();

        for (Language language : Language.values()) {
            String fileName = "messages-" + language.getLanguageCode() + ".yml";
            FileConfiguration msgConf;
            File messages = new File(getDataFolder(), fileName);
            msgConf = YamlConfiguration.loadConfiguration(messages);


            final InputStream defConfigStream = getResource(fileName);
            if (defConfigStream != null) {
                msgConf.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
            }
            msgConf.options().copyDefaults(true);

            try {
                msgConf.save(messages);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (getConfig().getString("language").equals(language.getLanguageCode())) {
                messagesConfig = msgConf;
            }

        }

        if (!players.exists()) {
            saveResource("players.yml", false);
        }
        if (!arenasFile.exists()) {
            saveResource("arenas.yml", false);
        }

        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        playersConfig = YamlConfiguration.loadConfiguration(players);


        if (getArenasConfig().getConfigurationSection("Arenas") != null) {
            for (String arena : getArenasConfig().getConfigurationSection("Arenas").getKeys(false)) {
                getArenas().put(arena, new Arena());
                Signs signs = new Signs();
                signs.loadArenaSigns(arena);
                signs.updateSigns(getArenas().get(arena));
            }
        }
        setItems(new Items());
    }


    public void putInventories(Player player, String arena) {
        if (!getInventories().containsKey(player)) {
            getInventories().put(player, new ArenaInventories(arena));
        } else {
            if (getInventories().get(player).getArena() == null) {
                getInventories().get(player).setArena(arena);
            }
            if (getInventories().get(player).getArena() != null && !getInventories().get(player).getArena().equals(arena)) {
                getInventories().put(player, new ArenaInventories(arena));
            }
        }
    }

    public String getMessage(String path) {
        String prefix = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("prefix"));
        String message = messagesConfig.getString(path).replace("%prefix%", prefix);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public List<String> getMessageList(String path) {
        List<String> message = new ArrayList<>(messagesConfig.getStringList(path));
        List<String> finalMessage = new ArrayList<>();
        for (String s : message) {
            finalMessage.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return finalMessage;
    }


    public HashMap<String, Arena> getArenas() {
        return arenas;
    }

    public void setArenas(HashMap<String, Arena> arenas) {
        this.arenas = arenas;
    }

    public HashMap<Player, PlayerData> getPlayerData() {
        return playerData;
    }


    public HashMap<Player, PlayerInfo> getPlayerInfo() {
        return playerInfo;
    }


    public HashMap<Player, ArenaInventories> getInventories() {
        return inventories;
    }


    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
    }

    public boolean isPassengerFix() {
        return passengerFix;
    }

    public void setPassengerFix(boolean passengerFix) {
        this.passengerFix = passengerFix;
    }

    public FileConfiguration getArenasConfig() {
        return arenasConfig;
    }

    public FileConfiguration getStats() {
        return playersConfig;
    }
}
