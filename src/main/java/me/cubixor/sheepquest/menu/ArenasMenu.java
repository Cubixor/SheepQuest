package me.cubixor.sheepquest.menu;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.commands.PlayCommands;
import me.cubixor.sheepquest.gameInfo.Arena;
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

    public ArenasMenu() {
        plugin = SheepQuest.getInstance();
    }

    public void arenasMenuCommand(Player player, String[] args) {
        if (!Utils.checkIfValid(player, args, "sheepquest.play.menu", "arenas-menu.command", 1)) {
            return;
        }
        updateArenasMenu(player);
        player.openInventory(plugin.getInventories().get(player).getArenasInventory());
    }


    public void updateArenasMenu(Player player) {
        plugin.putInventories(player, null);
        plugin.getInventories().get(player).setArenaSlot(new HashMap<>());

        Inventory arenasInventory = Bukkit.createInventory(null, 54, plugin.getMessage("arenas-menu.name"));

        int slot = 0;
        for (String arena : plugin.getArenas().keySet()) {
            Arena arenaObject = plugin.getArenas().get(arena);
            ItemStack material;
            if (plugin.getConfig().getBoolean("color-signs")) {
                material = Utils.setGlassColor(arenaObject);
            } else {
                material = new ItemStack(XMaterial.NETHER_STAR.parseMaterial());
            }


            ItemStack arenaItem = arenaItemStack(material, arena, "arenas-menu.arena-item-name", "arenas-menu.arena-item-lore");

            arenasInventory.setItem(slot, arenaItem);

            plugin.getInventories().get(player).getArenaSlot().put(slot, arena);
            slot++;
        }

        arenasInventory.setItem(48, Utils.setItemStack(XMaterial.SLIME_BALL.parseMaterial(), "arenas-menu.quickjoin-item-name", "arenas-menu.quickjoin-item-lore"));
        arenasInventory.setItem(49, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "arenas-menu.close-item-name", "arenas-menu.close-item-lore"));
        arenasInventory.setItem(50, Utils.setItemStack(XMaterial.DIAMOND.parseMaterial(), "arenas-menu.stats-item-name", "arenas-menu.stats-item-lore"));


        plugin.getInventories().get(player).setArenasInventory(arenasInventory);
    }

    public void updateOptionsMenu(String arena, Player player) {
        plugin.putInventories(player, arena);

        Inventory optionsInventory = Bukkit.createInventory(null, 18, plugin.getMessage("arenas-menu.name"));

        optionsInventory.setItem(2, arenaItemStack(new ItemStack(XMaterial.NETHER_STAR.parseMaterial()), arena, "arenas-menu.play-item-name", "arenas-menu.play-item-lore"));
        optionsInventory.setItem(4, Utils.setItemStack(XMaterial.ENDER_PEARL.parseMaterial(), "arenas-menu.staff-item-name", "arenas-menu.staff-item-lore"));
        optionsInventory.setItem(6, Utils.setItemStack(XMaterial.ENDER_EYE.parseMaterial(), "arenas-menu.setup-item-name", "arenas-menu.setup-item-lore"));

        optionsInventory.setItem(13, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "arenas-menu.options-menu-back-item-name", "arenas-menu.options-menu-back-item-lore"));

        plugin.getInventories().get(player).setOptionsInventory(optionsInventory);
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
        if (plugin.getInventories().get(player).getArenasInventory() != null && evt.getClickedInventory().equals(plugin.getInventories().get(player).getArenasInventory())) {
            switch (evt.getSlot()) {
                case 48:
                    new PlayCommands().quickJoin(player);
                    player.getOpenInventory().close();
                    break;
                case 49:
                    player.getOpenInventory().close();
                    break;
                case 50:
                    new StatsMenu().updateStatsMenu(player, player.getName());
                    player.openInventory(plugin.getInventories().get(player).getStatsInventory());
                    break;
                default:
                    if (evt.getCurrentItem().getType() != Material.AIR) {
                        if (evt.getClick().equals(ClickType.LEFT)) {
                            String arena = plugin.getInventories().get(player).getArenaSlot().get(evt.getSlot());
                            new PlayCommands().join(player, new String[]{"join", arena});
                            player.getOpenInventory().close();
                        } else if (evt.getClick().equals(ClickType.RIGHT)) {
                            updateOptionsMenu(plugin.getInventories().get(player).getArenaSlot().get(evt.getSlot()), player);
                            plugin.getInventories().get(player).setArena(plugin.getInventories().get(player).getArenaSlot().get(evt.getSlot()));
                            player.openInventory(plugin.getInventories().get(player).getOptionsInventory());
                        }
                    }
                    break;
            }
            evt.setCancelled(true);
        } else if (evt.getClickedInventory().equals(plugin.getInventories().get(player).getOptionsInventory())) {
            String arena = plugin.getInventories().get(player).getArena();
            switch (evt.getSlot()) {
                case 2:
                    new PlayCommands().join(player, new String[]{"join", arena});
                    player.getOpenInventory().close();
                    break;
                case 4:
                    player.getOpenInventory().close();
                    new StaffMenu().staffMenuCommand(player, new String[]{"staffmenu", arena});
                    break;
                case 6:
                    player.getOpenInventory().close();
                    new SetupMenu().setupMenuCommand(player, new String[]{"setupmenu", arena});
                    break;
                case 13:
                    updateArenasMenu(player);
                    player.openInventory(plugin.getInventories().get(player).getArenasInventory());
                    break;
            }

            evt.setCancelled(true);
        }
    }

    private ItemStack arenaItemStack(ItemStack arenaItem, String arena, String namePath, String lorePath) {
        Arena arenaObject = plugin.getArenas().get(arena);

        ItemMeta arenaItemItemMeta = arenaItem.getItemMeta();
        String vip = plugin.getConfig().getStringList("vip-arenas").contains(arena) ? plugin.getMessage("general.vip-prefix") : "";
        arenaItemItemMeta.setDisplayName(plugin.getMessage(namePath).replace("%arena%", arena).replace("%?vip?%", vip));
        List<String> playerItemLore = new ArrayList<>(plugin.getMessageList(lorePath));
        for (String s : playerItemLore) {
            String replaced = s.replace("%count%", Integer.toString(arenaObject.getPlayers().keySet().size()));
            replaced = replaced.replace("%max%", Integer.toString(plugin.getArenasConfig().getInt("Arenas." + arena + ".max-players")));
            replaced = replaced.replace("%state%", Utils.getStringState(arenaObject));
            Collections.replaceAll(playerItemLore, s, replaced);
        }
        arenaItemItemMeta.setLore(playerItemLore);
        arenaItem.setItemMeta(arenaItemItemMeta);
        return arenaItem;
    }
}
