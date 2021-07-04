package me.cubixor.sheepquest.spigot.api;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Utils {

    public static LinkedHashMap<Team, Integer> sortTeams(HashMap<Team, Integer> hm) {
        List<Map.Entry<Team, Integer>> list = new LinkedList<>(hm.entrySet());

        list.sort(Map.Entry.comparingByValue());

        LinkedHashMap<Team, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<Team, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static LinkedHashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(hm.entrySet());

        list.sort(Map.Entry.comparingByValue());

        LinkedHashMap<String, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static ItemStack setItemStack(String materialPath, String namePath, String lorePath) {
        SheepQuest plugin = SheepQuest.getInstance();

        ItemStack itemStack = XMaterial.matchXMaterial(plugin.getConfig().getString(materialPath)).get().parseItem();
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(plugin.getMessage(namePath));
        itemMeta.setLore(plugin.getMessageList(lorePath));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack setItemStack(Material material, String namePath, String lorePath) {
        SheepQuest plugin = SheepQuest.getInstance();

        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(plugin.getMessage(namePath));
        itemMeta.setLore(plugin.getMessageList(lorePath));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    public static ItemStack setItemStack(ItemStack itemStack, String namePath) {
        SheepQuest plugin = SheepQuest.getInstance();

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(plugin.getMessage(namePath));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack setItemStack(Material material, String namePath, String toReplace, String replacement) {
        SheepQuest plugin = SheepQuest.getInstance();

        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(plugin.getMessage(namePath).replace(toReplace, replacement));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack setItemStack(Material material, String namePath, String lorePath, String toReplace, String replaceMessage) {
        SheepQuest plugin = SheepQuest.getInstance();

        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(plugin.getMessage(namePath));
        List<String> lore = plugin.getMessageList(lorePath);
        for (String s : lore) {
            Collections.replaceAll(lore, s, s.replace(toReplace, replaceMessage));
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack setItemStack(ItemStack itemStack, String namePath, String lorePath, String toReplace, String replaceMessage) {
        SheepQuest plugin = SheepQuest.getInstance();

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(plugin.getMessage(namePath));
        List<String> lore = plugin.getMessageList(lorePath);
        for (String s : lore) {
            Collections.replaceAll(lore, s, s.replace(toReplace, replaceMessage));
        }
        itemStack.setAmount(1);
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static LocalArena getLocalArena(Player player) {
        SheepQuest plugin = SheepQuest.getInstance();

        for (String arena : plugin.getLocalArenas().keySet()) {
            if (plugin.getLocalArenas().get(arena).getPlayerTeam().containsKey(player)) {
                return plugin.getLocalArenas().get(arena);
            }
        }
        return null;
    }

    public static Arena getBungeeArena(String player) {
        SheepQuest plugin = SheepQuest.getInstance();

        if (plugin.isBungee()) {
            for (String arena : plugin.getArenas().keySet()) {
                if (plugin.getArenas().get(arena).getPlayers().contains(player)) {
                    return plugin.getArenas().get(arena);
                }
            }
        }
        return null;
    }

    public static Arena getArena(String player) {
        if (Bukkit.getPlayerExact(player) != null && getLocalArena(Bukkit.getPlayerExact(player)) != null) {
            return getLocalArena(Bukkit.getPlayerExact(player));
        }
        if (getBungeeArena(player) != null) {
            return getBungeeArena(player);
        }
        return null;
    }

    public static List<String> getPlayers() {
        SheepQuest plugin = SheepQuest.getInstance();

        List<String> players = new ArrayList<>();
        for (Arena arena : plugin.getLocalArenas().values()) {
            players.addAll(arena.getPlayers());
        }
        if (plugin.isBungee()) {
            for (Arena arena : plugin.getArenas().values()) {
                players.addAll(arena.getPlayers());
            }
        }
        return players;
    }

    public static boolean isInArena(Player player) {
        return getLocalArena(player) != null || getBungeeArena(player.getName()) != null;
    }

    public static HashMap<Team, Integer> getTeamPlayers(LocalArena localArena) {
        HashMap<Team, Integer> teamPlayers = new HashMap<>();
        for (Team team : Team.values()) {
            if (!team.equals(Team.NONE)) {
                teamPlayers.put(team, 0);
            }
        }

        for (Player p : localArena.getPlayerTeam().keySet()) {
            Team team = localArena.getPlayerTeam().get(p);
            teamPlayers.replace(team, teamPlayers.get(team) + 1);
        }
        return teamPlayers;
    }

    public static Team getTeamByWool(ItemStack material) {
        if (material.getType().toString().contains("WOOL")) {
            for (Team team : Team.values()) {
                if (XMaterial.matchXMaterial(material).equals(XMaterial.matchXMaterial(team.getWool()))) {
                    return team;
                }
            }
            return Team.NONE;
        }
        return null;
    }

    public static String getStringState(Arena arena) {
        SheepQuest plugin = SheepQuest.getInstance();

        String gameState = null;
        if (arena == null) {
            gameState = plugin.getMessage("general.state-offline");
        } else if (!ConfigUtils.getBoolean(arena.getName(), ConfigField.ACTIVE)) {
            gameState = plugin.getMessage("general.state-inactive");
        } else if (arena.getState().equals(GameState.WAITING)) {
            gameState = plugin.getMessage("general.state-waiting");
        } else if (arena.getState().equals(GameState.STARTING)) {
            gameState = plugin.getMessage("general.state-starting");
        } else if (arena.getState().equals(GameState.GAME)) {
            gameState = plugin.getMessage("general.state-game");
        } else if (arena.getState().equals(GameState.ENDING)) {
            gameState = plugin.getMessage("general.state-ending");
        }
        return gameState;
    }

    public static void removeBossBars(Player player, LocalArena localArena) {
        for (BossBar bossBar : localArena.getTeamBossBars().values()) {
            bossBar.removePlayer(player);
        }
    }

    public static void playSound(LocalArena localArena, Location loc, Sound sound, float volume, float pitch) {
        for (Player p : localArena.getPlayerTeam().keySet()) {
            p.playSound(loc, sound, volume, pitch);
        }
    }

    public static LinkedHashMap<ConfigField, Boolean> checkIfReady(String arena) {
        LinkedHashMap<ConfigField, Boolean> ready = new LinkedHashMap<>();

        ready.put(ConfigField.MIN_PLAYERS, ConfigUtils.getInt(arena, ConfigField.MIN_PLAYERS) != 0);
        ready.put(ConfigField.MAX_PLAYERS, ConfigUtils.getInt(arena, ConfigField.MAX_PLAYERS) != 0);
        ready.put(ConfigField.MAIN_LOBBY, ConfigUtils.getLocation(arena, ConfigField.MAIN_LOBBY) != null);
        ready.put(ConfigField.WAITING_LOBBY, ConfigUtils.getLocation(arena, ConfigField.WAITING_LOBBY) != null);
        ready.put(ConfigField.SHEEP_SPAWN, ConfigUtils.getLocation(arena, ConfigField.SHEEP_SPAWN) != null);
        ready.put(ConfigField.RED_SPAWN, ConfigUtils.getLocation(arena, ConfigField.RED_SPAWN) != null);
        ready.put(ConfigField.GREEN_SPAWN, ConfigUtils.getLocation(arena, ConfigField.GREEN_SPAWN) != null);
        ready.put(ConfigField.BLUE_SPAWN, ConfigUtils.getLocation(arena, ConfigField.BLUE_SPAWN) != null);
        ready.put(ConfigField.YELLOW_SPAWN, ConfigUtils.getLocation(arena, ConfigField.YELLOW_SPAWN) != null);
        ready.put(ConfigField.RED_AREA, ConfigUtils.getArea(arena, ConfigField.RED_AREA) != null);
        ready.put(ConfigField.GREEN_AREA, ConfigUtils.getArea(arena, ConfigField.GREEN_AREA) != null);
        ready.put(ConfigField.BLUE_AREA, ConfigUtils.getArea(arena, ConfigField.BLUE_AREA) != null);
        ready.put(ConfigField.YELLOW_AREA, ConfigUtils.getArea(arena, ConfigField.YELLOW_AREA) != null);

        return ready;
    }


    public static boolean checkIfValid(Player player, String[] args, String permission, String messagesPath, int argsLength, boolean requireInServer) {
        SheepQuest plugin = SheepQuest.getInstance();


        if (!player.hasPermission(permission)) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return false;
        }
        if (args.length != argsLength) {
            player.sendMessage(plugin.getMessage(messagesPath + "-usage"));
            return false;
        }

        if (argsLength > 1) {
            if (!ConfigUtils.getArenas().contains(args[1])) {
                player.sendMessage(plugin.getMessage("general.arena-invalid"));
                return false;
            }

            if (plugin.getArena(args[1]) == null) {
                player.sendMessage(plugin.getMessage("bungee.arena-offline").replace("%arena%", args[1]));
                return false;
            }

            return !requireInServer || checkServer(player, args[1]);
        }

        return true;
    }

    public static boolean checkServer(Player player, String arena) {
        SheepQuest plugin = SheepQuest.getInstance();
        if (plugin.isBungee()) {
            if (ConfigUtils.getString(arena, ConfigField.SERVER).equals(plugin.getServerName())) {
                return true;
            } else {
                player.sendMessage(plugin.getMessage("bungee.not-on-server"));
                return false;
            }
        } else {
            return true;
        }
    }

    public static ConfigField getTeamSpawn(String team) {
        ConfigField configField = null;
        switch (team) {
            case "red": {
                configField = ConfigField.RED_SPAWN;
                break;
            }
            case "green": {
                configField = ConfigField.GREEN_SPAWN;
                break;
            }
            case "blue": {
                configField = ConfigField.BLUE_SPAWN;
                break;
            }
            case "yellow": {
                configField = ConfigField.YELLOW_SPAWN;
                break;
            }
        }
        return configField;
    }

    public static ConfigField getTeamArea(String team) {
        ConfigField configField = null;
        switch (team) {
            case "red": {
                configField = ConfigField.RED_AREA;
                break;
            }
            case "green": {
                configField = ConfigField.GREEN_AREA;
                break;
            }
            case "blue": {
                configField = ConfigField.BLUE_AREA;
                break;
            }
            case "yellow": {
                configField = ConfigField.YELLOW_AREA;
                break;
            }
        }
        return configField;
    }

    public static void removeSheep(Player player) {
        SheepQuest plugin = SheepQuest.getInstance();

        if (player.getPassenger() != null) {
            if (player.getPassenger().getPassenger() != null) {
                if (player.getPassenger().getPassenger().getPassenger() != null) {

                    player.getPassenger().getPassenger().eject();

                }
                player.getPassenger().eject();
            }
            player.eject();

            player.playSound(player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.sheep-drop")).get().parseSound(), 100, 1);
            player.removePotionEffect(PotionEffectType.SLOW);
        }
    }

    public static boolean isInRegion(Entity entity, String arena, Team team) {
        if (team.equals(Team.NONE)) {
            return entity.getLocation().distance(ConfigUtils.getLocation(arena, ConfigField.SHEEP_SPAWN)) < 10;
        }

        String teamString = team.getCode();

        Location[] area = ConfigUtils.getArea(arena, getTeamArea(teamString));

        Location min = area[0];
        Location max = area[1];
        World w = min.getWorld();

        double b1x = min.getX();
        double b2x = max.getX();
        double b1y = min.getY();
        double b2y = max.getY();
        double b1z = min.getZ();
        double b2z = max.getZ();

        double bxmin;
        double bymin;
        double bzmin;

        double bxmax;
        double bymax;
        double bzmax;

        if (b1x < b2x) {
            bxmin = b1x - 1;
            bxmax = b2x + 1;
        } else {
            bxmin = b2x - 1;
            bxmax = b1x + 1;
        }

        if (b1y < b2y) {
            bymin = b1y - 1;
            bymax = b2y + 1;
        } else {
            bymin = b2y - 1;
            bymax = b1y + 1;
        }

        if (b1z < b2z) {
            bzmin = b1z - 1;
            bzmax = b2z + 1;
        } else {
            bzmin = b2z - 1;
            bzmax = b1z + 1;
        }


        boolean inRegion = entity.getLocation().getX() >= bxmin && entity.getLocation().getX() <= bxmax && entity.getWorld().equals(w);

        if (!(entity.getLocation().getY() >= bymin && entity.getLocation().getY() <= bymax && entity.getWorld().equals(w))) {
            inRegion = false;
        }
        if (!(entity.getLocation().getZ() >= bzmin && entity.getLocation().getZ() <= bzmax && entity.getWorld().equals(w))) {
            inRegion = false;
        }

        return inRegion;
    }

    public static ItemStack setGlassColor(Arena arena) {
        SheepQuest plugin = SheepQuest.getInstance();

        ItemStack material = null;
        if (arena == null) {
            material = XMaterial.matchXMaterial(plugin.getConfig().getString("sign-colors.offline")).get().parseItem();
        } else {
            String arenaName = arena.getName();
            if (!ConfigUtils.getBoolean(arenaName, ConfigField.ACTIVE)) {
                material = XMaterial.matchXMaterial(plugin.getConfig().getString("sign-colors.inactive")).get().parseItem();
            } else if (arena.getState().equals(GameState.WAITING)) {
                material = XMaterial.matchXMaterial(plugin.getConfig().getString("sign-colors.waiting")).get().parseItem();
            } else if (arena.getState().equals(GameState.STARTING)) {
                material = XMaterial.matchXMaterial(plugin.getConfig().getString("sign-colors.starting")).get().parseItem();
            } else if (arena.getState().equals(GameState.GAME)) {
                material = XMaterial.matchXMaterial(plugin.getConfig().getString("sign-colors.ingame")).get().parseItem();
            } else if (arena.getState().equals(GameState.ENDING)) {
                material = XMaterial.matchXMaterial(plugin.getConfig().getString("sign-colors.ending")).get().parseItem();
            }
        }
        return material;
    }

    public static List<Team> getTeams() {
        List<Team> teams = new ArrayList<>(Arrays.asList(Team.values()));
        teams.remove(Team.NONE);
        return teams;
    }

}