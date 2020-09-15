package me.cubixor.sheepquest.menu;

import me.cubixor.sheepquest.PlayerInfo;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.Team;
import me.cubixor.sheepquest.Utils;
import me.cubixor.sheepquest.commands.SetupCommands;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class SetupMenu implements Listener {

    private final SheepQuest plugin;

    public SetupMenu(SheepQuest s) {
        plugin = s;
    }

    public void setupMenuCommand(Player player, String[] args) {
        Utils utils = new Utils(plugin);
        if (!utils.checkIfValid(player, args, "sheepquest.setup.menu", "setup-menu.command", 2)) {
            return;
        }
        updateSetupMenu(args[1], player);
        player.openInventory(plugin.inventories.get(player).setupInventory);
    }

    public void updateSetupMenu(String arena, Player player) {
        plugin.putInventories(player, arena);
        Utils utils = new Utils(plugin);
        LinkedHashMap<String, Boolean> check = new LinkedHashMap<>(utils.checkIfReady(arena));

        Inventory setupInventory = Bukkit.createInventory(null, 27, plugin.getMessage("setup-menu.name").replace("%arena%", arena));
        setupInventory.setItem(0, utils.setItemStack(Material.COMPASS, "setup-menu.check-item-name", "setup-menu.check-item-lore",
                "%ready%", !check.containsValue(false) ? plugin.getMessage("arena-setup.check-yes") : plugin.getMessage("arena-setup.check-no")));
        setupInventory.setItem(1, utils.setItemStack(Material.LAVA_BUCKET, "setup-menu.delete-item-name", "setup-menu.delete-item-lore"));
        setupInventory.setItem(2, utils.setItemStack(Material.IRON_INGOT, "setup-menu.min-players-item-name", "setup-menu.min-players-item-lore",
                "%set%", checkString(check.get("min-players"))));
        setupInventory.setItem(3, utils.setItemStack(Material.GOLD_INGOT, "setup-menu.max-players-item-name", "setup-menu.max-players-item-lore",
                "%set%", checkString(check.get("max-players"))));
        setupInventory.setItem(4, utils.setItemStack(Material.EYE_OF_ENDER, "setup-menu.main-lobby-item-name", "setup-menu.main-lobby-item-lore",
                "%set%", checkString(check.get("main-lobby"))));
        setupInventory.setItem(5, utils.setItemStack(Material.WATCH, "setup-menu.waiting-lobby-item-name", "setup-menu.waiting-lobby-item-lore",
                "%set%", checkString(check.get("waiting-lobby"))));
        setupInventory.setItem(6, utils.setItemStack(Material.SHEARS, "setup-menu.sheep-spawn-item-name", "setup-menu.sheep-spawn-item-lore",
                "%set%", checkString(check.get("sheep-spawn"))));
        setupInventory.setItem(7, utils.setItemStack(Material.BANNER, "setup-menu.team-spawn-item-name", "setup-menu.team-spawn-item-lore",
                "%set%", checkString(check.get("red-spawn") && check.get("green-spawn") && check.get("blue-spawn") && check.get("yellow-spawn"))));
        setupInventory.setItem(8, utils.setItemStack(Material.WOOL, "setup-menu.team-area-item-name", "setup-menu.team-area-item-lore",
                "%set%", checkString(check.get("red-area") && check.get("green-area") && check.get("blue-area") && check.get("yellow-area"))));
        setupInventory.setItem(22, utils.setItemStack(Material.ARROW, "setup-menu.back-item-name", "setup-menu.back-item-lore"));

        plugin.inventories.get(player).setupInventory = setupInventory;
    }

    public void updateSpawnSetupMenu(String arena, Player player) {
        plugin.putInventories(player, arena);
        Utils utils = new Utils(plugin);
        LinkedHashMap<String, Boolean> check = new LinkedHashMap<>(utils.checkIfReady(arena));

        HashMap<Team, ItemStack> banners = new HashMap<>();
        for (Team team : Team.values()) {
            if (!team.equals(Team.NONE)) {
                ItemStack banner = new ItemStack(Material.BANNER, 1);
                BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();
                bannerMeta.setBaseColor(utils.getDyeColor(team));
                banner.setItemMeta(bannerMeta);
                banners.put(team, banner);
            }
        }

        Inventory spawnSetupInventory = Bukkit.createInventory(null, 27, plugin.getMessage("setup-menu.team-spawn-menu-name").replace("%arena%", arena));
        spawnSetupInventory.setItem(1, utils.setItemStack(banners.get(Team.RED), "setup-menu.red-team-spawn-item-name", "setup-menu.red-team-spawn-item-lore",
                "%set%", checkString(check.get("red-spawn"))));
        spawnSetupInventory.setItem(3, utils.setItemStack(banners.get(Team.GREEN), "setup-menu.green-team-spawn-item-name", "setup-menu.green-team-spawn-item-lore",
                "%set%", checkString(check.get("green-spawn"))));
        spawnSetupInventory.setItem(5, utils.setItemStack(banners.get(Team.BLUE), "setup-menu.blue-team-spawn-item-name", "setup-menu.blue-team-spawn-item-lore",
                "%set%", checkString(check.get("blue-spawn"))));
        spawnSetupInventory.setItem(7, utils.setItemStack(banners.get(Team.YELLOW), "setup-menu.yellow-team-spawn-item-name", "setup-menu.yellow-team-spawn-item-lore",
                "%set%", checkString(check.get("yellow-spawn"))));
        spawnSetupInventory.setItem(22, utils.setItemStack(Material.ARROW, "setup-menu.team-spawn-menu-back-item-name", "setup-menu.team-spawn-menu-back-item-lore"));


        plugin.inventories.get(player).spawnSetupInventory = spawnSetupInventory;

    }

    public void updateAreaSetupMenu(String arena, Player player) {
        plugin.putInventories(player, arena);
        Utils utils = new Utils(plugin);
        LinkedHashMap<String, Boolean> check = new LinkedHashMap<>(utils.checkIfReady(arena));

        Inventory areaSetupInventory = Bukkit.createInventory(null, 27, plugin.getMessage("setup-menu.team-area-menu-name").replace("%arena%", arena));
        areaSetupInventory.setItem(1, utils.setItemStack(utils.getTeamWool(Team.RED), "setup-menu.red-team-area-item-name", "setup-menu.red-team-area-item-lore",
                "%set%", checkString(check.get("red-area"))));
        areaSetupInventory.setItem(3, utils.setItemStack(utils.getTeamWool(Team.GREEN), "setup-menu.green-team-area-item-name", "setup-menu.green-team-area-item-lore",
                "%set%", checkString(check.get("green-area"))));
        areaSetupInventory.setItem(5, utils.setItemStack(utils.getTeamWool(Team.BLUE), "setup-menu.blue-team-area-item-name", "setup-menu.blue-team-area-item-lore",
                "%set%", checkString(check.get("blue-area"))));
        areaSetupInventory.setItem(7, utils.setItemStack(utils.getTeamWool(Team.YELLOW), "setup-menu.yellow-team-area-item-name", "setup-menu.yellow-team-area-item-lore",
                "%set%", checkString(check.get("yellow-area"))));
        areaSetupInventory.setItem(22, utils.setItemStack(Material.ARROW, "setup-menu.team-area-menu-back-item-name", "setup-menu.team-area-menu-back-item-lore"));

        areaSetupInventory.setItem(21, utils.setItemStack(Material.BLAZE_ROD, "setup-menu.wand-item-name", "setup-menu.wand-item-lore"));


        plugin.inventories.get(player).areaSetupInventory = areaSetupInventory;
    }

    private String checkString(boolean set) {
        return set ? plugin.getMessage("arena-setup.check-set") : plugin.getMessage("arena-setup.check-notset");
    }


    @EventHandler
    public void onChat(AsyncPlayerChatEvent evt) {
        SetupCommands setupCommands = new SetupCommands(plugin);
        if (plugin.playerInfo.get(evt.getPlayer()).minPlayersChat != null) {
            if (!evt.getMessage().equalsIgnoreCase("cancel")) {
                setupCommands.setMinPlayers(evt.getPlayer(), new String[]{"setminplayers", plugin.playerInfo.get(evt.getPlayer()).minPlayersChat, evt.getMessage()});
            } else {
                evt.getPlayer().sendMessage(plugin.getMessage("setup-menu.cancelled"));
            }
            plugin.playerInfo.get(evt.getPlayer()).minPlayersChat = null;
            evt.setCancelled(true);
        } else if (plugin.playerInfo.get(evt.getPlayer()).maxPlayersChat != null) {
            if (!evt.getMessage().equalsIgnoreCase("cancel")) {
                setupCommands.setMaxPlayers(evt.getPlayer(), new String[]{"setmaxplayers", plugin.playerInfo.get(evt.getPlayer()).maxPlayersChat, evt.getMessage()});
            } else {
                evt.getPlayer().sendMessage(plugin.getMessage("setup-menu.cancelled"));
            }
            plugin.playerInfo.get(evt.getPlayer()).maxPlayersChat = null;
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        plugin.playerInfo.put(evt.getPlayer(), new PlayerInfo());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent evt) {
        plugin.playerInfo.remove(evt.getPlayer());
        plugin.inventories.remove(evt.getPlayer());
    }

    @EventHandler
    public void onClick(InventoryClickEvent evt) {
        Player player = (Player) evt.getWhoClicked();
        if (!plugin.inventories.containsKey(player)) {
            return;
        }
        if (evt.getCurrentItem() == null) {
            return;
        }
        SetupCommands setupCommands = new SetupCommands(plugin);
        String arena = plugin.inventories.get(player).arena;
        boolean found = true;
        if (evt.getClickedInventory().equals(plugin.inventories.get(player).setupInventory)) {
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
                    plugin.playerInfo.get(player).maxPlayersChat = null;
                    plugin.playerInfo.get(player).minPlayersChat = arena;
                    player.sendMessage(plugin.getMessage("setup-menu.min-players-message"));
                    player.getOpenInventory().close();
                    break;
                case 3:
                    plugin.playerInfo.get(player).minPlayersChat = null;
                    plugin.playerInfo.get(player).maxPlayersChat = arena;
                    player.sendMessage(plugin.getMessage("setup-menu.max-players-message"));
                    player.getOpenInventory().close();
                    break;
                case 4:
                    setupCommands.setLocation(player, new String[]{"setmainlobby", arena}, "arena-setup.set-main-lobby", "main-lobby", "sheepquest.setup.setmainlobby");
                    player.getOpenInventory().close();
                    break;
                case 5:
                    setupCommands.setLocation(player, new String[]{"setwaitinglobby", arena}, "arena-setup.set-waiting-lobby", "waiting-lobby", "sheepquest.setup.setwaitinglobby");
                    player.getOpenInventory().close();
                    break;
                case 6:
                    setupCommands.setLocation(player, new String[]{"setsheepspawn", arena}, "arena-setup.set-sheep-spawn", "sheep-spawn", "sheepquest.setup.setsheepspawn");
                    player.getOpenInventory().close();
                    break;
                case 7:
                    updateSpawnSetupMenu(arena, player);
                    player.openInventory(plugin.inventories.get(player).spawnSetupInventory);
                    break;
                case 8:
                    updateAreaSetupMenu(arena, player);
                    player.openInventory(plugin.inventories.get(player).areaSetupInventory);
                    break;
                case 22:
                    new ArenasMenu(plugin).updateArenasMenu(player);
                    player.openInventory(plugin.inventories.get(player).arenasInventory);
                    break;
            }
        } else if (evt.getClickedInventory().equals(plugin.inventories.get(player).spawnSetupInventory) || evt.getClickedInventory().equals(plugin.inventories.get(player).areaSetupInventory)) {
            String team = null;
            switch (evt.getSlot()) {
                case 1:
                    team = "red";
                    break;
                case 3:
                    team = "green";
                    break;
                case 5:
                    team = "blue";
                    break;
                case 7:
                    team = "yellow";
                    break;
            }
            if (evt.getSlot() == 22) {
                updateSetupMenu(arena, player);
                player.openInventory(plugin.inventories.get(player).setupInventory);
            } else {
                if (evt.getClickedInventory().equals(plugin.inventories.get(player).spawnSetupInventory)) {
                    setupCommands.setTeamSpawn(player, new String[]{"setspawn", arena, team});
                } else if (evt.getClickedInventory().equals(plugin.inventories.get(player).areaSetupInventory)) {
                    if (evt.getSlot() == 21) {
                        setupCommands.giveWand(player);
                    } else {
                        setupCommands.setTeamArea(player, new String[]{"setteamarea", arena, team});
                    }
                }

                player.getOpenInventory().close();
            }
        } else {
            found = false;
        }

        if (found) {
            evt.setCancelled(true);
        }
    }

}
