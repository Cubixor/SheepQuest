package me.cubixor.sheepquest.commands;

import me.cubixor.sheepquest.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class SetupCommands {

    public final SheepQuest plugin;

    public SetupCommands(SheepQuest s) {
        plugin = s;
    }

    public void createArena(Player player, String[] args) {
        if (!player.hasPermission("sheepquest.setup.create")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        if (args.length != 2) {
            player.sendMessage(plugin.getMessage("arena-setup.create-usage"));
            return;
        }

        plugin.getArenasConfig().set("Arenas." + args[1] + ".active", false);
        plugin.getArenasConfig().set("Arenas." + args[1] + ".min-players", 0);
        plugin.getArenasConfig().set("Arenas." + args[1] + ".max-players", 0);
        plugin.saveArenas();

        plugin.arenas.put(args[1], new Arena(plugin));
        plugin.inventories.put(player, new ArenaInventories(args[1]));

        player.sendMessage(plugin.getMessage("arena-setup.create-success").replace("%arena%", args[1]));
    }

    public void deleteArena(Player player, String[] args) {
        Utils utils = new Utils(plugin);
        if (!utils.checkIfValid(player, args, "sheepquest.setup.delete", "arena-setup.delete", 2)) {
            return;
        }

        plugin.playerInfo.get(player).delete = args[1];
        player.sendMessage(plugin.getMessage("arena-setup.delete-confirm").replace("%arena%", args[1]));
    }

    public void deleteConfirm(Player player) {
        if (plugin.playerInfo.get(player).delete != null) {
            String arena = plugin.playerInfo.get(player).delete;
            if (plugin.arenas.get(arena).state.equals(GameState.GAME) || plugin.arenas.get(arena).state.equals(GameState.ENDING)) {
                new StaffCommands(plugin).stop(player, arena);
            }

            plugin.getArenasConfig().set("Arenas." + arena, null);
            plugin.saveArenas();

            plugin.playerInfo.get(player).delete = null;
            player.sendMessage(plugin.getMessage("arena-setup.delete-success").replace("%arena%", arena));
        } else {
            player.sendMessage(plugin.getMessage("arena-setup.delete-confirm-none"));
        }

    }

    public void setLocation(Player player, String[] args, String messagesPath, String arenasPath, String permission) {
        Utils utils = new Utils(plugin);
        if (!utils.checkIfValid(player, args, permission, messagesPath, 2)) {
            return;
        }

        plugin.getArenasConfig().set("Arenas." + args[1] + "." + arenasPath, player.getLocation());
        plugin.saveArenas();
        player.sendMessage(plugin.getMessage(messagesPath + "-success").replace("%arena%", args[1]));
    }

    public void setMaxPlayers(Player player, String[] args) {
        Utils utils = new Utils(plugin);
        if (!utils.checkIfValid(player, args, "sheepquest.setup.setmaxplayers", "arena-setup.set-max-players", 3)) {
            return;
        }

        try {
            int max = Integer.parseInt(args[2]);
            if (max > 44) {
                player.sendMessage(plugin.getMessage("arena-setup.set-max-players-bound"));
                return;
            }
            if (max % 4 == 0) {
                plugin.getArenasConfig().set("Arenas." + args[1] + ".max-players", max);
                plugin.saveArenas();
                player.sendMessage(plugin.getMessage("arena-setup.set-max-players-success").replace("%arena%", args[1]));
            } else {
                player.sendMessage(plugin.getMessage("arena-setup.set-max-players-divisible-by-4"));
            }
        } catch (NumberFormatException ex) {
            player.sendMessage(plugin.getMessage("arena-setup.set-max-players-invalid-number"));
        }
    }

    public void setMinPlayers(Player player, String[] args) {
        Utils utils = new Utils(plugin);
        if (!utils.checkIfValid(player, args, "sheepquest.setup.setminplayers", "arena-setup.set-min-players", 3)) {
            return;
        }

        try {
            int min = Integer.parseInt(args[2]);
            if (min >= 2) {
                plugin.getArenasConfig().set("Arenas." + args[1] + ".min-players", min);
                plugin.saveArenas();
                player.sendMessage(plugin.getMessage("arena-setup.set-min-players-success").replace("%arena%", args[1]));
            } else {
                player.sendMessage(plugin.getMessage("arena-setup.set-min-players-too-small"));
            }
        } catch (NumberFormatException ex) {
            player.sendMessage(plugin.getMessage("arena-setup.set-min-players-invalid-number"));
        }
    }

    public void setTeamSpawn(Player player, String[] args) {
        Utils utils = new Utils(plugin);
        if (!utils.checkIfValid(player, args, "sheepquest.setup.setspawn", "arena-setup.set-spawn", 3)) {
            return;
        }

        String team = args[2];
        if (!isTeamValid(team)) {
            player.sendMessage(plugin.getMessage("arena-setup.invalid-team"));
            return;
        }
        plugin.getArenasConfig().set("Arenas." + args[1] + ".teams." + team + "-spawn", player.getLocation());
        plugin.saveArenas();
        player.sendMessage(plugin.getMessage("arena-setup.set-spawn-success").replace("%arena%", args[1]).replace("%team%", plugin.getMessage("general.team-" + team)));

    }

    public void setTeamArea(Player player, String[] args) {
        Utils utils = new Utils(plugin);
        if (!utils.checkIfValid(player, args, "sheepquest.setup.setteamarea", "arena-setup.set-teams-area", 3)) {
            return;
        }

        String team = args[2];
        if (!isTeamValid(team)) {
            player.sendMessage(plugin.getMessage("arena-setup.invalid-team"));
            return;
        }
        if (!(plugin.playerInfo.get(player).selMin != null && plugin.playerInfo.get(player).selMax != null)) {
            player.sendMessage(plugin.getMessage("arena-setup.selection-empty"));
            return;
        }

        plugin.getArenasConfig().set("Arenas." + args[1] + ".teams-area." + team + ".min-point", plugin.playerInfo.get(player).selMin.getLocation());
        plugin.getArenasConfig().set("Arenas." + args[1] + ".teams-area." + team + ".max-point", plugin.playerInfo.get(player).selMax.getLocation());
        plugin.saveArenas();
        player.sendMessage(plugin.getMessage("arena-setup.set-teams-area-success").replace("%arena%", args[1]).replace("%team%", plugin.getMessage("general.team-" + team)));

    }

    public void giveWand(Player player) {
        if (!player.hasPermission("sheepquest.setup.wand")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        player.getInventory().addItem(plugin.items.setupWandItem);
        player.sendMessage(plugin.getMessage("arena-setup.wand-item-recive"));
    }

    public void reload(Player player) {
        if (!player.hasPermission("sheepquest.setup.reload")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }

        for (String arena : plugin.arenas.keySet()) {
            for (Player p : plugin.arenas.get(arena).playerTeam.keySet()) {
                new PlayCommands(plugin).kickPlayer(p, arena);
                p.sendMessage(plugin.getMessage("arena-leave-reload"));
            }
        }

        plugin.loadConfigs();

        player.sendMessage(plugin.getMessage("general.reload-success"));

    }

    public void checkArena(Player player, String[] args) {
        Utils utils = new Utils(plugin);
        if (!utils.checkIfValid(player, args, "sheepquest.setup.check", "arena-setup.check", 2)) {
            return;
        }

        String set = plugin.getMessage("arena-setup.check-set");
        String notSet = plugin.getMessage("arena-setup.check-notset");

        List<String> checkPage = plugin.getMessageList("arena-setup.check-page");

        LinkedHashMap<String, Boolean> checkReady = new LinkedHashMap<>(new Utils(plugin).checkIfReady(args[1]));

        boolean ready = true;
        boolean active = plugin.getArenasConfig().getBoolean("Arenas." + args[1] + ".active");

        for (String path : checkReady.keySet()) {
            boolean isSet = checkReady.get(path);

            String toReplace = "%" + path + "%";
            for (String line : checkPage) {
                Collections.replaceAll(checkPage, line, line.replace(toReplace, isSet ? set : notSet));
            }

            if (!isSet) {
                ready = false;
            }
        }
        for (String line : checkPage) {
            Collections.replaceAll(checkPage, line, line.replace("%active%", active ? plugin.getMessage("arena-setup.check-active") : plugin.getMessage("arena-setup.check-not-active")));
            Collections.replaceAll(checkPage, line, line.replace("%ready%", ready ? plugin.getMessage("arena-setup.check-ready") : plugin.getMessage("arena-setup.check-not-ready")));
            Collections.replaceAll(checkPage, line, line.replace("%arena%", args[1]));
        }


        for (String s : checkPage) {
            player.sendMessage(s);
        }
    }


    int timer = 20;

    public void confirmScheduler(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (timer == 0) {
                    plugin.playerInfo.get(player).delete = null;
                    this.cancel();
                } else {
                    timer--;
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }


    public boolean isTeamValid(String teamString) {
        Team team;
        switch (teamString.toLowerCase()) {
            case "red":
                team = Team.RED;
                break;
            case "green":
                team = Team.GREEN;
                break;
            case "blue":
                team = Team.BLUE;
                break;
            case "yellow":
                team = Team.YELLOW;
                break;
            default:
                team = Team.NONE;
                break;
        }
        return !team.equals(Team.NONE);
    }
}
