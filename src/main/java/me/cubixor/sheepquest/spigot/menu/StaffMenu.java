package me.cubixor.sheepquest.spigot.menu;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.commands.StaffCommands;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.game.Cooldown;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.PlayerGameStats;
import org.bukkit.Bukkit;
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

    public StaffMenu() {
        plugin = SheepQuest.getInstance();
    }

    public void staffMenuCommand(Player player, String[] args) {
        if (!Utils.checkIfValid(player, args, "sheepquest.staff.menu", "staff-menu.command", 2, false)) {
            return;
        }
        updateStaffMenu(args[1], player);
    }

    public void playersMenuCommand(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!Utils.checkIfValid(player, args, "sheepquest.staff.menu", "staff-menu.players-command", 2, true)) {
                return;
            }
            updatePlayersMenu(args[1], player);
        });
    }


    public void updateStaffMenu(String arena, Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            boolean active = ConfigUtils.getBoolean(arena, ConfigField.ACTIVE);
            Arena arenaObj = plugin.getArena(arena);
            String gameState = Utils.getStringState(arenaObj);
            Bukkit.getScheduler().runTask(plugin, () -> {

                plugin.putInventories(player, arena);

                int count = arenaObj != null ? arenaObj.getPlayers().size() : 0;

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

                plugin.getInventories().get(player).setActiveInventory(staffInventory);
                plugin.getInventories().get(player).setInventoryType(MenuType.STAFF);

                player.openInventory(plugin.getInventories().get(player).getActiveInventory());
            });
        });
    }

    public void updatePlayersMenu(String arenaString, Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            plugin.putInventories(player, arenaString);
            LocalArena localArena = plugin.getLocalArenas().get(arenaString);

            Inventory playersInventory = Bukkit.createInventory(null, 54, plugin.getMessage("staff-menu.players-menu-name").replace("%arena%", arenaString));
            List<Player> playerList = new ArrayList<>(localArena.getPlayerTeam().keySet());
            int slot = 0;
            plugin.getInventories().get(player).setPlayerSlot(new HashMap<>());
            if (!localArena.getPlayerTeam().keySet().isEmpty()) {
                for (Player p : playerList) {
                    ItemStack playerItem = new ItemStack(XMaterial.PLAYER_HEAD.parseMaterial());
                    SkullMeta skullMeta = (SkullMeta) playerItem.getItemMeta();
                    skullMeta.setOwningPlayer(p);
                    skullMeta.setDisplayName(plugin.getMessage("staff-menu.player-item-name").replace("%nick%", p.getName()));
                    List<String> playerItemLore = new ArrayList<>(plugin.getMessageList("staff-menu.player-item-lore"));
                    for (String s : playerItemLore) {
                        PlayerGameStats playerGameStats = localArena.getPlayerStats().get(p);
                        String replaced = s.replace("%team%", localArena.getPlayerTeam().get(p).getName());
                        replaced = replaced.replace("%sheep%", playerGameStats == null ? "0" : Integer.toString(localArena.getPlayerStats().get(p).getSheepTaken()));
                        replaced = replaced.replace("%kills%", playerGameStats == null ? "0" : Integer.toString(localArena.getPlayerStats().get(p).getKills()));
                        replaced = replaced.replace("%deaths%", playerGameStats == null ? "0" : Integer.toString(localArena.getPlayerStats().get(p).getDeaths()));
                        Collections.replaceAll(playerItemLore, s, replaced);
                    }
                    skullMeta.setLore(playerItemLore);
                    playerItem.setItemMeta(skullMeta);
                    playersInventory.setItem(slot, playerItem);
                    plugin.getInventories().get(player).getPlayerSlot().put(slot, p);
                    slot++;
                    if (slot > 43) {
                        break;
                    }
                }
            }

            playersInventory.setItem(49, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "staff-menu.players-menu-back-item-name", "staff-menu.players-menu-back-item-lore"));

            plugin.getInventories().get(player).setActiveInventory(playersInventory);
            plugin.getInventories().get(player).setInventoryType(MenuType.PLAYERS);

            player.openInventory(plugin.getInventories().get(player).getActiveInventory());
        });
    }

    @EventHandler
    public void onClick(InventoryClickEvent evt) {
        Player player = (Player) evt.getWhoClicked();

        if (!MenuUtils.isMenuClick(evt)) {
            return;
        }

        StaffCommands staffCommands = new StaffCommands();
        String arena = plugin.getInventories().get(player).getArena();
        if (plugin.getInventories().get(player).getInventoryType().equals(MenuType.STAFF)) {
            if (Cooldown.checkCooldown(player)) {
                evt.setCancelled(true);
                return;
            }

            switch (evt.getSlot()) {
                case 0:
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        boolean active = ConfigUtils.getBoolean(arena, ConfigField.ACTIVE);
                        staffCommands.setActive(player, new String[]{"active", arena, active ? "false" : "true"});
                    });
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
                    if (plugin.getLocalArenas().containsKey(arena)) {
                        updatePlayersMenu(arena, player);
                    } else {
                        player.sendMessage(plugin.getMessage("bungee.not-on-server"));
                        player.getOpenInventory().close();
                    }
                    break;
                case 13:
                    new ArenasMenu().updateArenasMenu(player);
                    break;
            }
            Cooldown.addCooldown(player);
            evt.setCancelled(true);
        } else if (plugin.getInventories().get(player).getInventoryType().equals(MenuType.PLAYERS)) {
            if (Cooldown.checkCooldown(player)) {
                evt.setCancelled(true);
                return;
            }

            if (evt.getSlot() == 49) {
                updateStaffMenu(arena, player);
            }
            if (evt.getCurrentItem().getType().equals(XMaterial.PLAYER_HEAD.parseMaterial())) {
                Player skullOwner = plugin.getInventories().get(player).getPlayerSlot().get(evt.getSlot());
                if (evt.getClick().equals(ClickType.RIGHT)) {
                    new StaffCommands().kick(player, new String[]{"kick", skullOwner.getName()});
                    updatePlayersMenu(arena, player);
                } else if (evt.getClick().equals(ClickType.LEFT)) {
                    player.teleport(skullOwner);
                    player.closeInventory();
                }
            }
            Cooldown.addCooldown(player);
            evt.setCancelled(true);
        }
    }
}
