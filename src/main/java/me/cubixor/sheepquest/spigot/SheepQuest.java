package me.cubixor.sheepquest.spigot;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.sheepquest.spigot.api.ConfigUpdater;
import me.cubixor.sheepquest.spigot.api.PassengerFix;
import me.cubixor.sheepquest.spigot.api.PlaceholderExpansion;
import me.cubixor.sheepquest.spigot.api.Updater;
import me.cubixor.sheepquest.spigot.commands.Command;
import me.cubixor.sheepquest.spigot.commands.PlayCommands;
import me.cubixor.sheepquest.spigot.commands.SetupWand;
import me.cubixor.sheepquest.spigot.commands.TabCompleter;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.game.*;
import me.cubixor.sheepquest.spigot.game.kits.*;
import me.cubixor.sheepquest.spigot.gameInfo.*;
import me.cubixor.sheepquest.spigot.menu.*;
import me.cubixor.sheepquest.spigot.mysql.ConnectionSetup;
import me.cubixor.sheepquest.spigot.mysql.MysqlConnection;
import me.cubixor.sheepquest.spigot.socket.SocketClient;
import me.cubixor.sheepquest.utils.SocketConnection;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.inventivetalent.apihelper.APIManager;
import org.inventivetalent.bossbar.BossBarAPI;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class SheepQuest extends JavaPlugin {


    private final File arenasFile = new File(getDataFolder(), "arenas.yml");
    private final File playersFile = new File(getDataFolder(), "players.yml");

    private FileConfiguration messagesConfig;
    private FileConfiguration arenasConfig;
    private FileConfiguration playersConfig;
    private final HashMap<String, List<Location>> signs = new HashMap<>();

    private final HashMap<Player, PlayerInfo> playerInfo = new HashMap<>();
    private final HashMap<Player, ArenaInventories> inventories = new HashMap<>();
    private final HashMap<String, LocalArena> localArenas = new HashMap<>();
    private FileConfiguration connectionConfig;
    private HashMap<String, Arena> arenas = new HashMap<>();
    private Items items;
    private final List<Kit> kits = new ArrayList<>();
    private boolean enabled = false;
    private boolean before9 = false;
    private boolean passengerFix = false;

    private MysqlConnection mysqlConnection;
    private boolean bungee;
    private String serverName;
    private SocketConnection bungeeSocket;

    private static SheepQuest instance;

    public static SheepQuest getInstance() {
        return instance;
    }


    @Override
    public void onEnable() {
        instance = this;

        //Check if server is 1.8
        // If true register bossbar api
        try {
            BarColor.WHITE.name();
        } catch (NoClassDefFoundError e) {
            before9 = true;
            APIManager.require(BossBarAPI.class, this);
            APIManager.initAPI(BossBarAPI.class);
        }

        //Legacy material initialization
        XMaterial.matchXMaterial("BLACK_STAINED_GLASS").get().parseItem().getData();

        //1.9 and 1.10 passenger fix
        PassengerFix.setupPassengerFix();

        kits.add(new KitStandard());
        kits.add(new KitArcher());
        kits.add(new KitAthlete());

        loadConfigs();
        if (!getServer().getPluginManager().isPluginEnabled(this)) {
            return;
        }
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
        getServer().getPluginManager().registerEvents(new MenuUtils(), this);
        getServer().getPluginManager().registerEvents(new JoinSheep(), this);
        getServer().getPluginManager().registerEvents(new KitMenu(), this);
        for (Kit kit : kits) {
            getServer().getPluginManager().registerEvents(kit, this);
        }


        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderExpansion().register();
        }

        new Updater(this, 83005).runUpdaterTask();

        if (getConfig().getBoolean("send-stats")) {
            Metrics metrics = new Metrics(this, 9022);
            metrics.addCustomChart(new SimplePie("used_language", () -> getConfig().getString("language", "en")));
            metrics.addCustomChart(new SingleLineChart("games", () -> getAllArenas().size()));
        }
    }

    @Override
    public void onDisable() {
        for (String arena : getLocalArenas().keySet()) {
            List<Player> players = new ArrayList<>(getLocalArenas().get(arena).getPlayerTeam().keySet());
            for (Player p : players) {
                new PlayCommands().kickFromLocalArenaSynchronized(p, localArenas.get(arena), false, false);
            }
        }
        if (getBungeeSocket() != null) {
            try {
                getBungeeSocket().getSocket().close();
                getBungeeSocket().getInputStream().close();
                getBungeeSocket().getOutputStream().close();
            } catch (IOException ignored) {
            }
        }
        if (getMysqlConnection() != null) {
            try {
                getMysqlConnection().getConnection().close();
            } catch (SQLException ignored) {
            }
        }
        for (BukkitTask bukkitTask : Bukkit.getScheduler().getPendingTasks()) {
            bukkitTask.cancel();
        }
    }


    public void saveArenas() {
        try {
            arenasConfig.save(arenasFile);
            arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePlayers() {
        try {
            playersConfig.save(playersFile);
            playersConfig = YamlConfiguration.loadConfiguration(playersFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfigs() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {

            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
            if (!getConfig().getString("config-version").equals(getDescription().getVersion())) {
                saveConfig();
            }
            reloadConfig();

            File connectionFile = new File(getDataFolder(), "connection.yml");

            for (Language language : Language.values()) {
                String fileName = "messages-" + language.getLanguageCode() + ".yml";
                File messagesFile = new File(getDataFolder(), fileName);

                if (!messagesFile.exists()) {
                    saveResource(fileName, false);
                }
                FileConfiguration msgConf = YamlConfiguration.loadConfiguration(messagesFile);

                if (!getConfig().getString("config-version").equals(getDescription().getVersion())) {
                    final InputStream defConfigStream = getResource(fileName);
                    if (defConfigStream != null) {
                        msgConf.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));
                    }
                    msgConf.options().copyDefaults(true);
                    try {
                        msgConf.save(messagesFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (getConfig().getString("language").equals(language.getLanguageCode())) {
                    messagesConfig = msgConf;
                }

            }

            if (!playersFile.exists()) {
                saveResource("players.yml", false);
            }
            if (!arenasFile.exists()) {
                saveResource("arenas.yml", false);
            }
            if (!connectionFile.exists()) {
                saveResource("connection.yml", false);
            }

            arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
            playersConfig = YamlConfiguration.loadConfiguration(playersFile);
            connectionConfig = YamlConfiguration.loadConfiguration(connectionFile);

            if (getConfig().getBoolean("database.enabled-stats") || getConfig().getBoolean("database.enabled-arenas")) {
                ConnectionSetup connectionSetup = new ConnectionSetup();

                if (!connectionSetup.connectToDatabase(connectionSetup.mysqlSetup())) {
                    Bukkit.getScheduler().runTask(this, () -> getServer().getPluginManager().disablePlugin(this));
                    return;
                }
            }

            setServerName(getConnectionConfig().getString("server-name"));

            new ConfigUpdater().updateConfigs();

            for (String arena : ConfigUtils.getArenas()) {
                getSigns().put(arena, new ArrayList<>());
                if (ConfigUtils.getString(arena, ConfigField.SERVER).equals(getServerName())) {
                    getLocalArenas().put(arena, new LocalArena(arena));
                }
            }
            getSigns().put("quickjoin", new ArrayList<>());
            for (Player p : Bukkit.getOnlinePlayers()) {
                getPlayerInfo().replace(p, new PlayerInfo(p.getName()));
            }

            if (getConfig().getBoolean("bungee.bungee-mode")) {
                setBungee(true);
                new SocketClient().clientSetup(getConnectionConfig().getString("host"),
                        getConnectionConfig().getInt("port"),
                        getConnectionConfig().getString("server-name"));
            }

            for (Kit kit : kits) {
                kit.loadItems();
            }

            setItems(new Items());
            new Signs().loadSigns();
            enabled = true;
        });
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

    public Arena getArena(String name) {
        if (getLocalArenas().containsKey(name)) {
            return getLocalArenas().get(name);
        } else if (getArenas().containsKey(name)) {
            return getArenas().get(name);
        }
        return null;
    }

    public List<Arena> getAllArenas() {
        List<Arena> arenas = new ArrayList<>();
        arenas.addAll(getLocalArenas().values());
        arenas.addAll(getArenas().values());
        return arenas;
    }

    public HashMap<String, LocalArena> getLocalArenas() {
        return localArenas;
    }

    public HashMap<String, Arena> getArenas() {
        return arenas;
    }

    public void setArenas(HashMap<String, Arena> arenas) {
        this.arenas = arenas;
    }

    public File getArenasFile() {
        return arenasFile;
    }

    public void setArenasConfig(FileConfiguration arenasConfig) {
        this.arenasConfig = arenasConfig;
    }

    public HashMap<String, List<Location>> getSigns() {
        return signs;
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

    public FileConfiguration getArenasConfig() {
        return arenasConfig;
    }

    public FileConfiguration getStats() {
        return playersConfig;
    }

    public MysqlConnection getMysqlConnection() {
        return mysqlConnection;
    }

    public void setMysqlConnection(MysqlConnection mysqlConnection) {
        this.mysqlConnection = mysqlConnection;
    }

    public boolean isBungee() {
        return bungee;
    }

    public void setBungee(boolean bungee) {
        this.bungee = bungee;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public FileConfiguration getConnectionConfig() {
        return connectionConfig;
    }

    public SocketConnection getBungeeSocket() {
        return bungeeSocket;
    }

    public void setBungeeSocket(SocketConnection bungeeSocket) {
        this.bungeeSocket = bungeeSocket;
    }

    public List<Kit> getKits() {
        return kits;
    }

    public boolean isDisabled() {
        return !enabled;
    }

    public boolean isBefore9() {
        return before9;
    }

    public boolean isPassengerFix() {
        return passengerFix;
    }

    public void setPassengerFix(boolean passengerFix) {
        this.passengerFix = passengerFix;
    }
}
