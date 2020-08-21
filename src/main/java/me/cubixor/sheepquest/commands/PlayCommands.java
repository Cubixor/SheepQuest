package me.cubixor.sheepquest.commands;

import me.cubixor.sheepquest.*;
import me.cubixor.sheepquest.game.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class PlayCommands {

    public final SheepQuest plugin;

    public PlayCommands(SheepQuest s) {
        plugin = s;
    }

    public void join(Player player, String[] args) {
        Utils utils = new Utils(plugin);
        if (!utils.checkIfValid(player, args, "sheepquest.play.join", "game.arena-join", 2)) {
            return;
        }

        String arenaString = args[1];
        Arena arena = plugin.arenas.get(arenaString);

        if (plugin.getConfig().getStringList("vip-arenas").contains(arenaString) && !player.hasPermission("sheepquest.vip")) {
            player.sendMessage(plugin.getMessage("game.arena-join-vip"));
            return;
        }

        putInArena(player, arena);

    }

    public void putInArena(Player player, Arena arena) {
        Utils utils = new Utils(plugin);

        int count = arena.playerTeam.keySet().size();
        String arenaString = utils.getArenaString(arena);

        if (utils.getArena(player) != null) {
            player.sendMessage(plugin.getMessage("game.arena-join-already-in-game").replace("%arena%", arenaString));
            return;
        }

        if (!plugin.getArenasConfig().getBoolean("Arenas." + arenaString + ".active")) {
            player.sendMessage(plugin.getMessage("game.arena-join-not-active").replace("%arena%", arenaString));
            return;
        }

        if (arena.state.equals(GameState.GAME) || arena.state.equals(GameState.ENDING)) {
            player.sendMessage(plugin.getMessage("game.arena-join-arena-in-game").replace("%arena%", arenaString));
            return;
        }

        if (count >= plugin.getArenasConfig().getInt("Arenas." + arenaString + ".max-players")) {
            player.sendMessage(plugin.getMessage("game.arena-join-arena-full").replace("%arena%", arenaString));
            return;
        }

        arena.playerTeam.put(player, Team.NONE);
        arena.teamBossBars.get(Team.NONE).addPlayer(player);

        PlayerData playerData = new PlayerData(player.getInventory().getContents(), player.getLocation(), player.getActivePotionEffects(), player.getGameMode(), player.getHealth(), player.getFoodLevel(), player.getExp(), player.getLevel());
        plugin.addPlayerData(player, playerData);

        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        player.teleport((Location) plugin.getArenasConfig().get("Arenas." + arenaString + ".waiting-lobby"));
        player.getInventory().setItem(plugin.items.teamItemSlot, plugin.items.teamItem);
        player.getInventory().setItem(plugin.items.leaveItemSlot, plugin.items.leaveItem);
        new WaitingTips(plugin).playerTips(player);

        count++;
        String max = Integer.toString(plugin.getArenasConfig().getInt("Arenas." + arenaString + ".max-players"));
        String countString = Integer.toString(count);


        if (count >= plugin.getArenasConfig().getInt("Arenas." + arenaString + ".min-players") && arena.timer == -1) {
            arena.timer = plugin.getConfig().getInt("waiting-time");
            arena.state = GameState.STARTING;

            for (Player p : arena.playerTeam.keySet()) {
                p.setLevel(arena.timer);
                p.setExp(0.94F);
            }
            new Countdown(plugin).time(arenaString);

        } else {
            Scoreboards scoreboards = new Scoreboards(plugin);
            for (Player p : arena.playerTeam.keySet()) {
                p.setScoreboard(scoreboards.getWaitingScoreboard(arena));
            }
        }

        for (Player p : arena.playerTeam.keySet()) {
            p.sendMessage(plugin.getMessage("game.arena-join-success").replace("%player%", player.getName()).replace("%count%", countString).replace("%max%", max));
        }

        new Signs(plugin).updateSigns(arena);
    }

    public void quickJoin(Player player) {
        Utils utils = new Utils(plugin);

        if (!player.hasPermission("sheepquest.play.quickjoin")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        if (utils.getArena(player) != null) {
            player.sendMessage(plugin.getMessage("game.arena-join-already-in-game").replace("%arena%", utils.getArenaString(utils.getArena(player))));
            return;
        }
        putInRandomArena(player);
    }

    public void putInRandomArena(Player player) {
        Utils utils = new Utils(plugin);

        boolean found = false;
        if (plugin.getArenasConfig().getConfigurationSection("Arenas") != null) {
            HashMap<String, Integer> playersCount = new HashMap<>();
            for (String s : plugin.getArenasConfig().getConfigurationSection("Arenas").getKeys(false)) {
                Arena arena = plugin.arenas.get(s);
                if (!utils.checkIfReady(s).containsValue(false)) {
                    if (plugin.getArenasConfig().getBoolean("Arenas." + s + ".active")) {
                        if (arena.state.equals(GameState.WAITING) || arena.state.equals(GameState.STARTING)) {
                            if (arena.playerTeam.keySet().size() < plugin.getArenasConfig().getInt("Arenas." + utils.getArenaString(arena) + ".max-players")) {
                                if (!plugin.getConfig().getStringList("vip-arenas").contains(utils.getArenaString(arena)) || (plugin.getConfig().getStringList("vip-arenas").contains(utils.getArenaString(arena)) && player.hasPermission("sheepquest.vip"))) {
                                    playersCount.put(s, arena.playerTeam.keySet().size());
                                    found = true;
                                }
                            }
                        }
                    }
                }
            }
            if (found) {
                LinkedHashMap<String, Integer> maxPlayers = utils.sortByValue(playersCount);
                String toJoin = (new ArrayList<>(maxPlayers.keySet())).get(maxPlayers.size() - 1);

                putInArena(player, plugin.arenas.get(toJoin));
            }
        }
        if (!found) {
            player.sendMessage(plugin.getMessage("game.quick-join-no-games-found"));
        }
    }


    public void leave(Player player) {
        Utils utils = new Utils(plugin);
        if (!player.hasPermission("sheepquest.play.leave")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        if (utils.getArena(player) == null) {
            player.sendMessage(plugin.getMessage("game.arena-leave-not-in-game"));
            return;
        }

        Arena arena = utils.getArena(player);
        String arenaString = utils.getArenaString(arena);

        sendKickMessage(player, arena);
        kickPlayer(player, arenaString);
    }

    public void sendKickMessage(Player player, Arena arena) {
        Utils utils = new Utils(plugin);
        String arenaString = utils.getArenaString(arena);
        String count = Integer.toString(arena.playerTeam.keySet().size() - 1);
        String max = Integer.toString(plugin.getArenasConfig().getInt("Arenas." + arenaString + ".max-players"));
        for (Player p : arena.playerTeam.keySet()) {
            p.sendMessage(plugin.getMessage("game.arena-leave-success").replace("%player%", player.getName()).replace("%count%", count).replace("%max%", max));
        }

    }

    public void kickPlayer(Player player, String arenaString) {
        Utils utils = new Utils(plugin);
        Arena arena = plugin.arenas.get(arenaString);

        PlayerData playerData = plugin.getPlayerData(player);

        player.getInventory().setContents(playerData.getInventory());
        player.updateInventory();
        player.removePotionEffect(PotionEffectType.SLOW);
        for (PotionEffect potionEffect : playerData.getPotionEffects()) {
            player.addPotionEffect(potionEffect);
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(" "));
        plugin.playerInfo.get(player).tipTask.cancel();
        player.setGameMode(playerData.getGameMode());
        player.setHealth(playerData.getHealth());
        player.setFoodLevel(playerData.getFood());
        player.setExp(playerData.getExp());
        player.setLevel(playerData.getLevel());
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        if (plugin.getConfig().getBoolean("use-main-lobby")) {
            player.teleport((Location) plugin.getArenasConfig().get("Arenas." + arenaString + ".main-lobby"));
        } else {
            player.teleport(playerData.getLocation());
        }
        plugin.removePlayerData(player);

        if (arena.playerTeam.containsKey(player)) {
            utils.removeBossBars(player, arena);
            if (arena.playerStats.get(player) != null && arena.playerStats.get(player).sheepCooldown != null) {
                arena.playerStats.get(player).sheepCooldown.cancel();
            }
            arena.playerStats.remove(player);
            arena.playerTeam.remove(player);

            int count = arena.playerTeam.keySet().size();

            if (arena.state.equals(GameState.STARTING) && count < plugin.getArenasConfig().getInt("Arenas." + arenaString + ".min-players")) {

                arena.state = GameState.WAITING;
                arena.timer = -1;

                Scoreboards scoreboards = new Scoreboards(plugin);
                for (Player p : arena.playerTeam.keySet()) {
                    p.setScoreboard(scoreboards.getWaitingScoreboard(arena));
                    p.sendMessage(plugin.getMessage("game.start-cancelled"));
                    p.setLevel(0);
                    p.setExp(0);
                }

            } else if (arena.state.equals(GameState.GAME) && count == 0) {
                new End(plugin).resetArena(arena);
                utils.removeSheep(player);
            }
        }

        new Teams(plugin).menuUpdate(arena);
        new Signs(plugin).updateSigns(plugin.arenas.get(arenaString));
    }


    public void arenaList(Player player) {
        Utils utils = new Utils(plugin);
        if (!player.hasPermission("sheepquest.play.list")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        if (plugin.getArenasConfig().getConfigurationSection("Arenas") == null) {
            player.sendMessage(plugin.getMessage("other.list-empty"));
            return;
        }

        for (String s : plugin.getMessageList("other.list-header")) {
            player.sendMessage(s);
        }

        for (String arenaString : plugin.getArenasConfig().getConfigurationSection("Arenas").getKeys(false)) {

            Arena arena = plugin.arenas.get(arenaString);
            String count = Integer.toString(arena.playerTeam.keySet().size());
            String max = Integer.toString(plugin.getArenasConfig().getInt("Arenas." + arenaString + ".max-players"));

            String gameState = utils.getStringState(arena);
            String vip = plugin.getConfig().getStringList("vip-arenas").contains(arenaString) ? plugin.getMessage("general.vip-prefix") : "";

            TextComponent message = new TextComponent(plugin.getMessage("other.list-arena").replace("%arena%", arenaString).replace("%count%", count).replace("%max%", max).replace("%state%", gameState).replace("%?vip?%", vip));
            TextComponent hover = new TextComponent(plugin.getMessage("other.list-hover"));
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sq join " + arenaString));
            player.spigot().sendMessage(message);
        }

        for (String s : plugin.getMessageList("other.list-footer")) {
            player.sendMessage(s);
        }

    }

    public void stats(Player player) {
        if (!player.hasPermission("sheepquest.play.stats")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        int wins = plugin.getStats().getInt("Players." + player.getName() + ".wins");
        int looses = plugin.getStats().getInt("Players." + player.getName() + ".looses");
        int gamesPlayed = plugin.getStats().getInt("Players." + player.getName() + ".games-played");
        int kills = plugin.getStats().getInt("Players." + player.getName() + ".kills");
        int deaths = plugin.getStats().getInt("Players." + player.getName() + ".deaths");
        int sheepTaken = plugin.getStats().getInt("Players." + player.getName() + ".sheep-taken");

        List<String> stats = new ArrayList<>(plugin.getMessageList("other.stats"));
        String statsString = String.join(",", stats);

        statsString = statsString.replace("%wins%", Integer.toString(wins));
        statsString = statsString.replace("%looses%", Integer.toString(looses));
        statsString = statsString.replace("%games%", Integer.toString(gamesPlayed));
        statsString = statsString.replace("%kills%", Integer.toString(kills));
        statsString = statsString.replace("%deaths%", Integer.toString(deaths));
        statsString = statsString.replace("%sheep%", Integer.toString(sheepTaken));

        List<String> statsReplaced = new ArrayList<>(Arrays.asList(statsString.split(",")));

        for (String s : statsReplaced) {
            player.sendMessage(s);
        }
    }
}
