package me.cubixor.sheepquest.spigot.commands;

import me.cubixor.sheepquest.spigot.SheepQuest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class SetupWand implements Listener {

    private final SheepQuest plugin;

    public SetupWand() {
        plugin = SheepQuest.getInstance();
    }


    @EventHandler
    public void onClick(PlayerInteractEvent evt) {
        if (plugin.isDisabled()) {
            return;
        }
        if (!(evt.getPlayer().getInventory().getItemInHand().equals(plugin.getItems().getSetupWandItem()) && evt.getPlayer().hasPermission("sheepquest.setup"))) {
            return;
        }
        if (!plugin.isBefore9()) {
            if (!evt.getHand().equals(EquipmentSlot.HAND)) {
                return;
            }
        }

        evt.setCancelled(true);

        switch (evt.getAction()) {
            case LEFT_CLICK_BLOCK: {
                plugin.getPlayerInfo().get(evt.getPlayer()).setSelMin(evt.getClickedBlock());
                evt.getPlayer().sendMessage(plugin.getMessage("arena-setup.wand-select-min"));
                break;
            }
            case RIGHT_CLICK_BLOCK: {
                plugin.getPlayerInfo().get(evt.getPlayer()).setSelMax(evt.getClickedBlock());
                evt.getPlayer().sendMessage(plugin.getMessage("arena-setup.wand-select-max"));
                break;
            }
        }
    }
}
