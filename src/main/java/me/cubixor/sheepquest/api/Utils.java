package me.cubixor.sheepquest.api;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.GameState;
import me.cubixor.sheepquest.gameInfo.Team;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
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

        Collections.sort(list, Map.Entry.comparingByValue());

        LinkedHashMap<Team, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<Team, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static LinkedHashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(hm.entrySet());

        Collections.sort(list, Map.Entry.comparingByValue());

        LinkedHashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public static ItemStack setItemStack(String materialPath, String namePath, String lorePath) {
        SheepQuest plugin = SheepQuest.getInstance();

        ItemStack itemStack = XMaterial.matchXMaterial(plugin.getConfig().getString(materialPath)).get().parseItem(true);
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

    public static Team[] getTeams() {
        return new Team[]{Team.RED, Team.GREEN, Team.BLUE, Team.YELLOW};
    }

    public static Arena getArena(Player player) {
        SheepQuest plugin = SheepQuest.getInstance();

        for (String arena : plugin.getArenas().keySet()) {
            if (plugin.getArenas().get(arena).getPlayers().containsKey(player)) {
                return plugin.getArenas().get(arena);
            }
        }
        return null;
    }

    public static String getArenaString(Arena arena) {
        SheepQuest plugin = SheepQuest.getInstance();

        for (String s : plugin.getArenas().keySet()) {
            if (plugin.getArenas().get(s).equals(arena)) {
                return s;
            }
        }
        return null;
    }

    public static HashMap<Team, Integer> getTeamPlayers(Arena arena) {
        HashMap<Team, Integer> teamPlayers = new HashMap<>();
        for (Team team : Team.values()) {
            if (!team.equals(Team.NONE)) {
                teamPlayers.put(team, 0);
            }
        }

        for (Player p : arena.getPlayers().keySet()) {
            switch (arena.getPlayers().get(p)) {
                case RED:
                    teamPlayers.replace(Team.RED, teamPlayers.get(Team.RED) + 1);
                    break;
                case GREEN:
                    teamPlayers.replace(Team.GREEN, teamPlayers.get(Team.GREEN) + 1);
                    break;
                case BLUE:
                    teamPlayers.replace(Team.BLUE, teamPlayers.get(Team.BLUE) + 1);
                    break;
                case YELLOW:
                    teamPlayers.replace(Team.YELLOW, teamPlayers.get(Team.YELLOW) + 1);
                    break;
            }

        }
        return teamPlayers;
    }

    public static String getTeamString(Team team) {
        String teamString;
        if (team.equals(Team.RED)) {
            teamString = "red";
        } else if (team.equals(Team.GREEN)) {
            teamString = "green";
        } else if (team.equals(Team.BLUE)) {
            teamString = "blue";
        } else if (team.equals(Team.YELLOW)) {
            teamString = "yellow";
        } else {
            teamString = "none";
        }
        return teamString;
    }

    public static ItemStack getTeamWool(Team team) {
        ItemStack wool = null;
        if (team.equals(Team.RED)) {
            wool = XMaterial.RED_WOOL.parseItem();
        } else if (team.equals(Team.GREEN)) {
            wool = XMaterial.LIME_WOOL.parseItem();
        } else if (team.equals(Team.BLUE)) {
            wool = XMaterial.BLUE_WOOL.parseItem();
        } else if (team.equals(Team.YELLOW)) {
            wool = XMaterial.YELLOW_WOOL.parseItem();
        }
        return wool;
    }

    public static Team getWoolTeam(ItemStack material) {
        Team team = null;
        if (material.getType().toString().contains("WOOL")) {
            if (XMaterial.matchXMaterial(material).equals(XMaterial.RED_WOOL)) {
                team = Team.RED;
            } else if (XMaterial.matchXMaterial(material).equals(XMaterial.LIME_WOOL)) {
                team = Team.GREEN;
            } else if (XMaterial.matchXMaterial(material).equals(XMaterial.BLUE_WOOL)) {
                team = Team.BLUE;
            } else if (XMaterial.matchXMaterial(material).equals(XMaterial.YELLOW_WOOL)) {
                team = Team.YELLOW;
            }
        } else {
            team = Team.NONE;
        }
        return team;
    }

    public static String getStringState(Arena arena) {
        SheepQuest plugin = SheepQuest.getInstance();

        String gameState = null;
        if (!plugin.getArenasConfig().getBoolean("Arenas." + getArenaString(arena) + ".active")) {
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

    public static Color getColor(Team team) {
        Color color = null;
        if (team.equals(Team.RED)) {
            color = Color.RED;
        } else if (team.equals(Team.GREEN)) {
            color = Color.LIME;
        } else if (team.equals(Team.BLUE)) {
            color = Color.BLUE;
        } else if (team.equals(Team.YELLOW)) {
            color = Color.YELLOW;
        }
        return color;
    }

    public static DyeColor getDyeColor(Team team) {
        DyeColor color = null;
        if (team.equals(Team.RED)) {
            color = DyeColor.RED;
        } else if (team.equals(Team.GREEN)) {
            color = DyeColor.LIME;
        } else if (team.equals(Team.BLUE)) {
            color = DyeColor.BLUE;
        } else if (team.equals(Team.YELLOW)) {
            color = DyeColor.YELLOW;
        }
        return color;
    }

    public static BarColor getBossBarColor(Team team) {
        BarColor barColor = BarColor.WHITE;
        switch (team) {
            case RED:
                barColor = BarColor.RED;
                break;
            case GREEN:
                barColor = BarColor.GREEN;
                break;
            case BLUE:
                barColor = BarColor.BLUE;
                break;
            case YELLOW:
                barColor = BarColor.YELLOW;
                break;
        }
        return barColor;
    }

    public static void removeBossBars(Player player, Arena arena) {
        for (BossBar bossBar : arena.getTeamBossBars().values()) {
            bossBar.removePlayer(player);
        }
    }

    public static void playSound(Arena arena, Location loc, Sound sound, float volume, float pitch) {
        for (Player p : arena.getPlayers().keySet()) {
            p.playSound(loc, sound, volume, pitch);
        }
    }

    public static LinkedHashMap<String, Boolean> checkIfReady(String arena) {
        SheepQuest plugin = SheepQuest.getInstance();

        LinkedHashMap<String, Boolean> ready = new LinkedHashMap<>();

        ready.put("min-players", plugin.getArenasConfig().getInt("Arenas." + arena + ".min-players") != 0);
        ready.put("max-players", plugin.getArenasConfig().getInt("Arenas." + arena + ".max-players") != 0);
        ready.put("main-lobby", plugin.getArenasConfig().get("Arenas." + arena + ".main-lobby") != null);
        ready.put("waiting-lobby", plugin.getArenasConfig().get("Arenas." + arena + ".waiting-lobby") != null);
        ready.put("sheep-spawn", plugin.getArenasConfig().get("Arenas." + arena + ".sheep-spawn") != null);
        ready.put("red-spawn", plugin.getArenasConfig().get("Arenas." + arena + ".teams.red-spawn") != null);
        ready.put("red-area", plugin.getArenasConfig().get("Arenas." + arena + ".teams-area.red.min-point") != null);
        ready.put("green-spawn", plugin.getArenasConfig().get("Arenas." + arena + ".teams.green-spawn") != null);
        ready.put("green-area", plugin.getArenasConfig().get("Arenas." + arena + ".teams-area.green.min-point") != null);
        ready.put("blue-spawn", plugin.getArenasConfig().get("Arenas." + arena + ".teams.blue-spawn") != null);
        ready.put("blue-area", plugin.getArenasConfig().get("Arenas." + arena + ".teams-area.blue.min-point") != null);
        ready.put("yellow-spawn", plugin.getArenasConfig().get("Arenas." + arena + ".teams.yellow-spawn") != null);
        ready.put("yellow-area", plugin.getArenasConfig().get("Arenas." + arena + ".teams-area.yellow.min-point") != null);

        return ready;
    }


    public static boolean checkIfValid(Player player, String[] args, String permission, String messagesPath, int argsLength) {
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
            if (plugin.getArenasConfig().get("Arenas." + args[1]) == null) {
                player.sendMessage(plugin.getMessage("general.arena-invalid"));
                return false;
            }
        }

        return true;
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

            if (plugin.isPassengerFix()) {
                new PassengerFixReflection().updatePassengers(player);
            }
            player.playSound(player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.sheep-drop")).get().parseSound(), 100, 1);
            player.removePotionEffect(PotionEffectType.SLOW);
        }
    }

    public static boolean isInRegion(Entity entity, String arena, Team team) {
        SheepQuest plugin = SheepQuest.getInstance();

        if (team.equals(Team.NONE)) {
            return entity.getLocation().distance((Location) plugin.getArenasConfig().get("Arenas." + arena + ".sheep-spawn")) < 10;
        }

        String teamString = getTeamString(team);

        Location min = (Location) plugin.getArenasConfig().get("Arenas." + arena + ".teams-area." + teamString + ".min-point");
        Location max = (Location) plugin.getArenasConfig().get("Arenas." + arena + ".teams-area." + teamString + ".max-point");
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


        boolean inRegion = true;

        if (!(entity.getLocation().getX() >= bxmin && entity.getLocation().getX() <= bxmax && entity.getWorld().equals(w))) {
            inRegion = false;
        }
        if (!(entity.getLocation().getY() >= bymin && entity.getLocation().getY() <= bymax && entity.getWorld().equals(w))) {
            inRegion = false;
        }
        if (!(entity.getLocation().getZ() >= bzmin && entity.getLocation().getZ() <= bzmax && entity.getWorld().equals(w))) {
            inRegion = false;
        }

        return inRegion;
    }

    public static ItemStack setGlassColor(Arena arenaObject) {
        SheepQuest plugin = SheepQuest.getInstance();

        String arena = getArenaString(arenaObject);
        ItemStack material = null;
        if (!plugin.getArenasConfig().getBoolean("Arenas." + arena + ".active")) {
            material = XMaterial.matchXMaterial(plugin.getConfig().getString("sign-colors.inactive")).get().parseItem();
        } else if (arenaObject.getState().equals(GameState.WAITING)) {
            material = XMaterial.matchXMaterial(plugin.getConfig().getString("sign-colors.waiting")).get().parseItem();
        } else if (arenaObject.getState().equals(GameState.STARTING)) {
            material = XMaterial.matchXMaterial(plugin.getConfig().getString("sign-colors.starting")).get().parseItem();
        } else if (arenaObject.getState().equals(GameState.GAME)) {
            material = XMaterial.matchXMaterial(plugin.getConfig().getString("sign-colors.ingame")).get().parseItem();
        } else if (arenaObject.getState().equals(GameState.ENDING)) {
            material = XMaterial.matchXMaterial(plugin.getConfig().getString("sign-colors.ending")).get().parseItem();
        }
        return material;
    }

}