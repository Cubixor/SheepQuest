package me.cubixor.sheepquest;

import me.cubixor.sheepquest.commands.Command;
import me.cubixor.sheepquest.commands.PlayCommands;
import me.cubixor.sheepquest.commands.SetupWand;
import me.cubixor.sheepquest.commands.TabCompleter;
import me.cubixor.sheepquest.game.*;
import me.cubixor.sheepquest.menu.ArenasMenu;
import me.cubixor.sheepquest.menu.SetupMenu;
import me.cubixor.sheepquest.menu.StaffMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class SheepQuest extends JavaPlugin {


    private final File messages = new File(getDataFolder(), "messages.yml");
    private FileConfiguration messagesConfig;
    private final File arenasFile = new File(getDataFolder(), "arenas.yml");
    private FileConfiguration arenasConfig;
    private final File players = new File(getDataFolder(), "players.yml");
    private FileConfiguration playersConfig;


    public HashMap<String, Arena> arenas = new HashMap<>();
    private HashMap<Player, PlayerData> playerData = new HashMap<>();
    public HashMap<Player, PlayerInfo> playerInfo = new HashMap<>();


    public HashMap<Player, ArenaInventories> inventories = new HashMap<>();
    public Items items;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        loadConfigs();

        getCommand("sheepquest").setExecutor(new Command(this));
        getCommand("t").setExecutor(new Command(this));
        getCommand("sheepquest").setTabCompleter(new TabCompleter(this));
        getServer().getPluginManager().registerEvents(new Chat(this), this);
        getServer().getPluginManager().registerEvents(new ArenaProtection(this), this);
        getServer().getPluginManager().registerEvents(new Kill(this), this);
        getServer().getPluginManager().registerEvents(new SheepCarrying(this), this);
        getServer().getPluginManager().registerEvents(new Signs(this), this);
        getServer().getPluginManager().registerEvents(new SetupWand(this), this);
        getServer().getPluginManager().registerEvents(new Teams(this), this);
        getServer().getPluginManager().registerEvents(new SetupMenu(this), this);
        getServer().getPluginManager().registerEvents(new StaffMenu(this), this);
        getServer().getPluginManager().registerEvents(new ArenasMenu(this), this);

        new Signs(this).loadSigns();


    }


    public void saveArenas() {
        try {
            arenasConfig.save(arenasFile);
            arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        } catch (IOException e) {
            Bukkit.getConsoleSender().sendMessage(getMessage("general.arenas-save-error"));
        }
    }

    public FileConfiguration getArenasConfig() {
        return arenasConfig;
    }

    public FileConfiguration getStats() {
        return playersConfig;
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
        if (!messages.exists()) {
            saveResource("messages.yml", false);
        }
        if (!players.exists()) {
            saveResource("players.yml", false);
        }
        if (!arenasFile.exists()) {
            saveResource("arenas.yml", false);
        }

        reloadConfig();
        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        messagesConfig = YamlConfiguration.loadConfiguration(messages);
        playersConfig = YamlConfiguration.loadConfiguration(players);

        if (getArenasConfig().getConfigurationSection("Arenas") != null) {
            for (String arena : getArenasConfig().getConfigurationSection("Arenas").getKeys(false)) {
                arenas.put(arena, new Arena(this));
                new Signs(this).loadArenaSigns(arena);
            }
        }

        items = new Items(this);
    }


    public PlayerData getPlayerData(Player player) {
        return playerData.get(player);
    }

    public void addPlayerData(Player player, PlayerData playerData) {
        this.playerData.put(player, playerData);
    }

    public void removePlayerData(Player player) {
        this.playerData.remove(player);
    }

    public void putInventories(Player player, String arena) {
        if (!inventories.containsKey(player)) {
            inventories.put(player, new ArenaInventories(arena));
        } else {
            if(inventories.get(player).arena == null){
                inventories.get(player).arena = arena;
            }
            if (inventories.get(player).arena != null && !inventories.get(player).arena.equals(arena)) {
                inventories.put(player, new ArenaInventories(arena));
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


    @Override
    public void onDisable() {
        for (String arena : arenas.keySet()) {
            for (Player p : arenas.get(arena).playerTeam.keySet()) {
                new PlayCommands(this).kickPlayer(p, arena);
            }
        }
    }
}
