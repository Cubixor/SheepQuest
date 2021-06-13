package me.cubixor.sheepquest.spigot.commands;

import me.cubixor.sheepquest.spigot.SheepQuest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
        if (!(evt.getPlayer().getInventory().getItemInMainHand().equals(plugin.getItems().getSetupWandItem()) && evt.getPlayer().hasPermission("sheepquest.setup"))) {
            return;
        }
        evt.setCancelled(true);
        if (evt.getAction().equals(Action.LEFT_CLICK_BLOCK) && evt.getHand().equals(EquipmentSlot.HAND)) {
            plugin.getPlayerInfo().get(evt.getPlayer()).setSelMin(evt.getClickedBlock());
            evt.getPlayer().sendMessage(plugin.getMessage("arena-setup.wand-select-min"));
        } else if (evt.getAction().equals(Action.RIGHT_CLICK_BLOCK) && evt.getHand().equals(EquipmentSlot.HAND)) {
            plugin.getPlayerInfo().get(evt.getPlayer()).setSelMax(evt.getClickedBlock());
            evt.getPlayer().sendMessage(plugin.getMessage("arena-setup.wand-select-max"));

        }

    }
}
