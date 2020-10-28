package me.cubixor.sheepquest.game;

import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.commands.PlayCommands;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.GameState;
import me.cubixor.sheepquest.gameInfo.PlayerInfo;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

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
        if (Utils.getArena((Player) evt.getEntity()) != null) {
            if (!evt.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                evt.setDamage(0.0F);
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent evt) {
        if (Utils.getArena(evt.getPlayer()) != null) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent evt) {
        if (Utils.getArena(evt.getPlayer()) != null) {
            evt.setCancelled(true);
        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent evt) {
        if (Utils.getArena(evt.getPlayer()) != null) {
            PlayCommands playCommands = new PlayCommands();
            playCommands.sendKickMessage(evt.getPlayer(), Utils.getArena(evt.getPlayer()));
            playCommands.kickPlayer(evt.getPlayer(), Utils.getArenaString(Utils.getArena(evt.getPlayer())));
        }
        plugin.getPlayerInfo().remove(evt.getPlayer());
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent evt) {
        if (!evt.getEntity().getType().equals(EntityType.PLAYER)) {
            return;
        }
        if (Utils.getArena((Player) evt.getEntity()) != null) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent evt) {
        Arena arena = Utils.getArena(evt.getPlayer());
        String arenaString = Utils.getArenaString(arena);
        if (arena != null) {
            if (evt.getTo().getY() < 0) {
                if (arena.getState().equals(GameState.WAITING) || arena.getState().equals(GameState.STARTING)) {
                    evt.getPlayer().teleport((Location) plugin.getArenasConfig().get("Arenas." + arenaString + ".waiting-lobby"));
                } else {
                    evt.getPlayer().teleport((Location) plugin.getArenasConfig().get("Arenas." + arenaString + ".teams." + Utils.getTeamString(arena.getPlayers().get(evt.getPlayer())) + "-spawn"));
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent evt) {
        Arena arena = Utils.getArena((Player) evt.getWhoClicked());
        if (arena != null) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent evt) {
        plugin.getPlayerInfo().put(evt.getPlayer(), new PlayerInfo());
    }
}