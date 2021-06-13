package me.cubixor.sheepquest.spigot.menu;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.commands.PlayCommands;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.game.Cooldown;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
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
        if (!Utils.checkIfValid(player, args, "sheepquest.play.menu", "arenas-menu.command", 1, false)) {
            return;
        }
        updateArenasMenu(player);
    }


    public void updateArenasMenu(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            List<String> arenas = new ArrayList<>(ConfigUtils.getArenas());
            HashMap<Integer, ItemStack> inv = new HashMap<>();
            HashMap<Integer, String> arenaSlot = new HashMap<>();

            int slot = 0;
            for (String arena : arenas) {
                Arena arenaObj = plugin.getArena(arena);
                ItemStack material;
                if (plugin.getConfig().getBoolean("color-signs")) {
                    material = Utils.setGlassColor(arenaObj);
                } else {
                    material = new ItemStack(XMaterial.NETHER_STAR.parseMaterial());
                }

                ItemStack arenaItem = arenaItemStack(material, arena, "arenas-menu.arena-item-name", "arenas-menu.arena-item-lore");
                inv.put(slot, arenaItem);
                arenaSlot.put(slot, arena);
                slot++;
            }
            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.putInventories(player, null);
                plugin.getInventories().get(player).setArenaSlot(new HashMap<>());

                Inventory arenasInventory = Bukkit.createInventory(null, 54, plugin.getMessage("arenas-menu.name"));
                for (int s : inv.keySet()) {
                    arenasInventory.setItem(s, inv.get(s));
                    plugin.getInventories().get(player).getArenaSlot().put(s, arenaSlot.get(s));
                }

                arenasInventory.setItem(48, Utils.setItemStack(XMaterial.SLIME_BALL.parseMaterial(), "arenas-menu.quickjoin-item-name", "arenas-menu.quickjoin-item-lore"));
                arenasInventory.setItem(49, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "arenas-menu.close-item-name", "arenas-menu.close-item-lore"));
                arenasInventory.setItem(50, Utils.setItemStack(XMaterial.DIAMOND.parseMaterial(), "arenas-menu.stats-item-name", "arenas-menu.stats-item-lore"));


                plugin.getInventories().get(player).setActiveInventory(arenasInventory);
                plugin.getInventories().get(player).setInventoryType(MenuType.ARENAS);

                player.openInventory(plugin.getInventories().get(player).getActiveInventory());

            });
        });
    }

    public void updateOptionsMenu(String arena, Player player, int slot) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            ItemStack arenaItemStack = arenaItemStack(new ItemStack(XMaterial.NETHER_STAR.parseMaterial()), arena, "arenas-menu.play-item-name", "arenas-menu.play-item-lore");

            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.putInventories(player, arena);

                Inventory optionsInventory = Bukkit.createInventory(null, 18, plugin.getMessage("arenas-menu.name"));

                optionsInventory.setItem(2, arenaItemStack);
                optionsInventory.setItem(4, Utils.setItemStack(XMaterial.ENDER_PEARL.parseMaterial(), "arenas-menu.staff-item-name", "arenas-menu.staff-item-lore"));
                optionsInventory.setItem(6, Utils.setItemStack(XMaterial.ENDER_EYE.parseMaterial(), "arenas-menu.setup-item-name", "arenas-menu.setup-item-lore"));

                optionsInventory.setItem(13, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "arenas-menu.options-menu-back-item-name", "arenas-menu.options-menu-back-item-lore"));

                plugin.getInventories().get(player).setActiveInventory(optionsInventory);
                plugin.getInventories().get(player).setInventoryType(MenuType.OPTIONS);

                player.openInventory(plugin.getInventories().get(player).getActiveInventory());

                plugin.getInventories().get(player).setArena(plugin.getInventories().get(player).getArenaSlot().get(slot));
            });
        });
    }

    @EventHandler
    public void onClick(InventoryClickEvent evt) {
        Player player = (Player) evt.getWhoClicked();

        if (!MenuUtils.isMenuClick(evt)) {
            return;
        }

        if (plugin.getInventories().get(player).getInventoryType().equals(MenuType.ARENAS)) {
            if (Cooldown.checkCooldown(player)) {
                evt.setCancelled(true);
                return;
            }

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
                    break;
                default:
                    if (evt.getCurrentItem().getType() != Material.AIR) {
                        if (evt.getClick().equals(ClickType.LEFT)) {
                            String arena = plugin.getInventories().get(player).getArenaSlot().get(evt.getSlot());
                            new PlayCommands().join(player, new String[]{"join", arena});
                            player.getOpenInventory().close();
                        } else if (evt.getClick().equals(ClickType.RIGHT)) {
                            updateOptionsMenu(plugin.getInventories().get(player).getArenaSlot().get(evt.getSlot()), player, evt.getSlot());
                        }
                    }
                    break;
            }
            Cooldown.addCooldown(player);
            evt.setCancelled(true);
        } else if (plugin.getInventories().get(player).getInventoryType().equals(MenuType.OPTIONS)) {
            if (Cooldown.checkCooldown(player)) {
                evt.setCancelled(true);
                return;
            }

            String arena = plugin.getInventories().get(player).getArena();
            switch (evt.getSlot()) {
                case 2:
                    new PlayCommands().join(player, new String[]{"join", arena});
                    player.getOpenInventory().close();
                    break;
                case 4:
                    new StaffMenu().staffMenuCommand(player, new String[]{"staffmenu", arena});
                    break;
                case 6:
                    new SetupMenu().setupMenuCommand(player, new String[]{"setupmenu", arena});
                    break;
                case 13:
                    updateArenasMenu(player);
                    break;
            }
            Cooldown.addCooldown(player);
            evt.setCancelled(true);
        }
    }

    private ItemStack arenaItemStack(ItemStack arenaItem, String arena, String namePath, String lorePath) {
        Arena arenaObj = plugin.getArena(arena);

        ItemMeta arenaItemItemMeta = arenaItem.getItemMeta();
        String vip = ConfigUtils.getBoolean(arena, ConfigField.VIP) ? plugin.getMessage("general.vip-prefix") : "";
        arenaItemItemMeta.setDisplayName(plugin.getMessage(namePath).replace("%arena%", arena).replace("%?vip?%", vip));
        List<String> playerItemLore = new ArrayList<>(plugin.getMessageList(lorePath));
        for (String s : playerItemLore) {
            String replaced = s.replace("%count%", Integer.toString(arenaObj != null ? arenaObj.getPlayers().size() : 0));
            replaced = replaced.replace("%max%", Integer.toString(ConfigUtils.getInt(arena, ConfigField.MAX_PLAYERS)));
            replaced = replaced.replace("%state%", Utils.getStringState(arenaObj));
            Collections.replaceAll(playerItemLore, s, replaced);
        }
        arenaItemItemMeta.setLore(playerItemLore);
        arenaItem.setItemMeta(arenaItemItemMeta);
        return arenaItem;
    }
}
