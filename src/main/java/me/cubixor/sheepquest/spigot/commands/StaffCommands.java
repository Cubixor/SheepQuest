package me.cubixor.sheepquest.spigot.commands;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.game.End;
import me.cubixor.sheepquest.spigot.game.Signs;
import me.cubixor.sheepquest.spigot.game.Start;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.socket.SocketClientSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StaffCommands {

    private final SheepQuest plugin;

    public StaffCommands() {
        plugin = SheepQuest.getInstance();
    }

    public void kick(Player player, String[] args) {
        if (!player.hasPermission("sheepquest.staff.kick")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        if (args.length != 2) {
            player.sendMessage(plugin.getMessage("arena-moderate.kick-usage"));
            return;
        }

        String target = args[1];

        if (player.getName().equals(target)) {
            new PlayCommands().leave(player);
            return;
        }

        List<String> localPlayers = new ArrayList<>();
        for (Arena arena : plugin.getLocalArenas().values()) {
            localPlayers.addAll(arena.getPlayers());
        }

        List<String> bungeePlayers = new ArrayList<>();
        for (Arena arena : plugin.getArenas().values()) {
            bungeePlayers.addAll(arena.getPlayers());
        }

        Arena arena = Utils.getArena(target);
        if (localPlayers.contains(target)) {
            kickFromLocalArena(player.getName(), target, arena.getName());
        } else if (bungeePlayers.contains(target)) {
            new SocketClientSender().sendKickPacket(player.getName(), target, arena);
        } else {
            player.sendMessage(plugin.getMessage("arena-moderate.kick-player-not-playing").replace("%player%", target));
            return;
        }

        player.sendMessage(plugin.getMessage("arena-moderate.kick-success").replace("%arena%", arena.getName()).replace("%player%", target));

    }

    public void kickFromLocalArena(String playerName, String targetName, String arena) {
        Player targetPlayer = Bukkit.getPlayerExact(targetName);
        LocalArena localArena = plugin.getLocalArenas().get(arena);

        new PlayCommands().kickFromLocalArena(targetPlayer, localArena, false, false);
        targetPlayer.sendMessage(plugin.getMessage("arena-moderate.kick-player").replace("%kicker%", playerName));
        for (Player p : localArena.getPlayerTeam().keySet()) {
            if (!p.getName().equals(targetName) && !p.getName().equals(playerName)) {
                p.sendMessage(plugin.getMessage("arena-moderate.kick-players").replace("%kicker%", playerName).replace("%player%", targetName));
            }
        }

    }

    public void forceStart(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!Utils.checkIfValid(player, args, "sheepquest.staff.start", "arena-moderate.force-start", 2, false)) {
                return;
            }

            Arena arena = plugin.getArena(args[1]);

            if (arena.getState().equals(GameState.GAME) || arena.getState().equals(GameState.ENDING)) {
                player.sendMessage(plugin.getMessage("arena-moderate.force-start-already-started").replace("%arena%", args[1]));
                return;
            }

            if (arena.getPlayers().size() == 0) {
                player.sendMessage(plugin.getMessage("arena-moderate.force-start-no-players").replace("%arena%", args[1]));
                return;
            }

            if (!arena.getPlayers().contains(player.getName())) {
                player.sendMessage(plugin.getMessage("arena-moderate.force-start-success").replace("%player%", player.getName()));
            }

            if (plugin.getLocalArenas().containsKey(args[1])) {
                forceLocalArenaStart(player.getName(), args[1]);
            } else {
                new SocketClientSender().sendForceStartPacket(player.getName(), arena);
            }
        });
    }

    public void forceLocalArenaStart(String playerName, String arena) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            LocalArena localArena = plugin.getLocalArenas().get(arena);

            for (Player p : localArena.getPlayerTeam().keySet()) {
                p.sendMessage(plugin.getMessage("arena-moderate.force-start-success").replace("%player%", playerName));
            }

            Bukkit.getScheduler().runTask(plugin, () -> new Start().start(localArena));
        });
    }

    public void forceStop(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (!Utils.checkIfValid(player, args, "sheepquest.staff.stop", "arena-moderate.force-stop", 2, false)) {
                return;
            }

            Arena arena = plugin.getArena(args[1]);

            if (arena.getState().equals(GameState.WAITING) || arena.getState().equals(GameState.STARTING)) {
                player.sendMessage(plugin.getMessage("arena-moderate.force-stop-not-started").replace("%arena%", args[1]));
                return;
            }

            if (!arena.getPlayers().contains(player.getName())) {
                player.sendMessage(plugin.getMessage("arena-moderate.force-stop-success").replace("%player%", player.getName()));
            }

            if (plugin.getLocalArenas().containsKey(args[1])) {
                forceLocalArenaStop(player.getName(), args[1]);
            } else {
                new SocketClientSender().sendForceStopPacket(player.getName(), arena);
            }
        });
    }

    public void forceLocalArenaStop(String playerName, String arena) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            LocalArena localArena = plugin.getLocalArenas().get(arena);

            for (Player p : localArena.getPlayerTeam().keySet()) {
                p.sendMessage(plugin.getMessage("arena-moderate.force-stop-success").replace("%player%", playerName));
            }

            new End().resetArena(localArena, false);
        });
    }


    public void setActive(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!Utils.checkIfValid(player, args, "sheepquest.staff.active", "arena-moderate.active", 3, true)) {
                return;
            }

            if (!args[2].equalsIgnoreCase("true") && !args[2].equalsIgnoreCase("false")) {
                player.sendMessage(plugin.getMessage("arena-moderate.active-usage"));
                return;
            }

            boolean cmdActive = Boolean.parseBoolean(args[2]);
            boolean active = ConfigUtils.getBoolean(args[1], ConfigField.ACTIVE);

            if (Utils.checkIfReady(args[1]).containsValue(false)) {
                player.sendMessage(plugin.getMessage("arena-moderate.active-not-ready").replace("%arena%", args[1]));
                return;
            }

            if (cmdActive && active) {
                player.sendMessage(plugin.getMessage("arena-moderate.active-arena-active").replace("%arena%", args[1]));
            } else if (!cmdActive && !active) {
                player.sendMessage(plugin.getMessage("arena-moderate.active-arena-not-active").replace("%arena%", args[1]));
            } else {
                ConfigUtils.updateField(args[1], ConfigField.ACTIVE, cmdActive);

                new Signs().updateSigns(args[1]);
                if (cmdActive) {
                    player.sendMessage(plugin.getMessage("arena-moderate.active-activate").replace("%arena%", args[1]));
                } else {
                    forceLocalArenaStop(player.getName(), args[1]);
                    player.sendMessage(plugin.getMessage("arena-moderate.active-deactivate").replace("%arena%", args[1]));
                }
            }

        });
    }
}
