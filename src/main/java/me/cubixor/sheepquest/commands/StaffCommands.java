package me.cubixor.sheepquest.commands;

import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.game.End;
import me.cubixor.sheepquest.game.Signs;
import me.cubixor.sheepquest.game.Start;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.GameState;
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
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(args[1])) {
                Arena playerArena = Utils.getArena(p);
                player.sendMessage(plugin.getMessage("arena-moderate.kick-success").replace("%arena%", Utils.getArenaString(playerArena)).replace("%player%", p.getName()));
                new PlayCommands().kickPlayer(p, Utils.getArenaString(playerArena));
                p.sendMessage(plugin.getMessage("arena-moderate.kick-player").replace("%kicker%", player.getName()));
                for (Player pl : playerArena.getPlayers().keySet()) {
                    pl.sendMessage(plugin.getMessage("arena-moderate.kick-players").replace("%kicker%", player.getName().replace("%player%", p.getName())));
                }
                return;
            }
        }
        player.sendMessage(plugin.getMessage("arena-moderate.kick-player-not-playing"));
    }

    public void forceStart(Player player, String[] args) {
        if (!Utils.checkIfValid(player, args, "sheepquest.staff.start", "arena-moderate.force-start", 2)) {
            return;
        }

        if (plugin.getArenas().get(args[1]).getState().equals(GameState.GAME) || plugin.getArenas().get(args[1]).getState().equals(GameState.ENDING)) {
            player.sendMessage(plugin.getMessage("arena-moderate.force-start-already-started").replace("%arena%", args[1]));
            return;
        }

        int count = plugin.getArenas().get(args[1]).getPlayers().keySet().size();

        if (count == 0) {
            player.sendMessage(plugin.getMessage("arena-moderate.force-start-no-players").replace("%arena%", args[1]));
            return;
        }

        for (Player p : plugin.getArenas().get(args[1]).getPlayers().keySet()) {
            p.sendMessage(plugin.getMessage("arena-moderate.force-start-success").replace("%player%", player.getName()));
        }

        if (!plugin.getArenas().get(args[1]).getPlayers().containsKey(player)) {
            player.sendMessage(plugin.getMessage("arena-moderate.force-start-success").replace("%player%", player.getName()));
        }

        Arena arena = plugin.getArenas().get(args[1]);

        new Start().start(arena);

    }

    public void forceStop(Player player, String[] args) {
        if (!Utils.checkIfValid(player, args, "sheepquest.staff.stop", "arena-moderate.force-stop", 2)) {
            return;
        }

        if (plugin.getArenas().get(args[1]).getState().equals(GameState.WAITING) || plugin.getArenas().get(args[1]).getState().equals(GameState.STARTING)) {
            player.sendMessage(plugin.getMessage("arena-moderate.force-stop-not-started").replace("%arena%", args[1]));
            return;
        }

        stop(player, args[1]);
    }

    public void stop(Player player, String arena) {
        List<Player> players = new ArrayList<>(plugin.getArenas().get(arena).getPlayers().keySet());
        for (Player p : players) {
            new PlayCommands().kickPlayer(p, arena);
            p.sendMessage(plugin.getMessage("arena-moderate.force-stop-success").replace("%player%", player.getName()));
        }

        if (!players.contains(player)) {
            player.sendMessage(plugin.getMessage("arena-moderate.force-stop-success").replace("%player%", player.getName()));
        }

        new End().resetArena(plugin.getArenas().get(arena));
    }


    public void setActive(Player player, String[] args) {
        if (!Utils.checkIfValid(player, args, "sheepquest.staff.active", "arena-moderate.active", 3)) {
            return;
        }

        if (!args[2].equalsIgnoreCase("true") && !args[2].equalsIgnoreCase("false")) {
            player.sendMessage(plugin.getMessage("arena-moderate.active-usage"));
            return;
        }

        boolean cmdActive = Boolean.parseBoolean(args[2]);
        boolean active = plugin.getArenasConfig().getBoolean("Arenas." + args[1] + ".active");

        if (Utils.checkIfReady(args[1]).containsValue(false)) {
            player.sendMessage(plugin.getMessage("arena-moderate.active-not-ready").replace("%arena%", args[1]));
            return;
        }

        if (cmdActive && active) {
            player.sendMessage(plugin.getMessage("arena-moderate.active-arena-active").replace("%arena%", args[1]));
        } else if (!cmdActive && !active) {
            player.sendMessage(plugin.getMessage("arena-moderate.active-arena-not-active").replace("%arena%", args[1]));
        } else {
            plugin.getArenasConfig().set("Arenas." + args[1] + ".active", cmdActive);
            plugin.saveArenas();
            new Signs().updateSigns(plugin.getArenas().get(args[1]));
            if (cmdActive) {
                player.sendMessage(plugin.getMessage("arena-moderate.active-activate").replace("%arena%", args[1]));
            } else {
                for (Player p : plugin.getArenas().get(args[1]).getPlayers().keySet()) {
                    new PlayCommands().kickPlayer(p, args[1]);
                    p.sendMessage(plugin.getMessage("arena-moderate.active-players"));
                }
                player.sendMessage(plugin.getMessage("arena-moderate.active-deactivate").replace("%arena%", args[1]));
            }
        }


    }
}
