package me.cubixor.sheepquest.spigot.menu;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.commands.SetupCommands;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.game.Cooldown;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class SetupMenu implements Listener {

    private final SheepQuest plugin;

    public SetupMenu() {
        plugin = SheepQuest.getInstance();
    }

    public void setupMenuCommand(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!Utils.checkIfValid(player, args, "sheepquest.setup.menu", "setup-menu.command", 2, true)) {
                return;
            }
            updateSetupMenu(args[1], player);
        });
    }

    public void updateSetupMenu(String arena, Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            LinkedHashMap<ConfigField, Boolean> check = new LinkedHashMap<>(Utils.checkIfReady(arena));
            boolean vip = ConfigUtils.getBoolean(arena, ConfigField.VIP);

            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.putInventories(player, arena);

                Inventory setupInventory = Bukkit.createInventory(null, 27, plugin.getMessage("setup-menu.name").replace("%arena%", arena));
                setupInventory.setItem(0, Utils.setItemStack(XMaterial.COMPASS.parseMaterial(), "setup-menu.check-item-name", "setup-menu.check-item-lore",
                        "%ready%", !check.containsValue(false) ? plugin.getMessage("arena-setup.check-yes") : plugin.getMessage("arena-setup.check-no")));
                setupInventory.setItem(1, Utils.setItemStack(XMaterial.LAVA_BUCKET.parseMaterial(), "setup-menu.delete-item-name", "setup-menu.delete-item-lore"));
                setupInventory.setItem(2, Utils.setItemStack(XMaterial.SNOWBALL.parseMaterial(), "setup-menu.teams-add-remove-item-name", "setup-menu.teams-add-remove-item-lore",
                        "%count%", Integer.toString(ConfigUtils.getTeamList(arena).size())));
                setupInventory.setItem(3, Utils.setItemStack(XMaterial.BLAZE_POWDER.parseMaterial(), "setup-menu.vip-item-name", "setup-menu.vip-item-lore",
                        "%vip%", vip ? plugin.getMessage("setup-menu.arena-vip") : plugin.getMessage("setup-menu.arena-not-vip")));
                setupInventory.setItem(4, Utils.setItemStack(XMaterial.IRON_INGOT.parseMaterial(), "setup-menu.min-players-item-name", "setup-menu.min-players-item-lore",
                        "%set%", checkString(check.get(ConfigField.MIN_PLAYERS))));
                setupInventory.setItem(5, Utils.setItemStack(XMaterial.GOLD_INGOT.parseMaterial(), "setup-menu.max-players-item-name", "setup-menu.max-players-item-lore",
                        "%set%", checkString(check.get(ConfigField.MAX_PLAYERS))));
                setupInventory.setItem(6, Utils.setItemStack(XMaterial.ENDER_EYE.parseMaterial(), "setup-menu.main-lobby-item-name", "setup-menu.main-lobby-item-lore",
                        "%set%", checkString(check.get(ConfigField.MAIN_LOBBY))));
                setupInventory.setItem(7, Utils.setItemStack(XMaterial.CLOCK.parseMaterial(), "setup-menu.waiting-lobby-item-name", "setup-menu.waiting-lobby-item-lore",
                        "%set%", checkString(check.get(ConfigField.WAITING_LOBBY))));
                setupInventory.setItem(8, Utils.setItemStack(XMaterial.SHEARS.parseMaterial(), "setup-menu.sheep-spawn-item-name", "setup-menu.sheep-spawn-item-lore",
                        "%set%", checkString(check.get(ConfigField.SHEEP_SPAWN))));
                setupInventory.setItem(9, Utils.setItemStack(XMaterial.WHITE_BANNER.parseMaterial(), "setup-menu.team-spawn-item-name", "setup-menu.team-spawn-item-lore",
                        "%set%", checkString(check.get(ConfigField.SPAWN))));
                setupInventory.setItem(10, Utils.setItemStack(XMaterial.WHITE_WOOL.parseMaterial(), "setup-menu.team-area-item-name", "setup-menu.team-area-item-lore",
                        "%set%", checkString(check.get(ConfigField.AREA))));
                setupInventory.setItem(22, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "setup-menu.back-item-name", "setup-menu.back-item-lore"));

                plugin.getInventories().get(player).setActiveInventory(setupInventory);
                plugin.getInventories().get(player).setInventoryType(MenuType.SETUP);

                player.openInventory(plugin.getInventories().get(player).getActiveInventory());
            });
        });
    }

    public void updateSpawnSetupMenu(String arena, Player player) {
        plugin.putInventories(player, arena);

        Inventory spawnSetupInventory = Bukkit.createInventory(null, 27, plugin.getMessage("setup-menu.team-spawn-menu-name").replace("%arena%", arena));
        List<Team> teams = new ArrayList<>(ConfigUtils.getTeamList(arena));
        int slot = 0;
        for (Team team : teams) {
            spawnSetupInventory.setItem(slot, Utils.setItemStack(team.getBanner(), "setup-menu.specific-team-spawn-item-name",
                    "setup-menu.specific-team-spawn-item-lore", "%set%", checkString(ConfigUtils.getSpawn(arena, team) != null), team));
            slot++;
        }
        spawnSetupInventory.setItem(22, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "setup-menu.team-spawn-menu-back-item-name", "setup-menu.team-spawn-menu-back-item-lore"));


        plugin.getInventories().get(player).setActiveInventory(spawnSetupInventory);
        plugin.getInventories().get(player).setInventoryType(MenuType.SPAWN_SETUP);

        player.openInventory(plugin.getInventories().get(player).getActiveInventory());
    }

    public void updateAreaSetupMenu(String arena, Player player) {
        plugin.putInventories(player, arena);

        Inventory areaSetupInventory = Bukkit.createInventory(null, 27, plugin.getMessage("setup-menu.team-area-menu-name").replace("%arena%", arena));
        List<Team> teams = new ArrayList<>(ConfigUtils.getTeamList(arena));
        int slot = 0;
        for (Team team : teams) {
            areaSetupInventory.setItem(slot, Utils.setItemStack(team.getWool(), "setup-menu.specific-team-area-item-name",
                    "setup-menu.specific-team-area-item-lore", "%set%", checkString(ConfigUtils.getArea(arena, team) != null), team));
            slot++;
        }
        areaSetupInventory.setItem(21, Utils.setItemStack(XMaterial.BLAZE_ROD.parseMaterial(), "setup-menu.wand-item-name", "setup-menu.wand-item-lore"));
        areaSetupInventory.setItem(22, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "setup-menu.team-area-menu-back-item-name", "setup-menu.team-area-menu-back-item-lore"));


        plugin.getInventories().get(player).setActiveInventory(areaSetupInventory);
        plugin.getInventories().get(player).setInventoryType(MenuType.AREA_SETUP);

        player.openInventory(plugin.getInventories().get(player).getActiveInventory());
    }

    public void updateTeamsMenu(String arena, Player player) {
        plugin.putInventories(player, arena);

        List<Team> arenaTeams = new ArrayList<>(ConfigUtils.getTeamList(arena));
        Inventory teamsInventory = Bukkit.createInventory(null, 27, plugin.getMessage("setup-menu.team-area-menu-name").replace("%arena%", arena));
        int slot = 0;
        for (Team team : Utils.getTeams()) {
            if (arenaTeams.contains(team)) {
                teamsInventory.setItem(slot, Utils.setItemStack(team.getWool(), "setup-menu.specific-team-remove-item-name",
                        "setup-menu.specific-team-remove-item-lore", "%team%", team.getName(), team));
            } else {
                teamsInventory.setItem(slot, Utils.setItemStack(team.getWool(), "setup-menu.specific-team-add-item-name",
                        "setup-menu.specific-team-add-item-lore", "%team%", team.getName(), team));
            }
            slot++;
        }
        teamsInventory.setItem(22, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "setup-menu.teams-add-remove-menu-back-item-name", "teams-add-remove-menu-back-item-lore"));

        plugin.getInventories().get(player).setActiveInventory(teamsInventory);
        plugin.getInventories().get(player).setInventoryType(MenuType.TEAMS_ADD_REMOVE);

        player.openInventory(plugin.getInventories().get(player).getActiveInventory());
    }

    private String checkString(boolean set) {
        return set ? plugin.getMessage("arena-setup.check-set") : plugin.getMessage("arena-setup.check-notset");
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent evt) {
        SetupCommands setupCommands = new SetupCommands();
        if (plugin.getPlayerInfo().get(evt.getPlayer()).getMinPlayersChat() != null) {
            if (!evt.getMessage().equalsIgnoreCase("cancel")) {
                setupCommands.setMinPlayers(evt.getPlayer(), new String[]{"setminplayers", plugin.getPlayerInfo().get(evt.getPlayer()).getMinPlayersChat(), evt.getMessage()});
            } else {
                evt.getPlayer().sendMessage(plugin.getMessage("setup-menu.cancelled"));
            }
            plugin.getPlayerInfo().get(evt.getPlayer()).setMinPlayersChat(null);
            evt.setCancelled(true);
        } else if (plugin.getPlayerInfo().get(evt.getPlayer()).getMaxPlayersChat() != null) {
            if (!evt.getMessage().equalsIgnoreCase("cancel")) {
                setupCommands.setMaxPlayers(evt.getPlayer(), new String[]{"setmaxplayers", plugin.getPlayerInfo().get(evt.getPlayer()).getMaxPlayersChat(), evt.getMessage()});
            } else {
                evt.getPlayer().sendMessage(plugin.getMessage("setup-menu.cancelled"));
            }
            plugin.getPlayerInfo().get(evt.getPlayer()).setMaxPlayersChat(null);
            evt.setCancelled(true);
        }
    }


    @EventHandler
    public void onClick(InventoryClickEvent evt) {
        Player player = (Player) evt.getWhoClicked();

        if (!MenuUtils.isMenuClick(evt)) {
            return;
        }

        SetupCommands setupCommands = new SetupCommands();
        String arena = plugin.getInventories().get(player).getArena();
        boolean found = true;
        if (plugin.getInventories().get(player).getInventoryType().equals(MenuType.SETUP)) {
            if (Cooldown.checkCooldown(player)) {
                evt.setCancelled(true);
                return;
            }

            switch (evt.getSlot()) {
                case 0:
                    setupCommands.checkArena(player, new String[]{"check", arena});
                    player.getOpenInventory().close();
                    break;
                case 1:
                    setupCommands.deleteArena(player, new String[]{"delete", arena});
                    player.getOpenInventory().close();
                    break;
                case 2:
                    updateTeamsMenu(arena, player);
                    break;
                case 3:
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        boolean vip = ConfigUtils.getBoolean(arena, ConfigField.VIP);
                        setupCommands.setVip(player, new String[]{"setvip", arena, String.valueOf(!vip)});
                    });
                    player.getOpenInventory().close();
                    break;
                case 4:
                    plugin.getPlayerInfo().get(player).setMaxPlayersChat(null);
                    plugin.getPlayerInfo().get(player).setMinPlayersChat(arena);
                    player.sendMessage(plugin.getMessage("setup-menu.min-players-message"));
                    player.getOpenInventory().close();
                    break;
                case 5:
                    plugin.getPlayerInfo().get(player).setMinPlayersChat(null);
                    plugin.getPlayerInfo().get(player).setMaxPlayersChat(arena);
                    player.sendMessage(plugin.getMessage("setup-menu.max-players-message"));
                    player.getOpenInventory().close();
                    break;
                case 6:
                    setupCommands.setLocation(player, new String[]{"setmainlobby", arena}, "arena-setup.set-main-lobby", ConfigField.MAIN_LOBBY, "sheepquest.setup.setmainlobby");
                    player.getOpenInventory().close();
                    break;
                case 7:
                    setupCommands.setLocation(player, new String[]{"setwaitinglobby", arena}, "arena-setup.set-waiting-lobby", ConfigField.WAITING_LOBBY, "sheepquest.setup.setwaitinglobby");
                    player.getOpenInventory().close();
                    break;
                case 8:
                    setupCommands.setLocation(player, new String[]{"setsheepspawn", arena}, "arena-setup.set-sheep-spawn", ConfigField.SHEEP_SPAWN, "sheepquest.setup.setsheepspawn");
                    player.getOpenInventory().close();
                    break;
                case 9:
                    updateSpawnSetupMenu(arena, player);
                    break;
                case 10:
                    updateAreaSetupMenu(arena, player);
                    break;
                case 22:
                    new ArenasMenu().updateArenasMenu(player);
                    break;
            }
        } else if (plugin.getInventories().get(player).getInventoryType().equals(MenuType.SPAWN_SETUP)
                || plugin.getInventories().get(player).getInventoryType().equals(MenuType.AREA_SETUP)
                || plugin.getInventories().get(player).getInventoryType().equals(MenuType.TEAMS_ADD_REMOVE)) {
            if (Cooldown.checkCooldown(player)) {
                evt.setCancelled(true);
                return;
            }

            if (evt.getSlot() == 22) {
                updateSetupMenu(arena, player);
            } else {
                if (plugin.getInventories().get(player).getInventoryType().equals(MenuType.SPAWN_SETUP)) {
                    setupCommands.setTeamSpawn(player, new String[]{"setspawn", arena, Utils.getTeamByBanner(evt.getCurrentItem()).getCode()});
                } else if (plugin.getInventories().get(player).getInventoryType().equals(MenuType.AREA_SETUP)) {
                    if (evt.getSlot() == 21) {
                        setupCommands.giveWand(player);
                    } else {
                        setupCommands.setTeamArea(player, new String[]{"setteamarea", arena, Utils.getTeamByWool(evt.getCurrentItem()).getCode()});
                    }
                } else if (plugin.getInventories().get(player).getInventoryType().equals(MenuType.TEAMS_ADD_REMOVE)) {
                    Team team = Utils.getTeamByWool(evt.getCurrentItem());
                    if (ConfigUtils.getTeamList(arena).contains(team)) {
                        setupCommands.removeTeam(player, new String[]{"removeteam", arena, team.getCode()});
                    } else {
                        setupCommands.addTeam(player, new String[]{"addteam", arena, team.getCode()});
                    }
                }

                player.getOpenInventory().close();
            }
        } else {
            found = false;
        }

        if (found) {
            Cooldown.addCooldown(player);
            evt.setCancelled(true);
        }
    }
}
