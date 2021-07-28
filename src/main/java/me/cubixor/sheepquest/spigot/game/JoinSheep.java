package me.cubixor.sheepquest.spigot.game;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.commands.PlayCommands;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;

public class JoinSheep implements Listener {

    private final SheepQuest plugin;

    public JoinSheep() {
        plugin = SheepQuest.getInstance();
    }


    @EventHandler
    public void sheepClickEvent(PlayerInteractEntityEvent evt) {
        if (!plugin.isBefore9()) {
            if (!evt.getHand().equals(EquipmentSlot.HAND)) {
                return;
            }
        }


        if (!evt.getRightClicked().getType().equals(EntityType.SHEEP)) {
            return;
        }

        Sheep sheep = (Sheep) evt.getRightClicked();
        clickSheep(sheep, evt.getPlayer());
    }

    @EventHandler
    public void sheepDamageEvent(EntityDamageByEntityEvent evt) {
        if (!(evt.getDamager().getType().equals(EntityType.PLAYER) && evt.getEntityType().equals(EntityType.SHEEP))) {
            return;
        }

        Sheep sheep = (Sheep) evt.getEntity();
        if (clickSheep(sheep, (Player) evt.getDamager())) {
            evt.setCancelled(true);
        }

    }

    private boolean clickSheep(Sheep sheep, Player player) {
        List<String> configSheep = plugin.getArenasConfig().getStringList("join-sheep");
        List<Block> sheepLocation = new ArrayList<>();
        for (String loc : configSheep) {
            sheepLocation.add(ConfigUtils.stringToLocation(loc).getBlock());
        }

        if (!sheepLocation.contains(sheep.getLocation().getBlock())) {
            return false;
        }

        new PlayCommands().quickJoin(player);
        return true;
    }

    public void spawnSheep(Player player) {
        if (plugin.isBefore9()) {
            player.sendMessage(plugin.getMessage("general.version-not-supported"));
            return;
        }

        Location location = player.getLocation();

        Sheep sheep = (Sheep) location.getWorld().spawnEntity(location, EntityType.SHEEP);
        sheep.setAI(false);
        sheep.setInvulnerable(true);
        sheep.setCollidable(false);
        sheep.setColor(DyeColor.valueOf(plugin.getConfig().getString("join-sheep-color")));
        //sheep.setGravity(false);
        sheep.setSilent(true);
        sheep.setRemoveWhenFarAway(false);
        sheep.setCustomName(plugin.getMessage("other.join-sheep-name"));
        sheep.setCustomNameVisible(true);

        List<String> configSheep = plugin.getArenasConfig().getStringList("join-sheep");
        configSheep.add(ConfigUtils.locationToString(location));
        plugin.getArenasConfig().set("join-sheep", configSheep);
        plugin.saveArenas();

        player.sendMessage(plugin.getMessage("arena-setup.spawn-join-sheep"));
    }
}
