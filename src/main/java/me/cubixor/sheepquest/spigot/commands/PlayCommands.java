package me.cubixor.sheepquest.spigot.commands;

import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.api.Particles;
import me.cubixor.sheepquest.spigot.api.Sounds;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.config.StatsField;
import me.cubixor.sheepquest.spigot.config.StatsUtils;
import me.cubixor.sheepquest.spigot.game.*;
import me.cubixor.sheepquest.spigot.game.kits.KitType;
import me.cubixor.sheepquest.spigot.game.kits.Kits;
import me.cubixor.sheepquest.spigot.gameInfo.*;
import me.cubixor.sheepquest.spigot.socket.BungeeUtils;
import me.cubixor.sheepquest.spigot.socket.SocketClientSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class PlayCommands {

    public final SheepQuest plugin;

    public PlayCommands() {
        plugin = SheepQuest.getInstance();
    }

    public void join(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (!Utils.checkIfValid(player, args, "sheepquest.play.join", "game.arena-join", 2, false)) {
                return;
            }

            String arenaString = args[1];

            if (ConfigUtils.getBoolean(arenaString, ConfigField.VIP) && !player.hasPermission("sheepquest.vip")) {
                player.sendMessage(plugin.getMessage("game.arena-join-vip"));
                return;
            }

            putInSpecifiedArena(player, arenaString, true);
        });
    }

    public void putInSpecifiedArena(Player player, String arenaString, boolean firstJoin) {
        if (!plugin.isBungee()) {
            LocalArena localArena = plugin.getLocalArenas().get(arenaString);
            if (checkArenaJoin(player, localArena)) {
                putInLocalArena(player, localArena);
            }
        } else {
            if (plugin.getLocalArenas().containsKey(arenaString)) {
                LocalArena localArena = plugin.getLocalArenas().get(arenaString);
                if (checkArenaJoin(player, localArena)) {
                    putInLocalArena(player, localArena);
                    Arena arena = new Arena(localArena.getName(), localArena.getServer(), localArena.getState(), localArena.getPlayers());
                    plugin.getSocketClient().getSender().sendJoinPacket(arena, player.getName(), firstJoin, true);
                }
            } else {
                Arena arena = plugin.getArenas().get(arenaString);
                if (checkArenaJoin(player, arena)) {
                    plugin.getSocketClient().getSender().sendJoinPacket(arena, player.getName(), firstJoin, false);
                }
            }
        }

    }

    private boolean checkArenaJoin(Player player, Arena arena) {
        String arenaString = arena.getName();

        if (Utils.isInArena(player)) {
            player.sendMessage(plugin.getMessage("game.arena-join-already-in-game").replace("%arena%", arenaString));
            return false;
        }

        if (!ConfigUtils.getBoolean(arenaString, ConfigField.ACTIVE)) {
            player.sendMessage(plugin.getMessage("game.arena-join-not-active").replace("%arena%", arenaString));
            return false;
        }

        if (arena.getState().equals(GameState.GAME) || arena.getState().equals(GameState.ENDING)) {
            player.sendMessage(plugin.getMessage("game.arena-join-arena-in-game").replace("%arena%", arenaString));
            return false;
        }

        if (arena.getPlayers().size() >= ConfigUtils.getInt(arenaString, ConfigField.MAX_PLAYERS)) {
            player.sendMessage(plugin.getMessage("game.arena-join-arena-full").replace("%arena%", arenaString));
            return false;
        }

        return true;
    }

    public void putInLocalArena(Player player, LocalArena localArena) {
        String arenaString = localArena.getName();
        int min = ConfigUtils.getInt(arenaString, ConfigField.MIN_PLAYERS);
        int max = ConfigUtils.getInt(arenaString, ConfigField.MAX_PLAYERS);
        Location waitingLobby = ConfigUtils.getLocation(arenaString, ConfigField.WAITING_LOBBY);

        Bukkit.getScheduler().runTask(plugin, () -> {
            if (!plugin.getPlayerInfo().containsKey(player)) {
                return;
            }
            if (localArena.getPlayers().contains(player.getName())) {
                player.sendMessage(plugin.getMessage("game.arena-join-already-in-game").replace("%arena%", arenaString));
                return;
            }

            if (localArena.getPlayers().size() >= max) {
                player.sendMessage(plugin.getMessage("game.arena-join-arena-full").replace("%arena%", arenaString));
                new BungeeUtils().sendBackToServer(player);
                return;
            }

            if (localArena.getState().equals(GameState.GAME) || localArena.getState().equals(GameState.ENDING)) {
                player.sendMessage(plugin.getMessage("game.arena-join-arena-in-game").replace("%arena%", arenaString));
                new BungeeUtils().sendBackToServer(player);
                return;
            }


            localArena.getPlayers().add(player.getName());
            localArena.getPlayerTeam().put(player, Team.NONE);
            localArena.getPlayerKit().put(player, KitType.STANDARD);
            localArena.getTeamBossBars().get(Team.NONE).addPlayer(player);
            new Scoreboards().createScoreboard(localArena, player);
            int count = localArena.getPlayers().size() - 1;

            PlayerData playerData = new PlayerData(
                    player.getInventory().getContents(), player.getInventory().getArmorContents(), player.getLocation(), player.getActivePotionEffects(),
                    player.getGameMode(), player.getHealth(), player.getFoodLevel(), player.getExp(), player.getLevel(), player.getAllowFlight());
            localArena.getPlayerData().put(player, playerData);
            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);
            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(player.getMaxHealth());
            player.setFoodLevel(20);
            player.setExp(0);
            player.setLevel(0);
            player.setAllowFlight(false);
            player.setFlying(false);
            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }
            player.teleport(waitingLobby);
            if (plugin.getConfig().getBoolean("allow-team-choosing")) {
                player.getInventory().setItem(plugin.getItems().getTeamItemSlot(), plugin.getItems().getTeamItem());
            }
            if (Kits.useKits()) {
                player.getInventory().setItem(plugin.getItems().getKitsItemSlot(), plugin.getItems().getKitsItem());
            }
            player.getInventory().setItem(plugin.getItems().getLeaveItemSlot(), plugin.getItems().getLeaveItem());
            player.getInventory().setHeldItemSlot(4);
            count++;

            String maxString = Integer.toString(max);
            String countString = Integer.toString(count);

            Sounds.playSound(localArena, player.getLocation(), "join");
            Particles.spawnParticle(localArena, waitingLobby.add(0, 1.5, 0), "join");
            for (Player p : localArena.getPlayerTeam().keySet()) {
                p.sendMessage(plugin.getMessage("game.arena-join-success").replace("%player%", player.getName()).replace("%count%", countString).replace("%max%", maxString));
            }

            if (count >= min) {
                if (localArena.getTimer() == -1) {
                    localArena.setTimer(plugin.getConfig().getInt("waiting-time"));
                    localArena.setState(GameState.STARTING);

                    for (Player p : localArena.getPlayerTeam().keySet()) {
                        p.setLevel(localArena.getTimer());
                        p.setExp(0.94F);
                    }
                    new Countdown().time(arenaString);
                }
                if (count >= max) {
                    localArena.setTimer(plugin.getConfig().getInt("full-waiting-time"));
                    for (Player p : localArena.getPlayerTeam().keySet()) {
                        p.sendMessage(plugin.getMessage("game.full-countdown").replace("%time%", Integer.toString(localArena.getTimer())));
                    }
                }
            } else {
                Scoreboards scoreboards = new Scoreboards();
                for (Player p : localArena.getPlayerTeam().keySet()) {
                    p.setScoreboard(scoreboards.getWaitingScoreboard(localArena, p));
                }
            }

            new Signs().updateSigns(arenaString);

            if (plugin.isBungee()) {
                Arena arena = new Arena(localArena.getName(), localArena.getServer(), localArena.getState(), localArena.getPlayers());
                plugin.getSocketClient().getSender().sendUpdateArenaPacket(arena);
            }
        });
    }

    public void quickJoin(Player player) {
        if (!player.hasPermission("sheepquest.play.quickjoin")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        if (Utils.getLocalArena(player) != null) {
            player.sendMessage(plugin.getMessage("game.arena-join-already-in-game").replace("%arena%", (Utils.getLocalArena(player)).getName()));
            return;
        }
        putInRandomArena(player, true);
    }

    public void putInRandomArena(Player player, boolean firstJoin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            boolean found = false;
            HashMap<String, Integer> playersCount = new HashMap<>();
            List<String> arenas = new ArrayList<>(ConfigUtils.getArenas());
            if (!arenas.isEmpty()) {
                for (String arenaString : arenas) {
                    Arena arena = plugin.getArena(arenaString);
                    if (arena != null) {
                        if (ConfigUtils.getBoolean(arenaString, ConfigField.ACTIVE)) {
                            if (arena.getState().equals(GameState.WAITING) || arena.getState().equals(GameState.STARTING)) {
                                if (arena.getPlayers().size() < ConfigUtils.getInt(arenaString, ConfigField.MAX_PLAYERS)) {
                                    if (!ConfigUtils.getBoolean(arenaString, ConfigField.VIP) || player.hasPermission("sheepquest.vip")) {
                                        playersCount.put(arenaString, arena.getPlayers().size());
                                        found = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (found) {
                LinkedHashMap<String, Integer> maxPlayers = new LinkedHashMap<>();

                playersCount.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .forEachOrdered(x -> maxPlayers.put(x.getKey(), x.getValue()));

                String toJoin = (new ArrayList<>(maxPlayers.keySet())).get(0);

                putInSpecifiedArena(player, toJoin, firstJoin);
            }

            if (!found) {
                player.sendMessage(plugin.getMessage("game.quick-join-no-games-found"));
            }
        });
    }


    public void leave(Player player) {
        if (!player.hasPermission("sheepquest.play.leave")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        if (Utils.getLocalArena(player) == null) {
            player.sendMessage(plugin.getMessage("game.arena-leave-not-in-game"));
            return;
        }

        LocalArena localArena = Utils.getLocalArena(player);

        sendKickMessage(player, localArena);
        kickFromLocalArena(player, localArena, false, false);
    }

    public void sendKickMessage(Player player, LocalArena localArena) {
        List<Player> players = new ArrayList<>(localArena.getPlayerTeam().keySet());
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String arenaString = localArena.getName();
            String count = Integer.toString(localArena.getPlayers().contains(player.getName()) ? localArena.getPlayers().size() - 1 : localArena.getPlayers().size());
            String max = Integer.toString(ConfigUtils.getInt(arenaString, ConfigField.MAX_PLAYERS));
            for (Player p : players) {
                p.sendMessage(plugin.getMessage("game.arena-leave-success").replace("%player%", player.getName()).replace("%count%", count).replace("%max%", max));
            }
        });
    }


    public void kickFromLocalArena(Player player, LocalArena localArena, boolean reset, boolean end) {
        Bukkit.getScheduler().runTask(plugin, () -> kickFromLocalArenaSynchronized(player, localArena, reset, end));
    }

    public void kickFromLocalArenaSynchronized(Player player, LocalArena localArena, boolean reset, boolean end) {
        if (!localArena.getPlayers().contains(player.getName())) {
            return;
        }
        String arenaString = localArena.getName();
        localArena.getPlayers().remove(player.getName());
        PlayerData playerData = localArena.getPlayerData().get(player);
        player.getInventory().setContents(playerData.getInventory());
        player.getInventory().setArmorContents(playerData.getArmorContents());
        player.updateInventory();
        player.removePotionEffect(PotionEffectType.SLOW);
        for (PotionEffect potionEffect : playerData.getPotionEffects()) {
            player.addPotionEffect(potionEffect);
        }
        ActionBar.clearActionBar(player);
        player.setGameMode(playerData.getGameMode());
        player.setHealth(playerData.getHealth());
        player.setFoodLevel(playerData.getFood());
        player.setExp(playerData.getExp());
        player.setLevel(playerData.getLevel());
        player.setAllowFlight(playerData.isFly());
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        Titles.clearTitle(player);
        localArena.getPlayerData().remove(player);
        localArena.getPlayerKit().remove(player);
        localArena.getPlayerScoreboards().remove(player);

        Sounds.playSound(localArena, player.getLocation(), "leave");
        Particles.spawnParticle(localArena, player.getLocation().add(0, 1.5, 0), "leave");

        if (plugin.isEnabled()) {
            if (localArena.getState().equals(GameState.ENDING)) {
                StatsUtils.addStats(player.getName(), StatsField.PLAYTIME, plugin.getConfig().getInt("game-time"));
            } else if (localArena.getState().equals(GameState.GAME)) {
                int time = plugin.getConfig().getInt("game-time") - localArena.getTimer();
                StatsUtils.addStats(player.getName(), StatsField.PLAYTIME, time);
            }
            plugin.savePlayers();
        }

        boolean resetSent = false;

        if (localArena.getPlayerTeam().containsKey(player)) {
            if (localArena.getRespawnTimer().containsKey(player)) {
                for (Player p : localArena.getPlayerTeam().keySet()) {
                    p.showPlayer(player);
                }
                localArena.getRespawnTimer().remove(player);
            }
            for (Player p : localArena.getRespawnTimer().keySet()) {
                player.showPlayer(p);
            }

            localArena.getTeamBossBars().get(localArena.getPlayerTeam().get(player)).removePlayer(player);
            Utils.removeFromScoreboard(localArena, localArena.getPlayerTeam().get(player).getCode(), player.getName());
            if (localArena.getPlayerStats().get(player) != null && localArena.getPlayerStats().get(player).getSheepCooldown() != null) {
                localArena.getPlayerStats().get(player).getSheepCooldown().cancel();
            }
            localArena.getPlayerStats().remove(player);
            localArena.getPlayerTeam().remove(player);

            int count = localArena.getPlayerTeam().keySet().size();

            if (!reset && localArena.getState().equals(GameState.STARTING)) {
                if (plugin.isEnabled()) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        int minPlayers = ConfigUtils.getInt(arenaString, ConfigField.MIN_PLAYERS);
                        if (count < minPlayers) {
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                localArena.setState(GameState.WAITING);
                                localArena.setTimer(-1);

                                Scoreboards scoreboards = new Scoreboards();
                                for (Player p : localArena.getPlayerTeam().keySet()) {
                                    p.setScoreboard(scoreboards.getWaitingScoreboard(localArena, p));
                                    p.sendMessage(plugin.getMessage("game.start-cancelled"));
                                    p.setLevel(0);
                                    p.setExp(0);
                                    Titles.clearTitle(p);
                                }

                                new Teams().menuUpdate(localArena);
                                new Signs().updateSigns(arenaString);
                                Arena arena = new Arena(localArena.getName(), localArena.getServer(), localArena.getState(), localArena.getPlayers());
                                SocketClientSender socketClientSender = plugin.getSocketClient().getSender();
                                socketClientSender.sendUpdateArenaPacket(arena);

                            });
                        }
                    });
                }
            } else if (localArena.getState().equals(GameState.GAME) && count == 0) {
                Utils.removeSheep(player);
                if (!reset && plugin.isEnabled()) {
                    new End().resetArena(localArena, end);
                    resetSent = true;
                }
            } else if (localArena.getState().equals(GameState.GAME) && count == 1 && plugin.getConfig().getBoolean("stop-game-one-player")) {
                Utils.removeSheep(player);
                if (!reset && plugin.isEnabled()) {
                    new ArrayList<>(localArena.getPlayerTeam().keySet()).get(0).sendMessage(plugin.getMessage("game.stopped-one-player"));
                    new End().resetArena(localArena, end);
                    resetSent = true;
                }

            }
        }

        if (plugin.getConfig().getBoolean("use-main-lobby")) {
            player.teleport(ConfigUtils.getLocation(arenaString, ConfigField.MAIN_LOBBY));
        } else {
            player.teleport(playerData.getLocation());
        }
        Sounds.playSound(player, player.getLocation(), "leave");

        if (plugin.isEnabled()) {
            new Teams().menuUpdate(localArena);
            new Signs().updateSigns(arenaString);
        }

        if (plugin.isBungee()) {
            if (!reset && !resetSent && plugin.isEnabled()) {
                Arena arena = new Arena(localArena.getName(), localArena.getServer(), localArena.getState(), localArena.getPlayers());
                SocketClientSender socketClientSender = plugin.getSocketClient().getSender();
                socketClientSender.sendUpdateArenaPacket(arena);
            }
            if (!(end && plugin.getConfig().getBoolean("auto-join-on-end"))) {
                new BungeeUtils().sendBackToServer(player);
            }
        }
    }

    public void arenaList(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!player.hasPermission("sheepquest.play.list")) {
                player.sendMessage(plugin.getMessage("general.no-permission"));
                return;
            }
            if (ConfigUtils.getArenas().isEmpty()) {
                player.sendMessage(plugin.getMessage("other.list-empty"));
                return;
            }

            LinkedList<TextComponent> msg = new LinkedList<>();

            for (String arenaString : ConfigUtils.getArenas()) {

                Arena arena = plugin.getArena(arenaString);

                String count;
                if (arena != null) {
                    count = Integer.toString(arena.getPlayers().size());
                } else {
                    count = "0";
                }

                String gameState = Utils.getStringState(arena);
                String max = Integer.toString(ConfigUtils.getInt(arenaString, ConfigField.MAX_PLAYERS));
                String vip = ConfigUtils.getBoolean(arenaString, ConfigField.VIP) ? plugin.getMessage("general.vip-prefix") : "";

                TextComponent message = new TextComponent(plugin.getMessage("other.list-arena").replace("%arena%", arenaString).replace("%count%", count).replace("%max%", max).replace("%state%", gameState).replace("%?vip?%", vip));
                message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(plugin.getMessage("other.list-hover")).create()));
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sq join " + arenaString));
                msg.add(message);
            }

            for (String s : plugin.getMessageList("other.list-header")) {
                player.sendMessage(s);
            }

            for (TextComponent tc : msg) {
                player.spigot().sendMessage(tc);
            }

            for (String s : plugin.getMessageList("other.list-footer")) {
                player.sendMessage(s);
            }
        });
    }

    public void stats(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
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

            if (!player.hasPermission("sheepquest.play.stats.others") && !target.equalsIgnoreCase(player.getName())) {
                player.sendMessage(plugin.getMessage("general.no-permission"));
                return;
            }

            if (!StatsUtils.getPlayers().contains(target)) {
                player.sendMessage(plugin.getMessage("general.invalid-player"));
                return;
            }

            sendStats(player, target);
        });
    }

    private void sendStats(Player player, String target) {
        int wins = StatsUtils.getSavedStats(target, StatsField.WINS);
        int looses = StatsUtils.getSavedStats(target, StatsField.LOOSES);
        int gamesPlayed = StatsUtils.getSavedStats(target, StatsField.GAMES_PLAYED);
        int kills = StatsUtils.getSavedStats(target, StatsField.KILLS);
        int deaths = StatsUtils.getSavedStats(target, StatsField.DEATHS);
        int sheepTaken = StatsUtils.getSavedStats(target, StatsField.SHEEP_TAKEN);
        int bonusSheepTaken = StatsUtils.getSavedStats(target, StatsField.BONUS_SHEEP_TAKEN);
        int playtime = StatsUtils.getSavedStats(target, StatsField.PLAYTIME);

        List<String> stats = new ArrayList<>(plugin.getMessageList("other.stats"));
        String statsString = String.join(",", stats);

        statsString = statsString.replace("%wins%", Integer.toString(wins));
        statsString = statsString.replace("%looses%", Integer.toString(looses));
        statsString = statsString.replace("%games%", Integer.toString(gamesPlayed));
        statsString = statsString.replace("%kills%", Integer.toString(kills));
        statsString = statsString.replace("%deaths%", Integer.toString(deaths));
        statsString = statsString.replace("%sheep%", Integer.toString(sheepTaken));
        statsString = statsString.replace("%bonus-sheep%", Integer.toString(bonusSheepTaken));
        statsString = statsString.replace("%playtime%", new StatsUtils().convertPlaytime(playtime));
        statsString = statsString.replace("%player%", target);

        List<String> statsReplaced = new ArrayList<>(Arrays.asList(statsString.split(",")));

        for (String s : statsReplaced) {
            player.sendMessage(s);
        }

    }
}