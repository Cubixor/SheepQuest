package me.cubixor.sheepquest.spigot.commands;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.game.Cooldown;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.menu.ArenasMenu;
import me.cubixor.sheepquest.spigot.menu.SetupMenu;
import me.cubixor.sheepquest.spigot.menu.StaffMenu;
import me.cubixor.sheepquest.spigot.menu.StatsMenu;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Command implements CommandExecutor {

    public final SheepQuest plugin;

    public Command() {
        plugin = SheepQuest.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("sheepquest")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("general.must-be-player"));
                return true;
            }
            Player player = (Player) sender;

            if (Cooldown.checkCooldown(player)) {
                return true;
            } else {
                Cooldown.addCooldown(player);
            }

            SetupCommands setupCommands = new SetupCommands();
            StaffCommands staffCommands = new StaffCommands();
            PlayCommands playCommands = new PlayCommands();


            if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
                help(player, "sheepquest.play.help", "help.general-help");
            } else if (args[0].equalsIgnoreCase("create")) {
                setupCommands.createArena(player, args);
            } else if (args[0].equalsIgnoreCase("delete")) {
                setupCommands.deleteArena(player, args);
            } else if (args[0].equalsIgnoreCase("confirm")) {
                setupCommands.deleteConfirm(player);
            } else if (args[0].equalsIgnoreCase("check")) {
                setupCommands.checkArena(player, args);
            } else if (args[0].equalsIgnoreCase("active")) {
                staffCommands.setActive(player, args);
            } else if (args[0].equalsIgnoreCase("setvip")) {
                setupCommands.setVip(player, args);
            } else if (args[0].equalsIgnoreCase("setmainlobby")) {
                setupCommands.setLocation(player, args, "arena-setup.set-main-lobby", ConfigField.MAIN_LOBBY, "sheepquest.setup.setmainlobby");
            } else if (args[0].equalsIgnoreCase("setwaitinglobby")) {
                setupCommands.setLocation(player, args, "arena-setup.set-waiting-lobby", ConfigField.WAITING_LOBBY, "sheepquest.setup.setwaitinglobby");
            } else if (args[0].equalsIgnoreCase("setspawn")) {
                setupCommands.setTeamSpawn(player, args);
            } else if (args[0].equalsIgnoreCase("setsheepspawn")) {
                setupCommands.setLocation(player, args, "arena-setup.set-sheep-spawn", ConfigField.SHEEP_SPAWN, "sheepquest.setup.setsheepspawn");
            } else if (args[0].equalsIgnoreCase("setminplayers")) {
                setupCommands.setMinPlayers(player, args);
            } else if (args[0].equalsIgnoreCase("setmaxplayers")) {
                setupCommands.setMaxPlayers(player, args);
            } else if (args[0].equalsIgnoreCase("setteamarea")) {
                setupCommands.setTeamArea(player, args);
            } else if (args[0].equalsIgnoreCase("wand")) {
                setupCommands.giveWand(player);
            } else if (args[0].equalsIgnoreCase("forcestart")) {
                staffCommands.forceStart(player, args);
            } else if (args[0].equalsIgnoreCase("forcestop")) {
                staffCommands.forceStop(player, args);
            } else if (args[0].equalsIgnoreCase("kick")) {
                staffCommands.kick(player, args);
            } else if (args[0].equalsIgnoreCase("join")) {
                playCommands.join(player, args);
            } else if (args[0].equalsIgnoreCase("leave")) {
                playCommands.leave(player);
            } else if (args[0].equalsIgnoreCase("reload")) {
                setupCommands.reload(player);
            } else if (args[0].equalsIgnoreCase("quickjoin")) {
                playCommands.quickJoin(player);
            } else if (args[0].equalsIgnoreCase("stats")) {
                playCommands.stats(player, args);
            } else if (args[0].equalsIgnoreCase("list")) {
                playCommands.arenaList(player);
            } else if (args[0].equalsIgnoreCase("staff")) {
                help(player, "sheepquest.staff.help", "help.staff-help");
            } else if (args[0].equalsIgnoreCase("admin")) {
                help(player, "sheepquest.admin.help", "help.admin-help");
            } else if (args[0].equalsIgnoreCase("setupmenu")) {
                new SetupMenu().setupMenuCommand(player, args);
            } else if (args[0].equalsIgnoreCase("staffmenu")) {
                new StaffMenu().staffMenuCommand(player, args);
            } else if (args[0].equalsIgnoreCase("arenasmenu")) {
                new ArenasMenu().arenasMenuCommand(player, args);
            } else if (args[0].equalsIgnoreCase("statsmenu")) {
                new StatsMenu().statsMenuCommand(player, args);
            } else if (args[0].equalsIgnoreCase("playersmenu")) {
                new StaffMenu().playersMenuCommand(player, args);
            } else {
                sender.sendMessage(plugin.getMessage("general.unknown-command"));
            }


        } else if (command.getName().equalsIgnoreCase("t")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("general.must-be-player"));
                return true;
            }

            Player player = (Player) sender;
            LocalArena localArena = Utils.getLocalArena(player);
            if (localArena == null) {
                player.sendMessage(plugin.getMessage("game.chat-not-in-game"));
                return true;
            }

            if (!localArena.getState().equals(GameState.GAME)) {
                player.sendMessage(plugin.getMessage("game.team-chat-game"));
                return true;
            }

            if (args.length == 0) {
                player.sendMessage(plugin.getMessage("game.team-chat-usage"));
                return true;
            }

            StringBuilder finalMessageBuilder = new StringBuilder();
            for (String msg : args) {
                finalMessageBuilder.append(msg).append(" ");
            }
            finalMessageBuilder.deleteCharAt(finalMessageBuilder.length() - 1);
            String finalMessage = finalMessageBuilder.toString();

            String teamColor = plugin.getMessage("general." + localArena.getPlayerTeam().get(player).getCode() + "-color");

            for (Player p : localArena.getPlayerTeam().keySet()) {
                if (localArena.getPlayerTeam().get(p).equals(localArena.getPlayerTeam().get(player))) {
                    p.sendMessage(plugin.getMessage("game.team-chat-format").replace("%player%", player.getName()).replace("%message%", finalMessage).replace("%color%", teamColor));
                }
            }
        }
        return true;
    }


    public void help(Player player, String permission, String messagesPath) {
        if (!player.hasPermission(permission)) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }

        for (String s : plugin.getMessageList(messagesPath)) {
            player.sendMessage(s);
        }
    }
}
