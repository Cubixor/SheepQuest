package me.cubixor.sheepquest.menu;

import me.cubixor.sheepquest.Arena;
import me.cubixor.sheepquest.PlayerGameStats;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.Utils;
import me.cubixor.sheepquest.commands.PlayCommands;
import me.cubixor.sheepquest.commands.StaffCommands;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class StaffMenu implements Listener {

    private final SheepQuest plugin;

    public StaffMenu(SheepQuest s) {
        plugin = s;
    }

    public void staffMenuCommand(Player player, String[] args){
        Utils utils = new Utils(plugin);
        if(!utils.checkIfValid(player, args, "sheepquest.staff.menu", "staff-menu.command", 2)){
            return;
        }
        updateStaffMenu(args[1], player);
        player.openInventory(plugin.inventories.get(player).staffInventory);
    }


    public void updateStaffMenu(String arena, Player player) {
        plugin.putInventories(player, arena);
        Utils utils = new Utils(plugin);

        boolean active = plugin.getArenasConfig().getBoolean("Arenas." + arena + ".active");
        String gameState = utils.getStringState(plugin.arenas.get(arena));
        int count = plugin.arenas.get(arena).playerTeam.keySet().size();

        Inventory staffInventory = Bukkit.createInventory(null, 27, plugin.getMessage("staff-menu.name").replace("%arena%", arena));
        staffInventory.setItem(0, utils.setItemStack(Material.NETHER_STAR, "staff-menu.active-item-name", "staff-menu.active-item-lore",
                "%active%", active ? plugin.getMessage("staff-menu.state-active") : plugin.getMessage("staff-menu.state-not-active")));
        staffInventory.setItem(1, utils.setItemStack(Material.SLIME_BALL, "staff-menu.start-item-name", "staff-menu.start-item-lore",
                "%state%", gameState));
        staffInventory.setItem(2, utils.setItemStack(Material.MAGMA_CREAM, "staff-menu.stop-item-name", "staff-menu.stop-item-lore",
                "%state%", gameState));
        staffInventory.setItem(3, utils.setItemStack(Material.PLAYER_HEAD, "staff-menu.players-item-name", "staff-menu.players-item-lore",
                "%players%", Integer.toString(count)));

        staffInventory.setItem(22, utils.setItemStack(Material.ARROW, "staff-menu.back-item-name", "staff-menu.back-item-lore"));

        plugin.inventories.get(player).staffInventory = staffInventory;
    }

    public void updatePlayersMenu(String arenaString, Player player) {
        plugin.putInventories(player, arenaString);
        Utils utils = new Utils(plugin);
        Arena arena = plugin.arenas.get(arenaString);

        Inventory playersInventory = Bukkit.createInventory(null, 54, plugin.getMessage("staff-menu.players-menu-name").replace("%arena%", arenaString));
        List<Player> playerList = new ArrayList<>(arena.playerTeam.keySet());
        int slot = 0;
        plugin.inventories.get(player).playerSlot = new HashMap<>();
        if (!arena.playerTeam.keySet().isEmpty()) {
            for (Player p : playerList) {
                ItemStack playerItem = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) playerItem.getItemMeta();
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
                skullMeta.setDisplayName(plugin.getMessage("staff-menu.player-item-name").replace("%nick%", p.getName()));
                List<String> playerItemLore = new ArrayList<>(plugin.getMessageList("staff-menu.player-item-lore"));
                for (String s : playerItemLore) {
                    PlayerGameStats playerGameStats = arena.playerStats.get(p);
                    String replaced = s.replace("%team%", plugin.getMessage("general.team-" + utils.getTeamString(arena.playerTeam.get(p))));
                    replaced = replaced.replace("%sheep%", playerGameStats == null ? "0" : Integer.toString(arena.playerStats.get(p).sheepTaken));
                    replaced = replaced.replace("%kills%", playerGameStats == null ? "0" : Integer.toString(arena.playerStats.get(p).kills));
                    replaced = replaced.replace("%deaths%", playerGameStats == null ? "0" : Integer.toString(arena.playerStats.get(p).deaths));
                    Collections.replaceAll(playerItemLore, s, replaced);
                }
                skullMeta.setLore(playerItemLore);
                playerItem.setItemMeta(skullMeta);
                playersInventory.setItem(slot, playerItem);
                plugin.inventories.get(player).playerSlot.put(slot, p);
                slot++;
            }
        }

        playersInventory.setItem(49, utils.setItemStack(Material.ARROW, "staff-menu.players-menu-back-item-name", "staff-menu.players-menu-back-item-lore"));

        plugin.inventories.get(player).playersInventory = playersInventory;
    }

    @EventHandler
    public void onClick(InventoryClickEvent evt) {
        Player player = (Player) evt.getWhoClicked();
        if (!plugin.inventories.containsKey(player)) {
            return;
        }
        if(evt.getCurrentItem() == null){
            return;
        }
        StaffCommands staffCommands = new StaffCommands(plugin);
        String arena = plugin.inventories.get(player).arena;
        if (evt.getClickedInventory().equals(plugin.inventories.get(player).staffInventory)) {
            switch (evt.getSlot()) {
                case 0:
                    boolean active = plugin.getArenasConfig().getBoolean("Arenas." + arena + ".active");
                    staffCommands.setActive(player, new String[]{"active", arena, active ? "false" : "true"});
                    player.closeInventory();
                    break;
                case 1:
                    staffCommands.forceStart(player, new String[]{"forcestart", arena});
                    player.closeInventory();
                    break;
                case 2:
                    staffCommands.forceStop(player, new String[]{"forcestop", arena});
                    player.closeInventory();
                    break;
                case 3:
                    updatePlayersMenu(arena, player);
                    player.openInventory(plugin.inventories.get(player).playersInventory);
                    break;
                case 22:
                    new ArenasMenu(plugin).updateArenasMenu(player);
                    player.openInventory(plugin.inventories.get(player).arenasInventory);
                    break;
            }
            evt.setCancelled(true);
        } else if (evt.getClickedInventory().equals(plugin.inventories.get(player).playersInventory)) {
            if (evt.getSlot() == 49) {
                updateStaffMenu(arena, player);
                player.openInventory(plugin.inventories.get(player).staffInventory);
            }
            if (evt.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
                Player skullOwner = plugin.inventories.get(player).playerSlot.get(evt.getSlot());
                if (evt.getClick().equals(ClickType.RIGHT)) {
                    new StaffCommands(plugin).kick(player, new String[]{"kick", skullOwner.getName()});
                    updatePlayersMenu(arena, player);
                    player.openInventory(plugin.inventories.get(player).playersInventory);
                } else if (evt.getClick().equals(ClickType.LEFT)) {
                    player.teleport(skullOwner);
                    player.closeInventory();
                }
            }
            evt.setCancelled(true);
        }
    }
}
