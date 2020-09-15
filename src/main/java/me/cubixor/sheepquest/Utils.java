package me.cubixor.sheepquest;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Utils {

    private final SheepQuest plugin;

    public Utils(SheepQuest sq) {
        plugin = sq;
    }

    public LinkedHashMap<Team, Integer> sortTeams(HashMap<Team, Integer> hm) {
        List<Map.Entry<Team, Integer>> list = new LinkedList<>(hm.entrySet());

        Collections.sort(list, Map.Entry.comparingByValue());

        LinkedHashMap<Team, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<Team, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public LinkedHashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(hm.entrySet());

        Collections.sort(list, Map.Entry.comparingByValue());

        LinkedHashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    public ItemStack setItemStack(String materialPath, String namePath, String lorePath) {
        ItemStack itemStack = new ItemStack(Material.matchMaterial(plugin.getConfig().getString(materialPath)), 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(plugin.getMessage(namePath));
        itemMeta.setLore(plugin.getMessageList(lorePath));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack setItemStack(Material material, String namePath, String lorePath) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(plugin.getMessage(namePath));
        itemMeta.setLore(plugin.getMessageList(lorePath));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack setItemStack(Material material, String namePath) {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(plugin.getMessage(namePath));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack setItemStack(ItemStack itemStack, String namePath) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(plugin.getMessage(namePath));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public Arena getArena(Player player) {
        for (String arena : plugin.arenas.keySet()) {
            if (plugin.arenas.get(arena).playerTeam.containsKey(player)) {
                return plugin.arenas.get(arena);
            }
        }
        return null;
    }

    public String getArenaString(Arena arena) {
        for (String s : plugin.arenas.keySet()) {
            if (plugin.arenas.get(s).equals(arena)) {
                return s;
            }
        }
        return null;
    }

    public ItemStack setItemStack(Material material, String namePath, String lorePath, String toReplace, String replaceMessage) {
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

    public ItemStack setItemStack(ItemStack itemStack, String namePath, String lorePath, String toReplace, String replaceMessage) {
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


    public HashMap<Team, Integer> getTeamPlayers(Arena arena) {
        HashMap<Team, Integer> teamPlayers = new HashMap<>();
        for (Team team : Team.values()) {
            if (!team.equals(Team.NONE)) {
                teamPlayers.put(team, 0);
            }
        }

        for (Player p : arena.playerTeam.keySet()) {
            switch (arena.playerTeam.get(p)) {
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

    public String getTeamString(Team team) {
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

    public ItemStack getTeamWool(Team team) {
        Wool wool = null;
        if (team.equals(Team.RED)) {
            wool = new Wool(DyeColor.RED);
        } else if (team.equals(Team.GREEN)) {
            wool = new Wool(DyeColor.LIME);
        } else if (team.equals(Team.BLUE)) {
            wool = new Wool(DyeColor.BLUE);
        } else if (team.equals(Team.YELLOW)) {
            wool = new Wool(DyeColor.YELLOW);
        }
        return wool.toItemStack();
    }

    public Team getWoolTeam(ItemStack material) {
        Team team = null;
        if (material.getType().equals(Material.WOOL)) {
            Wool wool = (Wool) material.getData();
            if (wool.getColor().equals(DyeColor.RED)) {
                team = Team.RED;
            } else if (wool.getColor().equals(DyeColor.LIME)) {
                team = Team.GREEN;
            } else if (wool.getColor().equals(DyeColor.BLUE)) {
                team = Team.BLUE;
            } else if (wool.getColor().equals(DyeColor.YELLOW)) {
                team = Team.YELLOW;
            }
        } else {
            team = Team.NONE;
        }
        return team;
    }

    public String getStringState(Arena arena) {
        String gameState = null;
        if (!plugin.getArenasConfig().getBoolean("Arenas." + getArenaString(arena) + ".active")) {
            gameState = plugin.getMessage("general.state-inactive");
        } else if (arena.state.equals(GameState.WAITING)) {
            gameState = plugin.getMessage("general.state-waiting");
        } else if (arena.state.equals(GameState.STARTING)) {
            gameState = plugin.getMessage("general.state-starting");
        } else if (arena.state.equals(GameState.GAME)) {
            gameState = plugin.getMessage("general.state-game");
        } else if (arena.state.equals(GameState.ENDING)) {
            gameState = plugin.getMessage("general.state-ending");
        }
        return gameState;
    }

    public Color getColor(Team team) {
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

    public DyeColor getDyeColor(Team team) {
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

    public BarColor getBossBarColor(Team team) {
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

    public void removeBossBars(Player player, Arena arena) {
        for (BossBar bossBar : arena.teamBossBars.values()) {
            bossBar.removePlayer(player);
        }
    }

    public LinkedHashMap<String, Boolean> checkIfReady(String arena) {
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


    public boolean checkIfValid(Player player, String[] args, String permission, String messagesPath, int argsLength) {

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

    public void removeSheep(Player player) {
        if (player.getPassenger() != null) {
            if (player.getPassenger().getPassenger() != null) {
                if (player.getPassenger().getPassenger().getPassenger() != null) {

                    player.getPassenger().getPassenger().eject();

                }
                player.getPassenger().eject();
            }
            player.eject();

            if (plugin.passengerFix != null) {
                plugin.passengerFix.updatePassengers(player);
            }

            player.removePotionEffect(PotionEffectType.SLOW);
        }
    }

    public boolean isInRegion(Entity entity, String arena, Team team) {
        Utils utils = new Utils(plugin);

        if (team.equals(Team.NONE)) {
            return entity.getLocation().distance((Location) plugin.getArenasConfig().get("Arenas." + arena + ".sheep-spawn")) < 10;
        }

        String teamString = utils.getTeamString(team);

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

    public ItemStack setGlassColor(Arena arenaObject) {
        String arena = getArenaString(arenaObject);
        ItemStack material = null;
        if (!plugin.getArenasConfig().getBoolean("Arenas." + arena + ".active")) {
            if (plugin.below1_13) {
                material = new ItemStack(Material.STAINED_GLASS, 1, (short) 15);
            } else {
                material = new ItemStack(Material.matchMaterial(plugin.getConfig().getString("sign-colors.inactive")));
            }
        } else if (arenaObject.state.equals(GameState.WAITING)) {
            if (plugin.below1_13) {
                material = new ItemStack(Material.STAINED_GLASS, 1, (short) 5);
            } else {
                material = new ItemStack(Material.matchMaterial(plugin.getConfig().getString("sign-colors.waiting")));
            }
        } else if (arenaObject.state.equals(GameState.STARTING)) {
            if (plugin.below1_13) {
                material = new ItemStack(Material.STAINED_GLASS, 1, (short) 9);
            } else {
                material = new ItemStack(Material.matchMaterial(plugin.getConfig().getString("sign-colors.starting")));
            }
        } else if (arenaObject.state.equals(GameState.GAME)) {
            if (plugin.below1_13) {
                material = new ItemStack(Material.STAINED_GLASS, 1, (short) 14);
            } else {
                material = new ItemStack(Material.matchMaterial(plugin.getConfig().getString("sign-colors.ingame")));
            }
        } else if (arenaObject.state.equals(GameState.ENDING)) {
            if (plugin.below1_13) {
                material = new ItemStack(Material.STAINED_GLASS, 1, (short) 10);
            } else {
                material = new ItemStack(Material.matchMaterial(plugin.getConfig().getString("sign-colors.ending")));
            }
        }
        return material;
    }
}
