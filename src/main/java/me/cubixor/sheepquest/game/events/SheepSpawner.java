package me.cubixor.sheepquest.game.events;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.config.arenas.ArenasConfigManager;
import me.cubixor.minigamesapi.spigot.events.TimerTickEvent;
import me.cubixor.minigamesapi.spigot.game.arena.GameState;
import me.cubixor.minigamesapi.spigot.utils.Particles;
import me.cubixor.minigamesapi.spigot.utils.Sounds;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.arena.SheepRegion;
import me.cubixor.sheepquest.config.SQConfigField;
import me.cubixor.sheepquest.game.SheepPathfinder;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SheepSpawner implements Listener {

    private final ArenasConfigManager arenasConfigManager;
    private final SheepPathfinder sheepPathfinder;

    public SheepSpawner(ArenasConfigManager arenasConfigManager, SheepPathfinder sheepPathfinder) {
        this.arenasConfigManager = arenasConfigManager;
        this.sheepPathfinder = sheepPathfinder;
    }

    @EventHandler
    public void onTimerTick(TimerTickEvent evt) {
        SQArena arena = (SQArena) evt.getLocalArena();

        if (!evt.getLocalArena().getState().equals(GameState.GAME)) {
            return;
        }

        arena.setSheepTimer(arena.getSheepTimer() - 1);

        if (arena.getSheepTimer() <= 0) {
            arena.setSheepTimer(MinigamesAPI.getPlugin().getConfig().getInt("sheep-time"));

            spawnSheep(arena);
        }
    }

    public void spawnSheep(SQArena arena) {
        Location loc = arenasConfigManager.getLocation(arena.getName(), SQConfigField.SHEEP_SPAWN);
        Sheep sheep = loc.getWorld().spawn(loc, Sheep.class);
        sheep.setColor(DyeColor.WHITE);
        //sheep.setInvulnerable(true);

        Sounds.playSound("sheep-spawn", loc, arena.getBukkitPlayers());
        Particles.spawnParticle(loc.add(0, 1, 0), "sheep-spawn");

        sheepPathfinder.walkToLocation(sheep, new SheepRegion(loc, MinigamesAPI.getPlugin().getConfig().getInt("sheep-spawn-size")), arena);
    }
}
