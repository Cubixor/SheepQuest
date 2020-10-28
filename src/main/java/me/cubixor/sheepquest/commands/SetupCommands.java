package me.cubixor.sheepquest.commands;

import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.game.Signs;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.ArenaInventories;
import me.cubixor.sheepquest.gameInfo.GameState;
import me.cubixor.sheepquest.gameInfo.Team;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

public class SetupCommands {

    public final SheepQuest plugin;

    public SetupCommands() {
        plugin = SheepQuest.getInstance();
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

        plugin.getArenas().put(args[1], new Arena());
        plugin.getInventories().put(player, new ArenaInventories(args[1]));

        player.sendMessage(plugin.getMessage("arena-setup.create-success").replace("%arena%", args[1]));
    }

    public void deleteArena(Player player, String[] args) {
        if (!Utils.checkIfValid(player, args, "sheepquest.setup.delete", "arena-setup.delete", 2)) {
            return;
        }

        plugin.getPlayerInfo().get(player).setDelete(args[1]);
        player.sendMessage(plugin.getMessage("arena-setup.delete-confirm").replace("%arena%", args[1]));
    }

    public void deleteConfirm(Player player) {
        if (plugin.getPlayerInfo().get(player).getDelete() != null) {
            String arena = plugin.getPlayerInfo().get(player).getDelete();
            Arena arenaObj = plugin.getArenas().get(arena);

            if (arenaObj.getState().equals(GameState.GAME) || arenaObj.getState().equals(GameState.ENDING)) {
                new StaffCommands().stop(player, arena);
            } else {
                for (Player p : plugin.getArenas().get(arena).getPlayers().keySet()) {
                    new PlayCommands().kickPlayer(p, arena);
                    p.sendMessage(plugin.getMessage("arena-moderate.force-stop-success").replace("%player%", player.getName()));
                }
            }

            plugin.getArenas().remove(arena);
            new Signs().removeSigns(arenaObj);

            plugin.getArenasConfig().set("Arenas." + arena, null);
            plugin.saveArenas();

            plugin.getPlayerInfo().get(player).setDelete(null);
            player.sendMessage(plugin.getMessage("arena-setup.delete-success").replace("%arena%", arena));
        } else {
            player.sendMessage(plugin.getMessage("arena-setup.delete-confirm-none"));
        }

    }

    public void setLocation(Player player, String[] args, String messagesPath, String arenasPath, String permission) {
        if (!Utils.checkIfValid(player, args, permission, messagesPath, 2)) {
            return;
        }
        if (!setupCheckActive(player, args[1])) {
            return;
        }

        plugin.getArenasConfig().set("Arenas." + args[1] + "." + arenasPath, player.getLocation());
        plugin.saveArenas();
        player.sendMessage(plugin.getMessage(messagesPath + "-success").replace("%arena%", args[1]));
    }

    public void setMaxPlayers(Player player, String[] args) {
        if (!Utils.checkIfValid(player, args, "sheepquest.setup.setmaxplayers", "arena-setup.set-max-players", 3)) {
            return;
        }
        if (!setupCheckActive(player, args[1])) {
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
            player.sendMessage(plugin.getMessage("arena-setup.set-max-players-invalid-count"));
        }
    }

    public void setMinPlayers(Player player, String[] args) {
        if (!Utils.checkIfValid(player, args, "sheepquest.setup.setminplayers", "arena-setup.set-min-players", 3)) {
            return;
        }
        if (!setupCheckActive(player, args[1])) {
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
            player.sendMessage(plugin.getMessage("arena-setup.set-min-players-invalid-count"));
        }
    }

    public void setTeamSpawn(Player player, String[] args) {
        if (!Utils.checkIfValid(player, args, "sheepquest.setup.setspawn", "arena-setup.set-spawn", 3)) {
            return;
        }
        if (!setupCheckActive(player, args[1])) {
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
        if (!Utils.checkIfValid(player, args, "sheepquest.setup.setteamarea", "arena-setup.set-teams-area", 3)) {
            return;
        }
        if (!setupCheckActive(player, args[1])) {
            return;
        }

        String team = args[2];
        if (!isTeamValid(team)) {
            player.sendMessage(plugin.getMessage("arena-setup.invalid-team"));
            return;
        }
        if (!(plugin.getPlayerInfo().get(player).getSelMin() != null && plugin.getPlayerInfo().get(player).getSelMax() != null)) {
            player.sendMessage(plugin.getMessage("arena-setup.selection-empty"));
            return;
        }

        plugin.getArenasConfig().set("Arenas." + args[1] + ".teams-area." + team + ".min-point", plugin.getPlayerInfo().get(player).getSelMin().getLocation());
        plugin.getArenasConfig().set("Arenas." + args[1] + ".teams-area." + team + ".max-point", plugin.getPlayerInfo().get(player).getSelMax().getLocation());
        plugin.saveArenas();
        player.sendMessage(plugin.getMessage("arena-setup.set-teams-area-success").replace("%arena%", args[1]).replace("%team%", plugin.getMessage("general.team-" + team)));

    }

    public void giveWand(Player player) {
        if (!player.hasPermission("sheepquest.setup.wand")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        player.getInventory().addItem(plugin.getItems().getSetupWandItem());
        player.sendMessage(plugin.getMessage("arena-setup.wand-item-recive"));
    }

    public void reload(Player player) {
        if (!player.hasPermission("sheepquest.setup.reload")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }

        for (String arena : plugin.getArenas().keySet()) {
            List<Player> list = new ArrayList<>(plugin.getArenas().get(arena).getPlayers().keySet());
            for (Player p : list) {
                new PlayCommands().kickPlayer(p, arena);
                p.sendMessage(plugin.getMessage("game.arena-leave-reload"));
            }
        }

        plugin.loadConfigs();
        new Signs().loadSigns();

        player.sendMessage(plugin.getMessage("general.reload-success"));

    }

    public void checkArena(Player player, String[] args) {
        if (!Utils.checkIfValid(player, args, "sheepquest.setup.check", "arena-setup.check", 2)) {
            return;
        }

        String set = plugin.getMessage("arena-setup.check-set");
        String notSet = plugin.getMessage("arena-setup.check-notset");

        List<String> checkPage = plugin.getMessageList("arena-setup.check-page");

        LinkedHashMap<String, Boolean> checkReady = new LinkedHashMap<>(Utils.checkIfReady(args[1]));

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


    public void confirmScheduler(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getPlayerInfo().get(player).getConfirmTimer() == 0) {
                    plugin.getPlayerInfo().get(player).setDelete(null);
                    plugin.getPlayerInfo().get(player).setConfirmTimer(20);
                    this.cancel();
                } else {
                    plugin.getPlayerInfo().get(player).setConfirmTimer(plugin.getPlayerInfo().get(player).getConfirmTimer() - 1);
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

    private boolean setupCheckActive(Player player, String arena) {
        if (plugin.getArenasConfig().getBoolean("Arenas." + arena + ".active")) {
            player.sendMessage(plugin.getMessage("arena-setup.active-block").replace("%arena%", arena));
            return false;
        }
        return true;
    }
}
