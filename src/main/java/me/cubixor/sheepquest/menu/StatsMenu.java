package me.cubixor.sheepquest.menu;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.Utils;
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
        if (!player.hasPermission("sheepquest.play.stats.menu")) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }

        if (args.length != 1 && args.length != 2) {
            player.sendMessage(plugin.getMessage("other.stats-command-usage"));
        }

        if (args.length == 1) {
            updateStatsMenu(player, player.getName());
            player.openInventory(plugin.getInventories().get(player).getStatsInventory());
            return;
        }

        String target = args[1];

        if (!player.hasPermission("sheepquest.play.stats.menu.others") && !target.equalsIgnoreCase(player.getName())) {
            player.sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }

        updateStatsMenu(player, target);
        player.openInventory(plugin.getInventories().get(player).getStatsInventory());

    }

    public void updateStatsMenu(Player player, String target) {
        plugin.putInventories(player, null);

        Inventory statsInventory = Bukkit.createInventory(null, 18, plugin.getMessage("stats-menu.name").replace("%player%", target));

        int wins = plugin.getStats().getInt("Players." + target + ".wins");
        int looses = plugin.getStats().getInt("Players." + target + ".looses");
        int gamesPlayed = plugin.getStats().getInt("Players." + target + ".games-played");
        int kills = plugin.getStats().getInt("Players." + target + ".kills");
        int deaths = plugin.getStats().getInt("Players." + target + ".deaths");
        int sheepTaken = plugin.getStats().getInt("Players." + target + ".sheep-taken");
        int bonusSheepTaken = plugin.getStats().getInt("Players." + target + ".bonus-sheep-taken");


        statsInventory.setItem(0, Utils.setItemStack(XMaterial.EMERALD.parseMaterial(), "stats-menu.wins", "%wins%", Integer.toString(wins)));
        statsInventory.setItem(1, Utils.setItemStack(XMaterial.COAL.parseMaterial(), "stats-menu.looses", "%looses%", Integer.toString(looses)));
        statsInventory.setItem(2, Utils.setItemStack(XMaterial.NOTE_BLOCK.parseMaterial(), "stats-menu.games-played", "%games%", Integer.toString(gamesPlayed)));
        statsInventory.setItem(3, Utils.setItemStack(XMaterial.IRON_SWORD.parseMaterial(), "stats-menu.kills", "%kills%", Integer.toString(kills)));
        statsInventory.setItem(4, Utils.setItemStack(XMaterial.DEAD_BUSH.parseMaterial(), "stats-menu.deaths", "%deaths%", Integer.toString(deaths)));
        statsInventory.setItem(5, Utils.setItemStack(XMaterial.WHITE_WOOL.parseMaterial(), "stats-menu.sheep-taken", "%sheep%", Integer.toString(sheepTaken)));
        statsInventory.setItem(6, Utils.setItemStack(XMaterial.MAGENTA_WOOL.parseMaterial(), "stats-menu.bonus-sheep-taken", "%bonus-sheep%", Integer.toString(bonusSheepTaken)));

        statsInventory.setItem(13, Utils.setItemStack(XMaterial.ARROW.parseMaterial(), "stats-menu.back-item-name", "stats-menu.back-item-lore"));

        plugin.getInventories().get(player).setStatsInventory(statsInventory);
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
        if (plugin.getInventories().get(player).getStatsInventory() != null && evt.getClickedInventory().equals(plugin.getInventories().get(player).getStatsInventory())) {
            if (evt.getSlot() == 13) {
                new ArenasMenu().updateArenasMenu(player);
                player.openInventory(plugin.getInventories().get(player).getArenasInventory());

            }
            evt.setCancelled(true);
        }
    }

}
