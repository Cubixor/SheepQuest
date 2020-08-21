package me.cubixor.sheepquest.commands;

import me.cubixor.sheepquest.SheepQuest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class SetupWand implements Listener {

    private final SheepQuest plugin;

    public SetupWand(SheepQuest s) {
        plugin = s;
    }


    @EventHandler
    public void onClick(PlayerInteractEvent evt) {
        if (!(evt.getPlayer().getInventory().getItemInMainHand().equals(plugin.items.setupWandItem) && evt.getPlayer().hasPermission("sheepquest.setup"))) {
            return;
        }
        evt.setCancelled(true);
        if (evt.getAction().equals(Action.LEFT_CLICK_BLOCK) && evt.getHand().equals(EquipmentSlot.HAND)) {
            plugin.playerInfo.get(evt.getPlayer()).selMin = evt.getClickedBlock();
            evt.getPlayer().sendMessage(plugin.getMessage("arena-setup.wand-select-min"));
        } else if (evt.getAction().equals(Action.RIGHT_CLICK_BLOCK) && evt.getHand().equals(EquipmentSlot.HAND)) {
            plugin.playerInfo.get(evt.getPlayer()).selMax = evt.getClickedBlock();
            evt.getPlayer().sendMessage(plugin.getMessage("arena-setup.wand-select-max"));

        }

    }
}
