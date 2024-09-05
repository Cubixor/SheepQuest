package me.cubixor.sheepquest.game;

import me.cubixor.minigamesapi.spigot.game.ArenaProtection;
import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.game.arena.LocalArena;
import me.cubixor.sheepquest.arena.SQArena;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class SQArenaProtection extends ArenaProtection {

    public SQArenaProtection(ArenasManager arenasManager) {
        super(arenasManager);
    }

    @EventHandler
    public void onSheepHurt(EntityDamageEvent evt) {
        if (!evt.getEntityType().equals(EntityType.SHEEP)) {
            return;
        }
        cancelForEntity((Sheep) evt.getEntity(), evt);
    }

    @Override
    @EventHandler
    public void onDrop(PlayerDropItemEvent evt) {
        cancelInArena(evt.getPlayer(), evt);
    }

    @Override
    @EventHandler
    public void onPickup(PlayerPickupItemEvent evt) {
        cancelInArena(evt.getPlayer(), evt);
    }

    @Override
    @EventHandler
    public void onFood(FoodLevelChangeEvent evt) {
        cancelInArena((Player) evt.getEntity(), evt);
    }

    @Override
    @EventHandler
    public void onClick(InventoryClickEvent evt) {
        cancelInArena((Player) evt.getWhoClicked(), evt);
    }

    @EventHandler
    public void onDye(SheepDyeWoolEvent evt) {
        cancelForEntity(evt.getEntity(), evt);
    }

    private void cancelForEntity(Sheep sheep, Cancellable evt) {
        for (LocalArena localArena : arenasManager.getRegistry().getLocalArenas().values()) {
            SQArena arena = (SQArena) localArena;
            if (arena.getSheep().containsKey(sheep)) {
                evt.setCancelled(true);
                return;
            }
        }
    }
}
