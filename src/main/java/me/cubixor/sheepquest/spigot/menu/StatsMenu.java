package me.cubixor.sheepquest.spigot.menu;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.config.StatsField;
import me.cubixor.sheepquest.spigot.config.StatsUtils;
import me.cubixor.sheepquest.spigot.game.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class StatsMenu implements Listener {

    private final SheepQuest plugin;

    public StatsMenu() {
        plugin = SheepQuest.getInstance();
    }

    public void statsMenuCommand(Player player, String[] args) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!player.hasPermission("sheepquest.play.stats.menu")) {
                player.sendMessage(plugin.getMessage("general.no-permission"));
                return;
            }

            if (args.length != 1 && args.length != 2) {
                player.sendMessage(plugin.getMessage("other.stats-command-usage"));
            }

            if (args.length == 1) {
                updateStatsMenu(player, player.getName());
                return;
            }

            String target = args[1];

            if (!player.hasPermission("sheepquest.play.stats.menu.others") && !target.equalsIgnoreCase(player.getName())) {
                player.sendMessage(plugin.getMessage("general.no-permission"));
                return;
            }

            if (!StatsUtils.getPlayers().contains(target)) {
                player.sendMessage(plugin.getMessage("general.invalid-player"));
                return;
            }


            updateStatsMenu(player, target);
        });
    }

    public void updateStatsMenu(Player player, String target) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int wins = StatsUtils.getSavedStats(target, StatsField.WINS);
            int looses = StatsUtils.getSavedStats(target, StatsField.LOOSES);
            int gamesPlayed = StatsUtils.getSavedStats(target, StatsField.GAMES_PLAYED);
            int kills = StatsUtils.getSavedStats(target, StatsField.KILLS);
            int deaths = StatsUtils.getSavedStats(target, StatsField.DEATHS);
            int sheepTaken = StatsUtils.getSavedStats(target, StatsField.SHEEP_TAKEN);
            int bonusSheepTaken = StatsUtils.getSavedStats(target, StatsField.BONUS_SHEEP_TAKEN);
            int playtime = StatsUtils.getSavedStats(target, StatsField.PLAYTIME);
            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.putInventories(player, null);

                Inventory statsInventory = Bukkit.createInventory(null, 18, plugin.getMessage("stats-menu.name").replace("%player%", target));

                statsInventory.setItem(0, Utils.setItemStack(XMaterial.EMERALD.parseMaterial(), "stats-menu.wins", "%wins%", Integer.toString(wins)));
                statsInventory.setItem(1, Utils.setItemStack(XMaterial.COAL.parseMaterial(), "stats-menu.looses", "%looses%", Integer.toString(looses)));
                statsInventory.setItem(2, Utils.setItemStack(XMaterial.NOTE_BLOCK.parseMaterial(), "stats-menu.games-played", "%games%", Integer.toString(gamesPlayed)));
                statsInventory.setItem(3, Utils.setItemStack(XMaterial.IRON_SWORD.parseMaterial(), "stats-menu.kills", "%kills%", Integer.toString(kills)));
                statsInventory.setItem(4, Utils.setItemStack(XMaterial.DEAD_BUSH.parseMaterial(), "stats-menu.deaths", "%deaths%", Integer.toString(deaths)));
                statsInventory.setItem(5, Utils.setItemStack(XMaterial.WHITE_WOOL.parseMaterial(), "stats-menu.sheep-taken", "%sheep%", Integer.toString(sheepTaken)));
                statsInventory.setItem(6, Utils.setItemStack(XMaterial.MAGENTA_WOOL.parseMaterial(), "stats-menu.bonus-sheep-taken", "%bonus-sheep%", Integer.toString(bonusSheepTaken)));
                statsInventory.setItem(7, Utils.setItemStack(XMaterial.CLOCK.parseMaterial(), "stats-menu.playtime", "%playtime%", new StatsUtils().convertPlaytime(playtime)));

                statsInventory.setItem(13, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "stats-menu.back-item-name", "stats-menu.back-item-lore"));

                plugin.getInventories().get(player).setActiveInventory(statsInventory);
                plugin.getInventories().get(player).setInventoryType(MenuType.STATS);

                player.openInventory(plugin.getInventories().get(player).getActiveInventory());
            });
        });
    }

    @EventHandler
    public void onClick(InventoryClickEvent evt) {
        Player player = (Player) evt.getWhoClicked();
        if (!MenuUtils.isMenuClick(evt)) {
            return;
        }

        if (plugin.getInventories().get(player).getInventoryType().equals(MenuType.STATS)) {
            if (Cooldown.checkCooldown(player)) {
                evt.setCancelled(true);
                return;
            }

            if (evt.getSlot() == 13) {
                new ArenasMenu().updateArenasMenu(player);
            }
            Cooldown.addCooldown(player);
            evt.setCancelled(true);
        }
    }

}