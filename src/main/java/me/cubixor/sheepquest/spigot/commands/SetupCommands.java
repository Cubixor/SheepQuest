package me.cubixor.sheepquest.spigot.commands;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.game.JoinSheep;
import me.cubixor.sheepquest.spigot.game.Signs;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.spigot.gameInfo.ArenaInventories;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import me.cubixor.sheepquest.spigot.socket.SocketClientSender;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
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
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!player.hasPermission("sheepquest.setup.create")) {
                player.sendMessage(plugin.getMessage("general.no-permission"));
                return;
            }
            if (args.length != 2) {
                player.sendMessage(plugin.getMessage("arena-setup.create-usage"));
                return;
            }
            String arena = args[1];

            if (ConfigUtils.getArenas().contains(arena)) {
                player.sendMessage(plugin.getMessage("arena-setup.create-already-exists").replace("%arena%", arena));
                return;
            }

            ConfigUtils.insertArena(arena);

            LocalArena localArena = new LocalArena(arena);

            plugin.getLocalArenas().put(args[1], localArena);
            plugin.getSigns().put(arena, new ArrayList<>());
            plugin.getInventories().put(player, new ArenaInventories(args[1]));

            if (plugin.isBungee()) {
                Arena arenaObj = new Arena(localArena.getName(), localArena.getServer(), localArena.getState(), localArena.getPlayers());
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> new SocketClientSender().sendUpdateArenaPacket(arenaObj));
            }

            new Signs().updateSigns("quickjoin");

            player.sendMessage(plugin.getMessage("arena-setup.create-success").replace("%arena%", args[1]));
        });
    }

    public void deleteArena(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            if (!Utils.checkIfValid(player, args, "sheepquest.setup.delete", "arena-setup.delete", 2, true)) {
                return;
            }
            if (!setupCheckActive(player, args[1])) {
                return;
            }
            plugin.getPlayerInfo().get(player).setDelete(args[1]);
            confirmScheduler(player);
            player.sendMessage(plugin.getMessage("arena-setup.delete-confirm").replace("%arena%", args[1]));
        });
    }

    public void deleteConfirm(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (plugin.getPlayerInfo().get(player).getDelete() != null) {
                String arena = plugin.getPlayerInfo().get(player).getDelete();

                if (!setupCheckActive(player, arena)) {
                    return;
                }

                new Signs().removeSigns(arena);

                plugin.getLocalArenas().remove(arena);
                ConfigUtils.removeArena(arena);

                if (plugin.isBungee()) {
                    Arena arenaObj = new Arena(arena, plugin.getServerName());
                    new SocketClientSender().sendRemoveArenaPacket(arenaObj);
                }

                plugin.getPlayerInfo().get(player).setDelete(null);

                new Signs().updateSigns("quickjoin");

                player.sendMessage(plugin.getMessage("arena-setup.delete-success").replace("%arena%", arena));
            } else {
                player.sendMessage(plugin.getMessage("arena-setup.delete-confirm-none"));
            }
        });
    }

    public void createJoinSheep(Player player) {
        if (!player.hasPermission("sheepquest.setup.joinsheep")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        new JoinSheep().spawnSheep(player);
    }

    public void removeJoinSheep(Player player) {
        if (!player.hasPermission("sheepquest.setup.joinsheep")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }

        List<Entity> entities = new ArrayList<>(player.getWorld().getNearbyEntities(player.getLocation(), 1, 1, 1));
        for (Entity entity : entities) {
            if (entity.getType().equals(EntityType.SHEEP)) {
                Sheep sheep = (Sheep) entity;

                List<String> signList = new ArrayList<>(plugin.getArenasConfig().getStringList("join-sheep"));
                signList.remove(ConfigUtils.locationToString(sheep.getLocation()));
                plugin.getArenasConfig().set("join-sheep", signList);
                plugin.saveArenas();

                sheep.remove();

                player.sendMessage(plugin.getMessage("arena-setup.remove-join-sheep-success"));
                return;
            }
        }

        player.sendMessage(plugin.getMessage("arena-setup.remove-join-sheep-no-sheep"));
    }

    public void setLocation(Player player, String[] args, String messagesPath, ConfigField configField, String permission) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!Utils.checkIfValid(player, args, permission, messagesPath, 2, true)) {
                return;
            }
            if (!setupCheckActive(player, args[1])) {
                return;
            }


            ConfigUtils.updateField(args[1], configField, ConfigUtils.locationToString(player.getLocation()));
            player.sendMessage(plugin.getMessage(messagesPath + "-success").replace("%arena%", args[1]));
        });

    }

    public void addTeam(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!Utils.checkIfValid(player, args, "sheepquest.setup.changeteams", "arena-setup.add-team", 3, true)) {
                return;
            }
            if (!setupCheckActive(player, args[1])) {
                return;
            }

            Team team = Team.NONE;
            for (Team t : Utils.getTeams()) {
                if (args[2].equalsIgnoreCase(t.getCode())) {
                    team = t;
                    break;
                }
            }

            if (team.equals(Team.NONE)) {
                player.sendMessage(plugin.getMessage("arena-setup.add-team-invalid-team"));
                return;
            }

            List<Team> teams = new ArrayList<>(ConfigUtils.getTeamList(args[1]));

            if (teams.contains(team)) {
                player.sendMessage(plugin.getMessage("arena-setup.add-team-already-added"));
                return;
            }

            teams.add(team);
            List<String> teamsString = new ArrayList<>();
            for (Team t : teams) {
                teamsString.add(t.getCode());
            }
            ConfigUtils.updateField(args[1], ConfigField.TEAMS, teamsString);

            player.sendMessage(plugin.getMessage("arena-setup.add-team-success").replace("%arena%", args[1]));
        });
    }

    public void removeTeam(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!Utils.checkIfValid(player, args, "sheepquest.setup.changeteams", "arena-setup.remove-team", 3, true)) {
                return;
            }
            if (!setupCheckActive(player, args[1])) {
                return;
            }

            Team team = Team.NONE;
            for (Team t : Utils.getTeams()) {
                if (args[2].equalsIgnoreCase(t.getCode())) {
                    team = t;
                    break;
                }
            }

            if (team.equals(Team.NONE)) {
                player.sendMessage(plugin.getMessage("arena-setup.remove-team-invalid-team"));
                return;
            }

            List<Team> teams = new ArrayList<>(ConfigUtils.getTeamList(args[1]));

            if (!teams.contains(team)) {
                player.sendMessage(plugin.getMessage("arena-setup.remove-team-not-added"));
                return;
            }

            teams.remove(team);
            List<String> teamsString = new ArrayList<>();
            for (Team t : teams) {
                teamsString.add(t.getCode());
            }
            ConfigUtils.updateField(args[1], ConfigField.TEAMS, teamsString);

            ConfigUtils.updateField(args[1], ConfigField.SPAWN, team, null);
            ConfigUtils.updateField(args[1], ConfigField.AREA, team, null);

            player.sendMessage(plugin.getMessage("arena-setup.remove-team-success").replace("%arena%", args[1]));
        });
    }

    public void listTeams(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!Utils.checkIfValid(player, args, "sheepquest.setup.listteams", "arena-setup.list-teams", 2, true)) {
                return;
            }

            List<Team> teams = new ArrayList<>(ConfigUtils.getTeamList(args[1]));
            StringBuilder addedTeams = new StringBuilder();
            StringBuilder otherTeams = new StringBuilder();

            for (Team team : Utils.getTeams()) {
                if (teams.contains(team)) {
                    addedTeams.append(team.getName()).append(ChatColor.translateAlternateColorCodes('&', "&f, "));
                } else {
                    otherTeams.append(team.getName()).append(ChatColor.translateAlternateColorCodes('&', "&f, "));
                }
            }

            player.sendMessage(plugin.getMessage("arena-setup.list-teams-success").replace("%arena%", args[1]).replace("%teams%", addedTeams));
            player.sendMessage(plugin.getMessage("arena-setup.list-teams-success2").replace("%arena%", args[1]).replace("%teams%", otherTeams));
        });
    }

    public void setMaxPlayers(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!Utils.checkIfValid(player, args, "sheepquest.setup.setmaxplayers", "arena-setup.set-max-players", 3, true)) {
                return;
            }
            if (!setupCheckActive(player, args[1])) {
                return;
            }

            try {
                int max = Integer.parseInt(args[2]);
                //if (max % 4 == 0) {
                ConfigUtils.updateField(args[1], ConfigField.MAX_PLAYERS, max);
                player.sendMessage(plugin.getMessage("arena-setup.set-max-players-success").replace("%arena%", args[1]));
                //}
            } catch (NumberFormatException ex) {
                player.sendMessage(plugin.getMessage("arena-setup.set-max-players-invalid-count"));
            }
        });
    }

    public void setMinPlayers(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!Utils.checkIfValid(player, args, "sheepquest.setup.setminplayers", "arena-setup.set-min-players", 3, true)) {
                return;
            }
            if (!setupCheckActive(player, args[1])) {
                return;
            }

            try {
                int min = Integer.parseInt(args[2]);
                if (min >= 2) {
                    ConfigUtils.updateField(args[1], ConfigField.MIN_PLAYERS, min);
                    player.sendMessage(plugin.getMessage("arena-setup.set-min-players-success").replace("%arena%", args[1]));
                } else {
                    player.sendMessage(plugin.getMessage("arena-setup.set-min-players-too-small"));
                }
            } catch (NumberFormatException ex) {
                player.sendMessage(plugin.getMessage("arena-setup.set-min-players-invalid-count"));
            }
        });
    }

    public void setTeamSpawn(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!Utils.checkIfValid(player, args, "sheepquest.setup.setspawn", "arena-setup.set-spawn", 3, true)) {
                return;
            }
            if (!setupCheckActive(player, args[1])) {
                return;
            }

            String teamStr = args[2];
            Team team = getTeam(teamStr);
            if (getTeam(teamStr) == null) {
                player.sendMessage(plugin.getMessage("arena-setup.invalid-team"));
                return;
            }

            ConfigUtils.updateField(args[1], ConfigField.SPAWN, team, ConfigUtils.locationToString(player.getLocation()));
            player.sendMessage(plugin.getMessage("arena-setup.set-spawn-success").replace("%arena%", args[1]).replace("%team%", plugin.getMessage("general.team-" + team.getCode())));
        });
    }

    public void setTeamArea(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!Utils.checkIfValid(player, args, "sheepquest.setup.setteamarea", "arena-setup.set-teams-area", 3, true)) {
                return;
            }
            if (!setupCheckActive(player, args[1])) {
                return;
            }

            String teamStr = args[2];
            Team team = getTeam(teamStr);
            if (team == null) {
                player.sendMessage(plugin.getMessage("arena-setup.invalid-team"));
                return;
            }
            if (!(plugin.getPlayerInfo().get(player).getSelMin() != null && plugin.getPlayerInfo().get(player).getSelMax() != null)) {
                player.sendMessage(plugin.getMessage("arena-setup.selection-empty"));
                return;
            }

            Location loc1 = plugin.getPlayerInfo().get(player).getSelMin().getLocation();
            Location loc2 = plugin.getPlayerInfo().get(player).getSelMax().getLocation();

            ConfigUtils.updateField(args[1], ConfigField.AREA, team, ConfigUtils.joinLocations(loc1, loc2));

            player.sendMessage(plugin.getMessage("arena-setup.set-teams-area-success").replace("%arena%", args[1]).replace("%team%", plugin.getMessage("general.team-" + team.getCode())));
        });
    }

    public void giveWand(Player player) {
        if (!player.hasPermission("sheepquest.setup.wand")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        player.getInventory().addItem(plugin.getItems().getSetupWandItem());
        player.sendMessage(plugin.getMessage("arena-setup.wand-item-recive"));
    }

    public void setVip(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!Utils.checkIfValid(player, args, "sheepquest.setup.setvip", "arena-setup.set-vip", 3, true)) {
                return;
            }

            if (!args[2].equalsIgnoreCase("true") && !args[2].equalsIgnoreCase("false")) {
                player.sendMessage(plugin.getMessage("arena-setup.set-vip-usage"));
                return;
            }

            if (!setupCheckActive(player, args[1])) {
                return;
            }

            boolean cmdActive = Boolean.parseBoolean(args[2]);
            boolean active = ConfigUtils.getBoolean(args[1], ConfigField.VIP);

            if (cmdActive && active) {
                player.sendMessage(plugin.getMessage("arena-setup.set-vip-arena-vip").replace("%arena%", args[1]));
            } else if (!cmdActive && !active) {
                player.sendMessage(plugin.getMessage("arena-setup.set-vip-arena-not-vip").replace("%arena%", args[1]));
            } else {
                ConfigUtils.updateField(args[1], ConfigField.VIP, cmdActive);

                new Signs().updateSigns(args[1]);
                if (cmdActive) {
                    player.sendMessage(plugin.getMessage("arena-setup.set-vip-success-vip").replace("%arena%", args[1]));
                } else {
                    player.sendMessage(plugin.getMessage("arena-setup.set-vip-success-not-vip").replace("%arena%", args[1]));
                }
            }
        });

    }

    public void reload(Player player) {
        if (!player.hasPermission("sheepquest.setup.reload")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }

        plugin.onDisable();
        plugin.getInventories().clear();
        plugin.getSigns().clear();
        plugin.getLocalArenas().clear();
        plugin.getArenas().clear();

        plugin.loadConfigs();

        player.sendMessage(plugin.getMessage("general.reload-success"));
    }

    public void checkArena(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!Utils.checkIfValid(player, args, "sheepquest.setup.check", "arena-setup.check", 2, true)) {
                return;
            }

            String set = plugin.getMessage("arena-setup.check-set");
            String notSet = plugin.getMessage("arena-setup.check-notset");

            List<String> checkPage = plugin.getMessageList("arena-setup.check-page-new");

            LinkedHashMap<ConfigField, Boolean> checkReady = new LinkedHashMap<>(Utils.checkIfReady(args[1]));

            boolean ready = true;
            boolean active = ConfigUtils.getBoolean(args[1], ConfigField.ACTIVE);

            for (ConfigField configField : checkReady.keySet()) {
                boolean isSet = checkReady.get(configField);

                String toReplace = "%" + configField.getCode().replace(".", "") + "%";
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
                Collections.replaceAll(checkPage, line, line.replace("%team%", Integer.toString(ConfigUtils.getTeamList(args[1]).size())));
            }


            for (String s : checkPage) {
                player.sendMessage(s);
            }
        });
    }


    public void confirmScheduler(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (plugin.getPlayerInfo().get(player) == null) {
                    return;
                }
                plugin.getPlayerInfo().get(player).setDelete(null);
            }
        }.runTaskLater(plugin, 400);
    }


    public Team getTeam(String teamString) {
        for (Team team : Team.values()) {
            if (teamString.equalsIgnoreCase(team.getCode())) {
                return team;
            }
        }
        return null;
    }

    private boolean setupCheckActive(Player player, String arena) {
        if (ConfigUtils.getBoolean(arena, ConfigField.ACTIVE)) {
            player.sendMessage(plugin.getMessage("arena-setup.active-block").replace("%arena%", arena));
            return false;
        }
        return true;
    }
}
