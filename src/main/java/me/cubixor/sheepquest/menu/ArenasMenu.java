package me.cubixor.sheepquest.menu;

import me.cubixor.sheepquest.Arena;
import me.cubixor.sheepquest.GameState;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.Utils;
import me.cubixor.sheepquest.commands.PlayCommands;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

public class ArenasMenu implements Listener {

    private final SheepQuest plugin;

    public ArenasMenu(SheepQuest s) {
        plugin = s;
    }

    public void arenasMenuCommand(Player player, String[] args) {
        Utils utils = new Utils(plugin);
        if (!utils.checkIfValid(player, args, "sheepquest.play.menu", "arenas-menu.command", 1)) {
            return;
        }
        updateArenasMenu(player);
        player.openInventory(plugin.inventories.get(player).arenasInventory);
    }


    public void updateArenasMenu(Player player) {
        Utils utils = new Utils(plugin);
        plugin.putInventories(player, null);
        plugin.inventories.get(player).arenaSlot = new HashMap<>();

        Inventory arenasInventory = Bukkit.createInventory(null, 54, plugin.getMessage("arenas-menu.name"));

        int slot = 0;
        for (String arena : plugin.arenas.keySet()) {
            Arena arenaObject = plugin.arenas.get(arena);
            Material material = null;
            if (plugin.getConfig().getBoolean("color-signs")) {
                if (!plugin.getArenasConfig().getBoolean("Arenas." + arena + ".active")) {
                    material = Material.matchMaterial(plugin.getConfig().getString("sign-colors.inactive"));
                } else if (arenaObject.state.equals(GameState.WAITING)) {
                    material = Material.matchMaterial(plugin.getConfig().getString("sign-colors.waiting"));
                } else if (arenaObject.state.equals(GameState.STARTING)) {
                    material = Material.matchMaterial(plugin.getConfig().getString("sign-colors.starting"));
                } else if (arenaObject.state.equals(GameState.GAME)) {
                    material = Material.matchMaterial(plugin.getConfig().getString("sign-colors.ingame"));
                } else if (arenaObject.state.equals(GameState.ENDING)) {
                    material = Material.matchMaterial(plugin.getConfig().getString("sign-colors.ending"));
                }
            }

            ItemStack arenaItem = arenaItemStack(material, arena, "arenas-menu.arena-item-name", "arenas-menu.arena-item-lore");

            arenasInventory.setItem(slot, arenaItem);

            plugin.inventories.get(player).arenaSlot.put(slot, arena);
            slot++;
        }

        arenasInventory.setItem(48, utils.setItemStack(Material.SLIME_BALL, "arenas-menu.quickjoin-item-name", "arenas-menu.quickjoin-item-lore"));
        arenasInventory.setItem(49, utils.setItemStack(Material.ARROW, "arenas-menu.close-item-name", "arenas-menu.close-item-lore"));
        arenasInventory.setItem(50, utils.setItemStack(Material.DIAMOND, "arenas-menu.stats-item-name", "arenas-menu.stats-item-lore"));


        plugin.inventories.get(player).arenasInventory = arenasInventory;
    }

    public void updateOptionsMenu(String arena, Player player) {
        plugin.putInventories(player, arena);
        Utils utils = new Utils(plugin);

        Inventory optionsInventory = Bukkit.createInventory(null, 27, plugin.getMessage("arenas-menu.name"));

        optionsInventory.setItem(2, arenaItemStack(Material.NETHER_STAR, arena, "arenas-menu.play-item-name", "arenas-menu.play-item-lore"));
        optionsInventory.setItem(4, utils.setItemStack(Material.ENDER_PEARL, "arenas-menu.staff-item-name", "arenas-menu.staff-item-lore"));
        optionsInventory.setItem(6, utils.setItemStack(Material.ENDER_EYE, "arenas-menu.setup-item-name", "arenas-menu.setup-item-lore"));

        optionsInventory.setItem(22, utils.setItemStack(Material.ARROW, "arenas-menu.options-menu-back-item-name", "arenas-menu.options-menu-back-item-lore"));

        plugin.inventories.get(player).optionsInventory = optionsInventory;
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
        if (plugin.inventories.get(player).arenasInventory != null && evt.getClickedInventory().equals(plugin.inventories.get(player).arenasInventory)) {
            switch (evt.getSlot()) {
                case 48:
                    new PlayCommands(plugin).quickJoin(player);
                    player.getOpenInventory().close();
                    break;
                case 49:
                    player.getOpenInventory().close();
                    break;
                case 50:
                    new PlayCommands(plugin).stats(player);
                    player.getOpenInventory().close();
                    break;
                default:
                    if (evt.getClick().equals(ClickType.LEFT)) {
                        String arena = plugin.inventories.get(player).arenaSlot.get(evt.getSlot());
                        new PlayCommands(plugin).join(player, new String[]{"join", arena});
                        player.getOpenInventory().close();
                    } else if (evt.getClick().equals(ClickType.RIGHT)) {
                        updateOptionsMenu(plugin.inventories.get(player).arenaSlot.get(evt.getSlot()), player);
                        plugin.inventories.get(player).arena = plugin.inventories.get(player).arenaSlot.get(evt.getSlot());
                        player.openInventory(plugin.inventories.get(player).optionsInventory);
                    }
                    break;
            }
            evt.setCancelled(true);
        } else if (evt.getClickedInventory().equals(plugin.inventories.get(player).optionsInventory)) {
            String arena = plugin.inventories.get(player).arena;
            switch (evt.getSlot()) {
                case 2:
                    new PlayCommands(plugin).join(player, new String[]{"join", arena});
                    player.getOpenInventory().close();
                    break;
                case 4:
                    new StaffMenu(plugin).updateStaffMenu(arena, player);
                    player.openInventory(plugin.inventories.get(player).staffInventory);
                    break;
                case 6:
                    new SetupMenu(plugin).updateSetupMenu(arena, player);
                    player.openInventory(plugin.inventories.get(player).setupInventory);
                    break;
                case 22:
                    updateArenasMenu(player);
                    player.openInventory(plugin.inventories.get(player).arenasInventory);
                    break;
            }

            evt.setCancelled(true);
        }
    }

    private ItemStack arenaItemStack(Material material, String arena, String namePath, String lorePath) {
        Utils utils = new Utils(plugin);
        Arena arenaObject = plugin.arenas.get(arena);

        ItemStack arenaItem = new ItemStack(material, 1);
        ItemMeta arenaItemItemMeta = arenaItem.getItemMeta();
        String vip = plugin.getConfig().getStringList("vip-arenas").contains(arena) ? plugin.getMessage("general.vip-prefix") : "";
        arenaItemItemMeta.setDisplayName(plugin.getMessage(namePath).replace("%arena%", arena).replace("%?vip?%", vip));
        List<String> playerItemLore = new ArrayList<>(plugin.getMessageList(lorePath));
        for (String s : playerItemLore) {
            String replaced = s.replace("%count%", Integer.toString(arenaObject.playerTeam.keySet().size()));
            replaced = replaced.replace("%max%", Integer.toString(plugin.getArenasConfig().getInt("Arenas." + arena + ".max-players")));
            replaced = replaced.replace("%state%", utils.getStringState(arenaObject));
            Collections.replaceAll(playerItemLore, s, replaced);
        }
        arenaItemItemMeta.setLore(playerItemLore);
        arenaItem.setItemMeta(arenaItemItemMeta);
        return arenaItem;
    }
}
