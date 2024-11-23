package me.cubixor.sheepquest.game.events;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.config.arenas.ArenasConfigManager;
import me.cubixor.minigamesapi.spigot.events.TimerTickEvent;
import me.cubixor.minigamesapi.spigot.game.arena.GameState;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.minigamesapi.spigot.utils.Particles;
import me.cubixor.minigamesapi.spigot.utils.Sounds;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.arena.SheepRegion;
import me.cubixor.sheepquest.arena.Team;
import me.cubixor.sheepquest.config.SQConfigField;
import me.cubixor.sheepquest.game.SheepPathfinder;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;


public class SheepSpawner implements Listener {

    private final FileConfiguration config;
    private final ArenasConfigManager arenasConfigManager;
    private final SheepPathfinder sheepPathfinder;

    public SheepSpawner(ArenasConfigManager arenasConfigManager, SheepPathfinder sheepPathfinder) {
        this.arenasConfigManager = arenasConfigManager;
        this.sheepPathfinder = sheepPathfinder;
        this.config = MinigamesAPI.getPlugin().getConfig();
    }

    @EventHandler
    public void onTimerTick(TimerTickEvent evt) {
        SQArena arena = (SQArena) evt.getLocalArena();

        if (!evt.getLocalArena().getState().equals(GameState.GAME)) {
            return;
        }

        arena.setSheepTimer(arena.getSheepTimer() - 1);

        if (arena.getSheepTimer() <= 0) {
            arena.setSheepTimer(config.getInt("sheep-time"));

            spawnSheep(arena);
        }

        if (arena.getTimer() == arena.getNextBonusSheepTime()) {
            spawnBonusSheep(arena);
            arena.setNextBonusSheepTime();
        }
    }

    public void spawnSheep(SQArena arena) {
        Location loc = arenasConfigManager.getLocation(arena.getName(), SQConfigField.SHEEP_SPAWN);
        Sheep sheep = loc.getWorld().spawn(loc, Sheep.class);
        sheep.setColor(DyeColor.WHITE);
        //sheep.setInvulnerable(true);

        Sounds.playSound("sheep-spawn", loc, arena.getBukkitPlayers());
        Particles.spawnParticle(loc.add(0, 1, 0), "sheep-spawn");

        sheepPathfinder.walkToLocation(sheep, new SheepRegion(loc,
                        config.getInt("sheep-spawn-size")),
                config.getDouble("sheep-speed"),
                arena);
    }


    public void spawnBonusSheep(SQArena arena) {
        EntityType entityType = EntityType.valueOf(config.getString("bonus-sheep.entity-type"));
        DyeColor dyeColor = DyeColor.valueOf(config.getString("bonus-sheep.color"));
        int points = config.getInt("bonus-sheep.points");
        String sheepName = Messages.get("game.bonus-sheep-name", "%points%", String.valueOf(points));

        Location loc = arenasConfigManager.getLocation(arena.getName(), SQConfigField.SHEEP_SPAWN);
        LivingEntity entity = (LivingEntity) loc.getWorld().spawn(loc, entityType.getEntityClass());
        if (entity.getType().equals(EntityType.SHEEP)) {
            ((Sheep) entity).setColor(dyeColor);
        }
        entity.setCustomName(sheepName);
        entity.setCustomNameVisible(true);
        entity.setMetadata("SQ-bonus", new FixedMetadataValue(MinigamesAPI.getPlugin(), true));

        Sounds.playSound("bonus-sheep-spawn", loc, arena.getBukkitPlayers());
        Particles.spawnParticle(loc.add(0, 1.5, 0), "bonus-sheep-spawn");

        sheepPathfinder.walkToLocation(entity, new SheepRegion(loc,
                        config.getInt("sheep-spawn-size")),
                config.getDouble("bonus-sheep.points"),
                arena);
        arena.getBonusEntity().put(entity, Team.NONE);

        for (Player p : arena.getPlayerTeam().keySet()) {
            Messages.send(p, "game.bonus-sheep-spawn");
        }
    }
}
