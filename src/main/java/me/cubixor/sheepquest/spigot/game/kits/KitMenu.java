package me.cubixor.sheepquest.spigot.game.kits;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class KitMenu implements Listener {

    private final SheepQuest plugin;

    public KitMenu() {
        plugin = SheepQuest.getInstance();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        LocalArena localArena = Utils.getLocalArena((Player) evt.getWhoClicked());
        if (localArena != null && evt.getClickedInventory().equals(plugin.getItems().getKitsInventory()) && evt.getCurrentItem() != null && !evt.getCurrentItem().getType().equals(Material.AIR)) {
            evt.setCancelled(true);

            Player player = (Player) evt.getWhoClicked();

            KitType kitType = Kits.getById(evt.getSlot());
            if (kitType != null) {
                if (localArena.getPlayerKit().get(player).equals(kitType)) {
                    player.sendMessage(plugin.getMessage("kits.already-selected"));
                    player.closeInventory();
                    return;
                }
                if (!player.hasPermission(kitType.getPermission())) {
                    player.sendMessage(plugin.getMessage("general.no-permission"));
                    player.closeInventory();
                    return;
                }

                localArena.getPlayerKit().replace(player, kitType);
                Utils.removeKitBossBars(player, localArena);
                localArena.getKitBossBars().get(kitType).addPlayer(player);
                player.closeInventory();
                player.sendMessage(plugin.getMessage("kits.choose-success").replace("%kit%", kitType.getName()));
            }
        }
    }
}
