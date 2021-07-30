package me.cubixor.sheepquest.spigot.menu;

import me.cubixor.sheepquest.spigot.SheepQuest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MenuUtils implements Listener {

    private final SheepQuest plugin;

    public MenuUtils() {
        plugin = SheepQuest.getInstance();
    }


    public static boolean isMenuClick(InventoryClickEvent evt) {
        SheepQuest plugin = SheepQuest.getInstance();
        Player player = (Player) evt.getWhoClicked();


        return plugin.getInventories().containsKey(player)
                && evt.getCurrentItem() != null
                && evt.getClickedInventory() != null
                && plugin.getInventories().get(player).getActiveInventory() != null
                && evt.getClickedInventory().equals(plugin.getInventories().get(player).getActiveInventory());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent evt) {
        plugin.getInventories().remove(evt.getPlayer());
    }

}
