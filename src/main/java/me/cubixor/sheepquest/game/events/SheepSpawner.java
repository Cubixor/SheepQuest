package me.cubixor.sheepquest.game.events;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.config.arenas.ArenasConfigManager;
import me.cubixor.minigamesapi.spigot.events.TimerTickEvent;
import me.cubixor.minigamesapi.spigot.game.arena.GameState;
import me.cubixor.minigamesapi.spigot.utils.Particles;
import me.cubixor.minigamesapi.spigot.utils.Sounds;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.config.SQConfigField;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class SheepSpawner implements Listener {

    private final ArenasConfigManager arenasConfigManager;

    public SheepSpawner(ArenasConfigManager arenasConfigManager) {
        this.arenasConfigManager = arenasConfigManager;
    }

    @EventHandler
    public void onTimerTick(TimerTickEvent evt) {
        SQArena arena = (SQArena) evt.getLocalArena();

        if (!evt.getLocalArena().getState().equals(GameState.GAME)) {
            return;
        }

        if (arena.getSheepTimer() == 0) {
            arena.setSheepTimer(MinigamesAPI.getPlugin().getConfig().getInt("sheep-time"));

            spawnSheep(arena);
        }

        arena.setSheepTimer(arena.getSheepTimer() - 1);
    }

    public void spawnSheep(SQArena arena) {
        Location loc = arenasConfigManager.getLocation(arena.getName(), SQConfigField.SHEEP_SPAWN);
        Sheep sheep = loc.getWorld().spawn(loc, Sheep.class);
        sheep.setColor(DyeColor.WHITE);
        //sheep.setInvulnerable(true);

        Sounds.playSound("sheep-spawn", loc, arena.getBukkitPlayers());
        Particles.spawnParticle(loc.add(0, 1, 0), "sheep-spawn");

        arena.getSheep().put(sheep, new BukkitRunnable() {
            @Override
            public void run() {
                //TODO TODO TODO
            }
        }.runTask(MinigamesAPI.getPlugin()));
        //TODO Pathfinding
        //Pathfinding.walkToLocation(sheep, loc, plugin.getConfig().getDouble("sheep-speed"), arena, Team.NONE);
    }
}
