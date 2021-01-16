package me.cubixor.sheepquest.commands;

import com.cryptomorin.xseries.XSound;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.game.*;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.GameState;
import me.cubixor.sheepquest.gameInfo.PlayerData;
import me.cubixor.sheepquest.gameInfo.Team;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class PlayCommands {

    public final SheepQuest plugin;

    public PlayCommands() {
        plugin = SheepQuest.getInstance();
    }

    public void join(Player player, String[] args) {
        if (!Utils.checkIfValid(player, args, "sheepquest.play.join", "game.arena-join", 2)) {
            return;
        }

        String arenaString = args[1];
        Arena arena = plugin.getArenas().get(arenaString);

        if (plugin.getConfig().getStringList("vip-arenas").contains(arenaString) && !player.hasPermission("sheepquest.vip")) {
            player.sendMessage(plugin.getMessage("game.arena-join-vip"));
            return;
        }

        putInArena(player, arena);

    }

    public void putInArena(Player player, Arena arena) {
        int count = arena.getPlayers().keySet().size();
        String arenaString = Utils.getArenaString(arena);

        if (Utils.getArena(player) != null) {
            player.sendMessage(plugin.getMessage("game.arena-join-already-in-game").replace("%arena%", arenaString));
            return;
        }

        if (!plugin.getArenasConfig().getBoolean("Arenas." + arenaString + ".active")) {
            player.sendMessage(plugin.getMessage("game.arena-join-not-active").replace("%arena%", arenaString));
            return;
        }

        if (arena.getState().equals(GameState.GAME) || arena.getState().equals(GameState.ENDING)) {
            player.sendMessage(plugin.getMessage("game.arena-join-arena-in-game").replace("%arena%", arenaString));
            return;
        }

        if (count >= plugin.getArenasConfig().getInt("Arenas." + arenaString + ".max-players")) {
            player.sendMessage(plugin.getMessage("game.arena-join-arena-full").replace("%arena%", arenaString));
            return;
        }

        arena.getPlayers().put(player, Team.NONE);
        arena.getTeamBossBars().get(Team.NONE).addPlayer(player);

        PlayerData playerData = new PlayerData(player.getInventory().getContents(), player.getLocation(), player.getActivePotionEffects(), player.getGameMode(), player.getHealth(), player.getFoodLevel(), player.getExp(), player.getLevel());
        plugin.getPlayerData().put(player, playerData);

        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setExp(0);
        player.setLevel(0);
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        Location waitingLobby = (Location) plugin.getArenasConfig().get("Arenas." + arenaString + ".waiting-lobby");
        player.teleport(waitingLobby);
        if (plugin.getConfig().getBoolean("allow-team-choosing")) {
            player.getInventory().setItem(plugin.getItems().getTeamItemSlot(), plugin.getItems().getTeamItem());
        }
        player.getInventory().setItem(plugin.getItems().getLeaveItemSlot(), plugin.getItems().getLeaveItem());
        new WaitingTips().playerTips(player);

        count++;
        int max = plugin.getArenasConfig().getInt("Arenas." + arenaString + ".max-players");
        String maxString = Integer.toString(max);
        String countString = Integer.toString(count);

        Utils.playSound(arena, player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.join")).get().parseSound(), 1, 1);
        waitingLobby.getWorld().spawnParticle(Particle.valueOf(plugin.getConfig().getString("particles.join")), waitingLobby.getX(), waitingLobby.getY() + 1.5, waitingLobby.getZ(), 50, 1, 1, 1, 0.1);

        for (Player p : arena.getPlayers().keySet()) {
            p.sendMessage(plugin.getMessage("game.arena-join-success").replace("%player%", player.getName()).replace("%count%", countString).replace("%max%", maxString));
        }

        if (count >= plugin.getArenasConfig().getInt("Arenas." + arenaString + ".min-players")) {
            if (arena.getTimer() == -1) {
                arena.setTimer(plugin.getConfig().getInt("waiting-time"));
                arena.setState(GameState.STARTING);

                for (Player p : arena.getPlayers().keySet()) {
                    p.setLevel(arena.getTimer());
                    p.setExp(0.94F);
                }
                new Countdown().time(arenaString);
            }
            if (count >= max) {
                arena.setTimer(plugin.getConfig().getInt("full-waiting-time"));
                for (Player p : arena.getPlayers().keySet()) {
                    p.sendMessage(plugin.getMessage("game.full-countdown").replace("%time%", Integer.toString(arena.getTimer())));
                }
            }
        } else {
            Scoreboards scoreboards = new Scoreboards();
            for (Player p : arena.getPlayers().keySet()) {
                p.setScoreboard(scoreboards.getWaitingScoreboard(arena));
            }
        }

        new Signs().updateSigns(arena);
    }

    public void quickJoin(Player player) {
        if (!player.hasPermission("sheepquest.play.quickjoin")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        if (Utils.getArena(player) != null) {
            player.sendMessage(plugin.getMessage("game.arena-join-already-in-game").replace("%arena%", Utils.getArenaString(Utils.getArena(player))));
            return;
        }
        putInRandomArena(player);
    }

    public void putInRandomArena(Player player) {
        boolean found = false;
        if (plugin.getArenasConfig().getConfigurationSection("Arenas") != null) {
            HashMap<String, Integer> playersCount = new HashMap<>();
            for (String s : plugin.getArenasConfig().getConfigurationSection("Arenas").getKeys(false)) {
                Arena arena = plugin.getArenas().get(s);
                if (!Utils.checkIfReady(s).containsValue(false)) {
                    if (plugin.getArenasConfig().getBoolean("Arenas." + s + ".active")) {
                        if (arena.getState().equals(GameState.WAITING) || arena.getState().equals(GameState.STARTING)) {
                            if (arena.getPlayers().keySet().size() < plugin.getArenasConfig().getInt("Arenas." + Utils.getArenaString(arena) + ".max-players")) {
                                if (!plugin.getConfig().getStringList("vip-arenas").contains(Utils.getArenaString(arena)) || (plugin.getConfig().getStringList("vip-arenas").contains(Utils.getArenaString(arena)) && player.hasPermission("sheepquest.vip"))) {
                                    playersCount.put(s, arena.getPlayers().keySet().size());
                                    found = true;
                                }
                            }
                        }
                    }
                }
            }
            if (found) {
                LinkedHashMap<String, Integer> maxPlayers = Utils.sortByValue(playersCount);
                String toJoin = (new ArrayList<>(maxPlayers.keySet())).get(maxPlayers.size() - 1);

                putInArena(player, plugin.getArenas().get(toJoin));
            }
        }
        if (!found) {
            player.sendMessage(plugin.getMessage("game.quick-join-no-games-found"));
        }
    }


    public void leave(Player player) {
        if (!player.hasPermission("sheepquest.play.leave")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        if (Utils.getArena(player) == null) {
            player.sendMessage(plugin.getMessage("game.arena-leave-not-in-game"));
            return;
        }

        Arena arena = Utils.getArena(player);
        String arenaString = Utils.getArenaString(arena);

        sendKickMessage(player, arena);
        kickPlayer(player, arenaString);
    }

    public void sendKickMessage(Player player, Arena arena) {
        String arenaString = Utils.getArenaString(arena);
        String count = Integer.toString(arena.getPlayers().keySet().size() - 1);
        String max = Integer.toString(plugin.getArenasConfig().getInt("Arenas." + arenaString + ".max-players"));
        for (Player p : arena.getPlayers().keySet()) {
            p.sendMessage(plugin.getMessage("game.arena-leave-success").replace("%player%", player.getName()).replace("%count%", count).replace("%max%", max));
        }

    }

    public void kickPlayer(Player player, String arenaString) {
        Arena arena = plugin.getArenas().get(arenaString);

        PlayerData playerData = plugin.getPlayerData().get(player);

        player.getInventory().setContents(playerData.getInventory());
        player.updateInventory();
        player.removePotionEffect(PotionEffectType.SLOW);
        for (PotionEffect potionEffect : playerData.getPotionEffects()) {
            player.addPotionEffect(potionEffect);
        }
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(" "));
        plugin.getPlayerInfo().get(player).getTipTask().cancel();
        player.setGameMode(playerData.getGameMode());
        player.setHealth(playerData.getHealth());
        player.setFoodLevel(playerData.getFood());
        player.setExp(playerData.getExp());
        player.setLevel(playerData.getLevel());
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        plugin.getPlayerData().remove(player);

        Utils.playSound(arena, player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.leave")).get().parseSound(), 1, 1);
        player.getWorld().spawnParticle(Particle.valueOf(plugin.getConfig().getString("particles.join")), player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ(), 50, 1, 1, 1, 0.1);


        if (arena.getPlayers().containsKey(player)) {
            arena.getTeamBossBars().get(arena.getPlayers().get(player)).removePlayer(player);
            if (arena.getPlayerStats().get(player) != null && arena.getPlayerStats().get(player).getSheepCooldown() != null) {
                arena.getPlayerStats().get(player).getSheepCooldown().cancel();
            }
            arena.getPlayerStats().remove(player);
            arena.getPlayers().remove(player);

            int count = arena.getPlayers().keySet().size();

            if (arena.getState().equals(GameState.STARTING) && count < plugin.getArenasConfig().getInt("Arenas." + arenaString + ".min-players")) {

                arena.setState(GameState.WAITING);
                arena.setTimer(-1);

                Scoreboards scoreboards = new Scoreboards();
                for (Player p : arena.getPlayers().keySet()) {
                    p.setScoreboard(scoreboards.getWaitingScoreboard(arena));
                    p.sendMessage(plugin.getMessage("game.start-cancelled"));
                    p.setLevel(0);
                    p.setExp(0);
                }

            } else if (arena.getState().equals(GameState.GAME) && count == 0) {
                Utils.removeSheep(player);
                new End().resetArena(arena);
            }
        }

        if (plugin.getConfig().getBoolean("use-main-lobby")) {
            player.teleport((Location) plugin.getArenasConfig().get("Arenas." + arenaString + ".main-lobby"));
        } else {
            player.teleport(playerData.getLocation());
        }
        player.playSound(player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.leave")).get().parseSound(), 100, 1);

        new Teams().menuUpdate(arena);
        new Signs().updateSigns(plugin.getArenas().get(arenaString));
    }


    public void arenaList(Player player) {
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

            Arena arena = plugin.getArenas().get(arenaString);
            String count = Integer.toString(arena.getPlayers().keySet().size());
            String max = Integer.toString(plugin.getArenasConfig().getInt("Arenas." + arenaString + ".max-players"));

            String gameState = Utils.getStringState(arena);
            String vip = plugin.getConfig().getStringList("vip-arenas").contains(arenaString) ? plugin.getMessage("general.vip-prefix") : "";

            TextComponent message = new TextComponent(plugin.getMessage("other.list-arena").replace("%arena%", arenaString).replace("%count%", count).replace("%max%", max).replace("%state%", gameState).replace("%?vip?%", vip));
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(plugin.getMessage("other.list-hover")).create()));
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sq join " + arenaString));
            player.spigot().sendMessage(message);
        }

        for (String s : plugin.getMessageList("other.list-footer")) {
            player.sendMessage(s);
        }

    }

    public void stats(Player player, String[] args) {
        if (!player.hasPermission("sheepquest.play.stats")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        if (args.length != 1 && args.length != 2) {
            player.sendMessage(plugin.getMessage("stats-menu.command-usage"));
        }

        if (args.length == 1) {
            sendStats(player, player.getName());
            return;
        }

        String target = args[1];

        if (plugin.getStats().getConfigurationSection("Players." + target) == null) {
            player.sendMessage(plugin.getMessage("general.invalid-player"));
            return;
        }

        sendStats(player, target);
    }

    private void sendStats(Player player, String target) {
        int wins = plugin.getStats().getInt("Players." + target + ".wins");
        int looses = plugin.getStats().getInt("Players." + target + ".looses");
        int gamesPlayed = plugin.getStats().getInt("Players." + target + ".games-played");
        int kills = plugin.getStats().getInt("Players." + target + ".kills");
        int deaths = plugin.getStats().getInt("Players." + target + ".deaths");
        int sheepTaken = plugin.getStats().getInt("Players." + target + ".sheep-taken");
        int bonusSheepTaken = plugin.getStats().getInt("Players." + target + ".bonus-sheep-taken");

        List<String> stats = new ArrayList<>(plugin.getMessageList("other.stats"));
        String statsString = String.join(",", stats);

        statsString = statsString.replace("%wins%", Integer.toString(wins));
        statsString = statsString.replace("%looses%", Integer.toString(looses));
        statsString = statsString.replace("%games%", Integer.toString(gamesPlayed));
        statsString = statsString.replace("%kills%", Integer.toString(kills));
        statsString = statsString.replace("%deaths%", Integer.toString(deaths));
        statsString = statsString.replace("%sheep%", Integer.toString(sheepTaken));
        statsString = statsString.replace("%bonus-sheep%", Integer.toString(bonusSheepTaken));

        List<String> statsReplaced = new ArrayList<>(Arrays.asList(statsString.split(",")));

        for (String s : statsReplaced) {
            player.sendMessage(s);
        }

    }
}
