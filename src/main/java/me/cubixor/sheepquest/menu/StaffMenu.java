package me.cubixor.sheepquest.menu;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.commands.StaffCommands;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.PlayerGameStats;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class StaffMenu implements Listener {

    private final SheepQuest plugin;

    public StaffMenu() {
        plugin = SheepQuest.getInstance();
    }

    public void staffMenuCommand(Player player, String[] args) {
        if (!Utils.checkIfValid(player, args, "sheepquest.staff.menu", "staff-menu.command", 2)) {
            return;
        }
        updateStaffMenu(args[1], player);
        player.openInventory(plugin.getInventories().get(player).getStaffInventory());
    }


    public void updateStaffMenu(String arena, Player player) {
        plugin.putInventories(player, arena);

        boolean active = plugin.getArenasConfig().getBoolean("Arenas." + arena + ".active");
        String gameState = Utils.getStringState(plugin.getArenas().get(arena));
        int count = plugin.getArenas().get(arena).getPlayers().keySet().size();

        Inventory staffInventory = Bukkit.createInventory(null, 18, plugin.getMessage("staff-menu.name").replace("%arena%", arena));
        staffInventory.setItem(0, Utils.setItemStack(XMaterial.NETHER_STAR.parseMaterial(), "staff-menu.active-item-name", "staff-menu.active-item-lore",
                "%active%", active ? plugin.getMessage("staff-menu.state-active") : plugin.getMessage("staff-menu.state-not-active")));
        staffInventory.setItem(1, Utils.setItemStack(XMaterial.SLIME_BALL.parseMaterial(), "staff-menu.start-item-name", "staff-menu.start-item-lore",
                "%state%", gameState));
        staffInventory.setItem(2, Utils.setItemStack(XMaterial.MAGMA_CREAM.parseMaterial(), "staff-menu.stop-item-name", "staff-menu.stop-item-lore",
                "%state%", gameState));
        staffInventory.setItem(3, Utils.setItemStack(XMaterial.PLAYER_HEAD.parseMaterial(), "staff-menu.players-item-name", "staff-menu.players-item-lore",
                "%players%", Integer.toString(count)));

        staffInventory.setItem(13, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "staff-menu.back-item-name", "staff-menu.back-item-lore"));

        plugin.getInventories().get(player).setStaffInventory(staffInventory);
    }

    public void updatePlayersMenu(String arenaString, Player player) {
        plugin.putInventories(player, arenaString);
        Arena arena = plugin.getArenas().get(arenaString);

        Inventory playersInventory = Bukkit.createInventory(null, 54, plugin.getMessage("staff-menu.players-menu-name").replace("%arena%", arenaString));
        List<Player> playerList = new ArrayList<>(arena.getPlayers().keySet());
        int slot = 0;
        plugin.getInventories().get(player).setPlayerSlot(new HashMap<>());
        if (!arena.getPlayers().keySet().isEmpty()) {
            for (Player p : playerList) {
                ItemStack playerItem = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial());
                ItemMeta skullMeta = playerItem.getItemMeta();
                skullMeta.setDisplayName(plugin.getMessage("staff-menu.player-item-name").replace("%nick%", p.getName()));
                List<String> playerItemLore = new ArrayList<>(plugin.getMessageList("staff-menu.player-item-lore"));
                for (String s : playerItemLore) {
                    PlayerGameStats playerGameStats = arena.getPlayerStats().get(p);
                    String replaced = s.replace("%team%", plugin.getMessage("general.team-" + Utils.getTeamString(arena.getPlayers().get(p))));
                    replaced = replaced.replace("%sheep%", playerGameStats == null ? "0" : Integer.toString(arena.getPlayerStats().get(p).getSheepTaken()));
                    replaced = replaced.replace("%kills%", playerGameStats == null ? "0" : Integer.toString(arena.getPlayerStats().get(p).getKills()));
                    replaced = replaced.replace("%deaths%", playerGameStats == null ? "0" : Integer.toString(arena.getPlayerStats().get(p).getDeaths()));
                    Collections.replaceAll(playerItemLore, s, replaced);
                }
                skullMeta.setLore(playerItemLore);
                playerItem.setItemMeta(skullMeta);
                playersInventory.setItem(slot, playerItem);
                plugin.getInventories().get(player).getPlayerSlot().put(slot, p);
                slot++;
            }
        }

        playersInventory.setItem(49, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "staff-menu.players-menu-back-item-name", "staff-menu.players-menu-back-item-lore"));

        plugin.getInventories().get(player).setPlayersInventory(playersInventory);

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
        StaffCommands staffCommands = new StaffCommands();
        String arena = plugin.getInventories().get(player).getArena();
        if (evt.getClickedInventory().equals(plugin.getInventories().get(player).getStaffInventory())) {
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
                    player.openInventory(plugin.getInventories().get(player).getPlayersInventory());
                    break;
                case 13:
                    new ArenasMenu().updateArenasMenu(player);
                    player.openInventory(plugin.getInventories().get(player).getArenasInventory());
                    break;
            }
            evt.setCancelled(true);
        } else if (evt.getClickedInventory().equals(plugin.getInventories().get(player).getPlayersInventory())) {
            if (evt.getSlot() == 49) {
                updateStaffMenu(arena, player);
                player.openInventory(plugin.getInventories().get(player).getStaffInventory());
            }
            if (evt.getCurrentItem().getType().equals(XMaterial.PLAYER_HEAD.parseMaterial())) {
                Player skullOwner = plugin.getInventories().get(player).getPlayerSlot().get(evt.getSlot());
                if (evt.getClick().equals(ClickType.RIGHT)) {
                    new StaffCommands().kick(player, new String[]{"kick", skullOwner.getName()});
                    updatePlayersMenu(arena, player);
                    player.openInventory(plugin.getInventories().get(player).getPlayersInventory());
                } else if (evt.getClick().equals(ClickType.LEFT)) {
                    player.teleport(skullOwner);
                    player.closeInventory();
                }
            }
            evt.setCancelled(true);
        }
    }
}
