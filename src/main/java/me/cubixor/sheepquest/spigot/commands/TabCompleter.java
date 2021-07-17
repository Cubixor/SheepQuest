package me.cubixor.sheepquest.spigot.commands;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabCompleter implements org.bukkit.command.TabCompleter {

    private final SheepQuest plugin;

    public TabCompleter() {
        plugin = SheepQuest.getInstance();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> result = new ArrayList<>();
        if (!(sender instanceof Player)) {
            return result;
        }
        if (!command.getName().equals("sheepquest")) {
            return result;
        }
        Player player = (Player) sender;
        switch (args.length) {
            case 1:
                if (player.hasPermission("sheepquest.play.help") && "help".startsWith(args[0])) {
                    result.add("help");
                }
                if (player.hasPermission("sheepquest.play.join") && "join".startsWith(args[0])) {
                    result.add("join");
                }
                if (player.hasPermission("sheepquest.play.leave") && "leave".startsWith(args[0])) {
                    result.add("leave");
                }
                if (player.hasPermission("sheepquest.play.quickjoin") && "quickjoin".startsWith(args[0])) {
                    result.add("quickjoin");
                }
                if (player.hasPermission("sheepquest.play.stats") && "stats".startsWith(args[0])) {
                    result.add("stats");
                }
                if (player.hasPermission("sheepquest.play.stats.menu") && "statsmenu".startsWith(args[0])) {
                    result.add("statsmenu");
                }
                if (player.hasPermission("sheepquest.play.list") && "list".startsWith(args[0])) {
                    result.add("list");
                }
                if (player.hasPermission("sheepquest.staff.start") && "forcestart".startsWith(args[0])) {
                    result.add("forcestart");
                }
                if (player.hasPermission("sheepquest.staff.stop") && "forcestop".startsWith(args[0])) {
                    result.add("forcestop");
                }
                if (player.hasPermission("sheepquest.staff.active") && "active".startsWith(args[0])) {
                    result.add("active");
                }
                if (player.hasPermission("sheepquest.setup.check") && "check".startsWith(args[0])) {
                    result.add("check");
                }
                if (player.hasPermission("sheepquest.setup.create") && "create".startsWith(args[0])) {
                    result.add("create");
                }
                if (player.hasPermission("sheepquest.setup.delete")) {
                    if ("delete".startsWith(args[0])) {
                        result.add("delete");
                    }
                    if (plugin.getPlayerInfo().get(player).getDelete() != null) {
                        if ("confirm".startsWith(args[0]))
                            result.add("confirm");
                    }
                }
                if (player.hasPermission("sheepquest.setup.setmainlobby") && "setmainlobby".startsWith(args[0])) {
                    result.add("setmainlobby");
                }
                if (player.hasPermission("sheepquest.setup.setwaitinglobby") && "setwaitinglobby".startsWith(args[0])) {
                    result.add("setwaitinglobby");
                }
                if (player.hasPermission("sheepquest.setup.setsheepspawn") && "setsheepspawn".startsWith(args[0])) {
                    result.add("setsheepspawn");
                }
                if (player.hasPermission("sheepquest.setup.setspawn") && "setspawn".startsWith(args[0])) {
                    result.add("setspawn");
                }
                if (player.hasPermission("sheepquest.setup.setteamarea") && "setteamarea".startsWith(args[0])) {
                    result.add("setteamarea");
                }
                if (player.hasPermission("sheepquest.setup.setminplayers") && "setminplayers".startsWith(args[0])) {
                    result.add("setminplayers");
                }
                if (player.hasPermission("sheepquest.setup.setmaxplayers") && "setmaxplayers".startsWith(args[0])) {
                    result.add("setmaxplayers");
                }
                if (player.hasPermission("sheepquest.setup.setvip") && "setvip".startsWith(args[0])) {
                    result.add("setvip");
                }
                if (player.hasPermission("sheepquest.setup.changeteams") && "addteam".startsWith(args[0])) {
                    result.add("addteam");
                }
                if (player.hasPermission("sheepquest.setup.changeteams") && "removeteam".startsWith(args[0])) {
                    result.add("removeteam");
                }
                if (player.hasPermission("sheepquest.setup.listteams") && "listteams".startsWith(args[0])) {
                    result.add("listteams");
                }
                if (player.hasPermission("sheepquest.setup.wand") && "wand".startsWith(args[0])) {
                    result.add("wand");
                }
                if (player.hasPermission("sheepquest.setup.joinsheep") && "spawnjoinsheep".startsWith(args[0])) {
                    result.add("spawnjoinsheep");
                }
                if (player.hasPermission("sheepquest.setup.joinsheep") && "removejoinsheep".startsWith(args[0])) {
                    result.add("removejoinsheep");
                }
                if (player.hasPermission("sheepquest.setup.reload") && "reload".startsWith(args[0])) {
                    result.add("reload");
                }
                if (player.hasPermission("sheepquest.setup.menu") && "setupmenu".startsWith(args[0])) {
                    result.add("setupmenu");
                }
                if (player.hasPermission("sheepquest.staff.menu") && "staffmenu".startsWith(args[0])) {
                    result.add("staffmenu");
                }
                if (player.hasPermission("sheepquest.play.menu") && "arenasmenu".startsWith(args[0])) {
                    result.add("arenasmenu");
                }
                if (player.hasPermission("sheepquest.staff.menu") && "playersmenu".startsWith(args[0])) {
                    result.add("playersmenu");
                }
                if (player.hasPermission("sheepquest.staff.kick") && "kick".startsWith(args[0])) {
                    result.add("kick");
                }
                if (player.hasPermission("sheepquest.staff.help") && "staff".startsWith(args[0])) {
                    result.add("staff");
                }
                if (player.hasPermission("sheepquest.setup.help") && "admin".startsWith(args[0])) {
                    result.add("admin");
                }
                break;
            case 2:
                List<String> arenasList = new ArrayList<>();
                for (Arena arena : plugin.getAllArenas()) {
                    arenasList.add(arena.getName());
                }
                if ((args[0].equalsIgnoreCase("delete") && player.hasPermission("sheepquest.setup.delete")) ||
                        (args[0].equalsIgnoreCase("check") && player.hasPermission("sheepquest.setup.check")) ||
                        (args[0].equalsIgnoreCase("setmainlobby") && player.hasPermission("sheepquest.setup.setmainlobby")) ||
                        (args[0].equalsIgnoreCase("setwaitinglobby") && player.hasPermission("sheepquest.setup.setwaitinglobby")) ||
                        (args[0].equalsIgnoreCase("setspawn") && player.hasPermission("sheepquest.setup.setspawn")) ||
                        (args[0].equalsIgnoreCase("setsheepspawn") && player.hasPermission("sheepquest.setup.setsheepspawn")) ||
                        (args[0].equalsIgnoreCase("setmaxplayers") && player.hasPermission("sheepquest.setup.setmaxplayers")) ||
                        (args[0].equalsIgnoreCase("setminplayers") && player.hasPermission("sheepquest.setup.setminplayers")) ||
                        (args[0].equalsIgnoreCase("setvip") && player.hasPermission("sheepquest.setup.setvip")) ||
                        (args[0].equalsIgnoreCase("addteam") && player.hasPermission("sheepquest.setup.changeteams")) ||
                        (args[0].equalsIgnoreCase("removeteam") && player.hasPermission("sheepquest.setup.changeteams")) ||
                        (args[0].equalsIgnoreCase("listteams") && player.hasPermission("sheepquest.setup.listteams")) ||
                        (args[0].equalsIgnoreCase("setteamarea") && player.hasPermission("sheepquest.setup.setteamarea")) ||
                        (args[0].equalsIgnoreCase("forcestart") && player.hasPermission("sheepquest.staff.start")) ||
                        (args[0].equalsIgnoreCase("forcestop") && player.hasPermission("sheepquest.staff.stop")) ||
                        (args[0].equalsIgnoreCase("active") && player.hasPermission("sheepquest.staff.active")) ||
                        (args[0].equalsIgnoreCase("setupmenu") && player.hasPermission("sheepquest.setup.menu")) ||
                        (args[0].equalsIgnoreCase("staffmenu") && player.hasPermission("sheepquest.staff.menu")) ||
                        (args[0].equalsIgnoreCase("playersmenu") && player.hasPermission("sheepquest.staff.menu")) ||
                        (args[0].equalsIgnoreCase("join") && player.hasPermission("sheepquest.play.join"))) {

                    for (String s : arenasList) {
                        if (s.startsWith(args[1])) {
                            result.add(s);
                        }
                    }
                }
                if (args[0].equalsIgnoreCase("kick") && player.hasPermission("sheepquest.staff.kick")) {
                    for (String p : Utils.getPlayers()) {
                        if (p.startsWith(args[1])) {
                            result.add(p);
                        }

                    }
                }
                if ((args[0].equalsIgnoreCase("stats") && player.hasPermission("sheepquest.play.stats.others")) ||
                        (args[0].equalsIgnoreCase("statsmenu") && player.hasPermission("sheepquest.play.stats.menu.others"))) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getName().startsWith(args[1])) {
                            result.add(p.getName());
                        }
                    }
                }
                break;
            case 3:
                if ((args[0].equalsIgnoreCase("setspawn") && player.hasPermission("sheepquest.setup.setspawn")) ||
                        (args[0].equalsIgnoreCase("setteamarea") && player.hasPermission("sheepquest.setup.setteamarea")) ||
                        (args[0].equalsIgnoreCase("addteam") && player.hasPermission("sheepquest.setup.changeteams")) ||
                        (args[0].equalsIgnoreCase("removeteam") && player.hasPermission("sheepquest.setup.changeteams"))) {
                    List<String> teams = new ArrayList<>();
                    for (Team team : Utils.getTeams()) {
                        teams.add(team.getCode());
                    }
                    for (String s : teams) {
                        if (s.startsWith(args[2])) {
                            result.add(s);
                        }
                    }
                }
                if ((args[0].equalsIgnoreCase("active") && player.hasPermission("sheepquest.staff.active")) ||
                        (args[0].equalsIgnoreCase("setvip") && player.hasPermission("sheepquest.setup.setvip"))) {
                    if ("true".startsWith(args[2])) {
                        result.add("true");
                    }
                    if ("false".startsWith(args[2])) {
                        result.add("false");
                    }
                }
                break;
        }
        return result;

    }
}
