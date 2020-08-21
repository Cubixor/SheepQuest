package me.cubixor.sheepquest.game;

import me.cubixor.sheepquest.Arena;
import me.cubixor.sheepquest.GameState;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.Utils;
import me.cubixor.sheepquest.commands.PlayCommands;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ArenaProtection implements Listener {

    private final SheepQuest plugin;

    public ArenaProtection(SheepQuest s) {
        plugin = s;
    }

    @EventHandler
    public void onHurt(EntityDamageEvent evt) {
        if (!evt.getEntityType().equals(EntityType.SHEEP) && !evt.getEntityType().equals(EntityType.PLAYER)) {
            return;
        }
        if (evt.getEntityType().equals(EntityType.SHEEP)) {
            for (String arena : plugin.arenas.keySet()) {
                if (plugin.arenas.get(arena).sheep.containsKey((Sheep) evt.getEntity())) {
                    evt.setDamage(0.0F);
                    evt.setCancelled(true);
                    return;
                }
            }
        }
        if (evt.getEntityType().equals(EntityType.PLAYER)) {
            if (new Utils(plugin).getArena((Player) evt.getEntity()) != null) {
                if (!evt.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK) && !evt.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK)) {
                    evt.setDamage(0.0F);
                    evt.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent evt) {
        Utils utils = new Utils(plugin);
        if (utils.getArena(evt.getPlayer()) != null) {
            evt.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPickup(PlayerPickupItemEvent evt) {
        Utils utils = new Utils(plugin);
        if (utils.getArena(evt.getPlayer()) != null) {
            evt.setCancelled(true);
        }

    }

    @EventHandler
    public void onLeave(PlayerQuitEvent evt) {
        Utils utils = new Utils(plugin);
        if (utils.getArena(evt.getPlayer()) != null) {
            PlayCommands playCommands = new PlayCommands(plugin);
            playCommands.sendKickMessage(evt.getPlayer(), utils.getArena(evt.getPlayer()));
            playCommands.kickPlayer(evt.getPlayer(), utils.getArenaString(utils.getArena(evt.getPlayer())));
        }
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent evt) {
        Utils utils = new Utils(plugin);
        if (!evt.getEntity().getType().equals(EntityType.PLAYER)) {
            return;
        }
        if (utils.getArena((Player) evt.getEntity()) != null) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent evt) {
        Utils utils = new Utils(plugin);
        Arena arena = utils.getArena(evt.getPlayer());
        String arenaString = utils.getArenaString(arena);
        if (arena != null) {
            if (evt.getTo().getY() < 0) {
                if (arena.state.equals(GameState.WAITING) || arena.state.equals(GameState.STARTING)) {
                    evt.getPlayer().teleport((Location) plugin.getArenasConfig().get("Arenas." + arenaString + ".waiting-lobby"));
                } else {
                    evt.getPlayer().teleport((Location) plugin.getArenasConfig().get("Arenas." + arenaString + ".teams." + utils.getTeamString(arena.playerTeam.get(evt.getPlayer())) + "-spawn"));
                }
            }
        }
    }

}
