package me.cubixor.sheepquest.menu;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.commands.SetupCommands;
import me.cubixor.sheepquest.gameInfo.PlayerInfo;
import me.cubixor.sheepquest.gameInfo.Team;
import org.bukkit.Bukkit;
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

    public SetupMenu() {
        plugin = SheepQuest.getInstance();
    }

    public void setupMenuCommand(Player player, String[] args) {
        if (!Utils.checkIfValid(player, args, "sheepquest.setup.menu", "setup-menu.command", 2)) {
            return;
        }
        updateSetupMenu(args[1], player);
        player.openInventory(plugin.getInventories().get(player).getSetupInventory());
    }

    public void updateSetupMenu(String arena, Player player) {
        plugin.putInventories(player, arena);
        LinkedHashMap<String, Boolean> check = new LinkedHashMap<>(Utils.checkIfReady(arena));

        Inventory setupInventory = Bukkit.createInventory(null, 27, plugin.getMessage("setup-menu.name").replace("%arena%", arena));
        setupInventory.setItem(0, Utils.setItemStack(XMaterial.COMPASS.parseMaterial(), "setup-menu.check-item-name", "setup-menu.check-item-lore",
                "%ready%", !check.containsValue(false) ? plugin.getMessage("arena-setup.check-yes") : plugin.getMessage("arena-setup.check-no")));
        setupInventory.setItem(1, Utils.setItemStack(XMaterial.LAVA_BUCKET.parseMaterial(), "setup-menu.delete-item-name", "setup-menu.delete-item-lore"));
        setupInventory.setItem(2, Utils.setItemStack(XMaterial.IRON_INGOT.parseMaterial(), "setup-menu.min-players-item-name", "setup-menu.min-players-item-lore",
                "%set%", checkString(check.get("min-players"))));
        setupInventory.setItem(3, Utils.setItemStack(XMaterial.GOLD_INGOT.parseMaterial(), "setup-menu.max-players-item-name", "setup-menu.max-players-item-lore",
                "%set%", checkString(check.get("max-players"))));
        setupInventory.setItem(4, Utils.setItemStack(XMaterial.ENDER_EYE.parseMaterial(), "setup-menu.main-lobby-item-name", "setup-menu.main-lobby-item-lore",
                "%set%", checkString(check.get("main-lobby"))));
        setupInventory.setItem(5, Utils.setItemStack(XMaterial.CLOCK.parseMaterial(), "setup-menu.waiting-lobby-item-name", "setup-menu.waiting-lobby-item-lore",
                "%set%", checkString(check.get("waiting-lobby"))));
        setupInventory.setItem(6, Utils.setItemStack(XMaterial.SHEARS.parseMaterial(), "setup-menu.sheep-spawn-item-name", "setup-menu.sheep-spawn-item-lore",
                "%set%", checkString(check.get("sheep-spawn"))));
        setupInventory.setItem(7, Utils.setItemStack(XMaterial.WHITE_BANNER.parseMaterial(), "setup-menu.team-spawn-item-name", "setup-menu.team-spawn-item-lore",
                "%set%", checkString(check.get("red-spawn") && check.get("green-spawn") && check.get("blue-spawn") && check.get("yellow-spawn"))));
        setupInventory.setItem(8, Utils.setItemStack(XMaterial.WHITE_WOOL.parseMaterial(), "setup-menu.team-area-item-name", "setup-menu.team-area-item-lore",
                "%set%", checkString(check.get("red-area") && check.get("green-area") && check.get("blue-area") && check.get("yellow-area"))));
        setupInventory.setItem(22, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "setup-menu.back-item-name", "setup-menu.back-item-lore"));

        plugin.getInventories().get(player).setSetupInventory(setupInventory);
    }

    public void updateSpawnSetupMenu(String arena, Player player) {
        plugin.putInventories(player, arena);
        LinkedHashMap<String, Boolean> check = new LinkedHashMap<>(Utils.checkIfReady(arena));

        HashMap<Team, ItemStack> banners = new HashMap<>();
        for (Team team : Team.values()) {
            if (!team.equals(Team.NONE)) {
                ItemStack banner = new ItemStack(XMaterial.WHITE_BANNER.parseMaterial(), 1);
                BannerMeta bannerMeta = (BannerMeta) banner.getItemMeta();
                bannerMeta.setBaseColor(Utils.getDyeColor(team));
                banner.setItemMeta(bannerMeta);
                banners.put(team, banner);
            }
        }

        Inventory spawnSetupInventory = Bukkit.createInventory(null, 27, plugin.getMessage("setup-menu.team-spawn-menu-name").replace("%arena%", arena));
        spawnSetupInventory.setItem(1, Utils.setItemStack(banners.get(Team.RED), "setup-menu.red-team-spawn-item-name", "setup-menu.red-team-spawn-item-lore",
                "%set%", checkString(check.get("red-spawn"))));
        spawnSetupInventory.setItem(3, Utils.setItemStack(banners.get(Team.GREEN), "setup-menu.green-team-spawn-item-name", "setup-menu.green-team-spawn-item-lore",
                "%set%", checkString(check.get("green-spawn"))));
        spawnSetupInventory.setItem(5, Utils.setItemStack(banners.get(Team.BLUE), "setup-menu.blue-team-spawn-item-name", "setup-menu.blue-team-spawn-item-lore",
                "%set%", checkString(check.get("blue-spawn"))));
        spawnSetupInventory.setItem(7, Utils.setItemStack(banners.get(Team.YELLOW), "setup-menu.yellow-team-spawn-item-name", "setup-menu.yellow-team-spawn-item-lore",
                "%set%", checkString(check.get("yellow-spawn"))));
        spawnSetupInventory.setItem(22, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "setup-menu.team-spawn-menu-back-item-name", "setup-menu.team-spawn-menu-back-item-lore"));


        plugin.getInventories().get(player).setSpawnSetupInventory(spawnSetupInventory);

    }

    public void updateAreaSetupMenu(String arena, Player player) {
        plugin.putInventories(player, arena);
        LinkedHashMap<String, Boolean> check = new LinkedHashMap<>(Utils.checkIfReady(arena));

        Inventory areaSetupInventory = Bukkit.createInventory(null, 27, plugin.getMessage("setup-menu.team-area-menu-name").replace("%arena%", arena));
        areaSetupInventory.setItem(1, Utils.setItemStack(Utils.getTeamWool(Team.RED), "setup-menu.red-team-area-item-name", "setup-menu.red-team-area-item-lore",
                "%set%", checkString(check.get("red-area"))));
        areaSetupInventory.setItem(3, Utils.setItemStack(Utils.getTeamWool(Team.GREEN), "setup-menu.green-team-area-item-name", "setup-menu.green-team-area-item-lore",
                "%set%", checkString(check.get("green-area"))));
        areaSetupInventory.setItem(5, Utils.setItemStack(Utils.getTeamWool(Team.BLUE), "setup-menu.blue-team-area-item-name", "setup-menu.blue-team-area-item-lore",
                "%set%", checkString(check.get("blue-area"))));
        areaSetupInventory.setItem(7, Utils.setItemStack(Utils.getTeamWool(Team.YELLOW), "setup-menu.yellow-team-area-item-name", "setup-menu.yellow-team-area-item-lore",
                "%set%", checkString(check.get("yellow-area"))));
        areaSetupInventory.setItem(22, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "setup-menu.team-area-menu-back-item-name", "setup-menu.team-area-menu-back-item-lore"));

        areaSetupInventory.setItem(21, Utils.setItemStack(XMaterial.BLAZE_ROD.parseMaterial(), "setup-menu.wand-item-name", "setup-menu.wand-item-lore"));


        plugin.getInventories().get(player).setAreaSetupInventory(areaSetupInventory);
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
    public void onJoin(PlayerJoinEvent evt) {
        plugin.getPlayerInfo().put(evt.getPlayer(), new PlayerInfo());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent evt) {
        plugin.getPlayerInfo().remove(evt.getPlayer());
        plugin.getInventories().remove(evt.getPlayer());
    }

    @EventHandler
    public void onClick(InventoryClickEvent evt) {
        Player player = (Player) evt.getWhoClicked();
        if (!plugin.getInventories().containsKey(player)) {
            return;
        }
        if (evt.getCurrentItem() == null) {
            return;
        }
        SetupCommands setupCommands = new SetupCommands();
        String arena = plugin.getInventories().get(player).getArena();
        boolean found = true;
        if (evt.getClickedInventory().equals(plugin.getInventories().get(player).getSetupInventory())) {
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
                    plugin.getPlayerInfo().get(player).setMaxPlayersChat(null);
                    plugin.getPlayerInfo().get(player).setMinPlayersChat(arena);
                    player.sendMessage(plugin.getMessage("setup-menu.min-players-message"));
                    player.getOpenInventory().close();
                    break;
                case 3:
                    plugin.getPlayerInfo().get(player).setMinPlayersChat(null);
                    plugin.getPlayerInfo().get(player).setMaxPlayersChat(arena);
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
                    player.openInventory(plugin.getInventories().get(player).getSpawnSetupInventory());
                    break;
                case 8:
                    updateAreaSetupMenu(arena, player);
                    player.openInventory(plugin.getInventories().get(player).getAreaSetupInventory());
                    break;
                case 22:
                    new ArenasMenu().updateArenasMenu(player);
                    player.openInventory(plugin.getInventories().get(player).getArenasInventory());
                    break;
            }
        } else if (evt.getClickedInventory().equals(plugin.getInventories().get(player).getSpawnSetupInventory()) || evt.getClickedInventory().equals(plugin.getInventories().get(player).getAreaSetupInventory())) {
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
                player.openInventory(plugin.getInventories().get(player).getSetupInventory());
            } else {
                if (evt.getClickedInventory().equals(plugin.getInventories().get(player).getSpawnSetupInventory())) {
                    setupCommands.setTeamSpawn(player, new String[]{"setspawn", arena, team});
                } else if (evt.getClickedInventory().equals(plugin.getInventories().get(player).getAreaSetupInventory())) {
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
