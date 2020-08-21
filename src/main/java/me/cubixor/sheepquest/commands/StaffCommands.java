package me.cubixor.sheepquest.commands;

import me.cubixor.sheepquest.Arena;
import me.cubixor.sheepquest.GameState;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.Utils;
import me.cubixor.sheepquest.game.End;
import me.cubixor.sheepquest.game.Signs;
import me.cubixor.sheepquest.game.Start;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StaffCommands {

    private final SheepQuest plugin;

    public StaffCommands(SheepQuest s) {
        plugin = s;
    }

    public void kick(Player player, String[] args) {
        Utils utils = new Utils(plugin);
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
                Arena playerArena = utils.getArena(p);
                player.sendMessage(plugin.getMessage("arena-moderate.kick-success").replace("%arena%", utils.getArenaString(playerArena)).replace("%player%", p.getName()));
                new PlayCommands(plugin).kickPlayer(p, utils.getArenaString(playerArena));
                p.sendMessage(plugin.getMessage("arena-moderate.kick-player").replace("%kicker%", player.getName()));
                for (Player pl : playerArena.playerTeam.keySet()) {
                    pl.sendMessage(plugin.getMessage("arena-moderate.kick-players").replace("%kicker%", player.getName().replace("%player%", p.getName())));
                }
                return;
            }
        }
        player.sendMessage(plugin.getMessage("arena-moderate.kick-player-not-playing"));
    }

    public void forceStart(Player player, String[] args) {
        Utils utils = new Utils(plugin);
        if (!utils.checkIfValid(player, args, "sheepquest.staff.start", "arena-moderate.force-start", 2)) {
            return;
        }

        if (plugin.arenas.get(args[1]).state.equals(GameState.GAME) || plugin.arenas.get(args[1]).state.equals(GameState.ENDING)) {
            player.sendMessage(plugin.getMessage("arena-moderate.force-start-already-started").replace("%arena%", args[1]));
            return;
        }

        int count = plugin.arenas.get(args[1]).playerTeam.keySet().size();

        if (count == 0) {
            player.sendMessage(plugin.getMessage("arena-moderate.force-start-no-players").replace("%arena%", args[1]));
            return;
        }

        for (Player p : plugin.arenas.get(args[1]).playerTeam.keySet()) {
            p.sendMessage(plugin.getMessage("arena-moderate.force-start-success").replace("%player%", player.getName()));
        }

        if (!plugin.arenas.get(args[1]).playerTeam.containsKey(player)) {
            player.sendMessage(plugin.getMessage("arena-moderate.force-start-success").replace("%player%", player.getName()));
        }

        Arena arena = plugin.arenas.get(args[1]);

        new Start(plugin).start(arena);

    }

    public void forceStop(Player player, String[] args) {
        Utils utils = new Utils(plugin);
        if (!utils.checkIfValid(player, args, "sheepquest.staff.stop", "arena-moderate.force-stop", 2)) {
            return;
        }

        if (plugin.arenas.get(args[1]).state.equals(GameState.WAITING) || plugin.arenas.get(args[1]).state.equals(GameState.STARTING)) {
            player.sendMessage(plugin.getMessage("arena-moderate.force-stop-not-started").replace("%arena%", args[1]));
            return;
        }

        stop(player, args[1]);
    }

    public void stop(Player player, String arena) {
        List<Player> players = new ArrayList<>(plugin.arenas.get(arena).playerTeam.keySet());
        for (Player p : players) {
            new PlayCommands(plugin).kickPlayer(p, arena);
            p.sendMessage(plugin.getMessage("arena-moderate.force-stop-success").replace("%player%", player.getName()));
        }

        if (!players.contains(player)) {
            player.sendMessage(plugin.getMessage("arena-moderate.force-stop-success").replace("%player%", player.getName()));
        }

        new End(plugin).resetArena(plugin.arenas.get(arena));
    }


    public void setActive(Player player, String[] args) {
        Utils utils = new Utils(plugin);
        if (!utils.checkIfValid(player, args, "sheepquest.staff.active", "arena-moderate.active", 3)) {
            return;
        }

        if (!args[2].equalsIgnoreCase("true") && !args[2].equalsIgnoreCase("false")) {
            player.sendMessage(plugin.getMessage("arena-moderate.active-usage"));
            return;
        }

        boolean cmdActive = Boolean.parseBoolean(args[2]);
        boolean active = plugin.getArenasConfig().getBoolean("Arenas." + args[1] + ".active");

        if (utils.checkIfReady(args[1]).containsValue(false)) {
            player.sendMessage(plugin.getMessage("arena-moderate.active-not-ready"));
            return;
        }

        if (cmdActive && active) {
            player.sendMessage(plugin.getMessage("arena-moderate.active-arena-active").replace("%arena%", args[1]));
        } else if (!cmdActive && !active) {
            player.sendMessage(plugin.getMessage("arena-moderate.active-arena-not-active").replace("%arena%", args[1]));
        } else {
            plugin.getArenasConfig().set("Arenas." + args[1] + ".active", cmdActive);
            plugin.saveArenas();
            new Signs(plugin).updateSigns(plugin.arenas.get(args[1]));
            if (cmdActive) {
                player.sendMessage(plugin.getMessage("arena-moderate.active-activate").replace("%arena%", args[1]));
            } else {
                for (Player p : plugin.arenas.get(args[1]).playerTeam.keySet()) {
                    new PlayCommands(plugin).kickPlayer(p, args[1]);
                    p.sendMessage(plugin.getMessage("arena-moderate.active-players"));
                }
                player.sendMessage(plugin.getMessage("arena-moderate.active-deactivate").replace("%arena%", args[1]));
            }
        }


    }
}
