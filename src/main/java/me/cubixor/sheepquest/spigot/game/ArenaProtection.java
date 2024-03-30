package me.cubixor.sheepquest.spigot.game;

import com.cryptomorin.xseries.messages.Titles;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.commands.PlayCommands;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.PlayerInfo;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ArenaProtection implements Listener {

    private final SheepQuest plugin;

    public ArenaProtection() {
        plugin = SheepQuest.getInstance();
    }

    @EventHandler
    public void onHurt(EntityDamageEvent evt) {
        if (!evt.getEntityType().equals(EntityType.PLAYER)) {
            return;
        }
        if (Utils.getLocalArena((Player) evt.getEntity()) != null) {
            if (!evt.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)
                    && !evt.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)
                    && !evt.getCause().equals(EntityDamageEvent.DamageCause.CUSTOM)) {
                evt.setDamage(0.0F);
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onSheepHurt(EntityDamageEvent evt) {
        if (!evt.getEntityType().equals(EntityType.SHEEP)) {
            return;
        }
        Sheep sheep = (Sheep) evt.getEntity();
        for (LocalArena localArena : plugin.getLocalArenas().values()) {
            if (localArena.getSheep().containsKey(sheep)) {
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent evt) {
        if (Utils.getLocalArena(evt.getPlayer()) != null) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent evt) {
        if (Utils.getLocalArena(evt.getPlayer()) != null) {
            evt.setCancelled(true);
        }
    }


    @EventHandler
    public void onLeave(PlayerQuitEvent evt) {
        LocalArena localArena = Utils.getLocalArena(evt.getPlayer());
        if (localArena != null) {
            PlayCommands playCommands = new PlayCommands();
            Titles.clearTitle(evt.getPlayer());
            playCommands.sendKickMessage(evt.getPlayer(), Utils.getLocalArena(evt.getPlayer()));
            playCommands.kickFromLocalArenaSynchronized(evt.getPlayer(), localArena, false, false);
        }
        plugin.getPlayerInfo().remove(evt.getPlayer());
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent evt) {
        if (!evt.getEntity().getType().equals(EntityType.PLAYER)) {
            return;
        }
        if (Utils.getLocalArena((Player) evt.getEntity()) != null) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent evt) {
        LocalArena localArena = Utils.getLocalArena((Player) evt.getWhoClicked());
        if (localArena != null) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        plugin.getPlayerInfo().put(evt.getPlayer(), new PlayerInfo(evt.getPlayer().getName()));
    }

    @EventHandler
    public void onDye(SheepDyeWoolEvent evt) {
        if (Utils.getLocalArena(evt.getPlayer()) != null) {
            evt.setCancelled(true);
        }
    }
}