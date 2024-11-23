package me.cubixor.sheepquest.game;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.utils.Pathfinding;
import me.cubixor.sheepquest.arena.Region;
import me.cubixor.sheepquest.arena.SQArena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.logging.Level;

public class SheepPathfinder {

    private boolean nmsPathfindingSupported = true;
    private final Pathfinding pathfinding;

    public SheepPathfinder() {
        try {
            this.pathfinding = new Pathfinding();
        } catch (ReflectiveOperationException e) {
            nmsPathfindingSupported = false;
            Bukkit.getLogger().log(Level.WARNING, "NMS pathfinding is not supported on this version, enabling legacy movement instead");
        }
    }

    public void walkToLocation(LivingEntity entity, Region teamRegion, double speed, SQArena arena) {
        if (arena.getSheep().containsKey(entity)) {
            arena.getSheep().get(entity).cancel();
        }

        BukkitTask pathfindingTask = nmsPathfindingSupported ? nmsWalkToLocation(entity, teamRegion, speed) : substituteWalkToLocation(entity, teamRegion, speed);
        arena.getSheep().put(entity, pathfindingTask);
    }


    private BukkitTask nmsWalkToLocation(LivingEntity entity, Region teamRegion, double speed) {
        Object entityInsentient = pathfinding.getEntityInsentient(entity);
        pathfinding.clearGoals(entityInsentient);
        pathfinding.changeFollowRange(entityInsentient, 1000);
        pathfinding.addOtherGoals(entityInsentient);

        return new BukkitRunnable() {
            boolean wasInRegion = true;

            @Override
            public void run() {
                if (!entity.isOnGround()) {
                    return;
                }

                boolean inRegion = teamRegion.isInRegion(entity);

                if (!inRegion && wasInRegion) {
                    pathfinding.clearGoals(entityInsentient);
                    wasInRegion = false;
                } else if (inRegion && !wasInRegion) {
                    pathfinding.clearGoals(entityInsentient);
                    pathfinding.addOtherGoals(entityInsentient);
                    wasInRegion = true;
                }

                if (!inRegion) {
                    pathfinding.addWalkToLocationGoal(entityInsentient, teamRegion.getMiddle(), speed);
                }
            }
        }.runTaskTimer(MinigamesAPI.getPlugin(), 0, 10);
    }

    private BukkitTask substituteWalkToLocation(LivingEntity entity, Region teamRegion, double speed) {
        return new BukkitRunnable() {
            public void run() {
                if (!teamRegion.isInRegion(entity)) {
                    applyMovement(entity, teamRegion.getMiddle(), speed);
                }
            }
        }.runTaskTimer(MinigamesAPI.getPlugin(), 0L, 1);
    }

    private float[] getRotations(Location one, Location two) {
        double diffX = two.getX() - one.getX();
        double diffZ = two.getZ() - one.getZ();
        double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    private void applyMovement(Entity entity, Location location, double speed) {
        float yaw = getRotations(entity.getLocation(), location)[0];

        Vector direction = new Vector(-Math.sin(yaw * 3.1415927F / 180.0F) * 1 * 0.5F, 0, Math.cos(yaw * 3.1415927F / 180.0F) * 1 * 0.5F).multiply(speed);

        if (entity.getLocation().getY() - location.getY() > 0 && entity.isOnGround()) {
            direction.setY(Math.min(0.42, entity.getLocation().getY() - location.getY()));
        }
        if (entity.isOnGround() && (
                entity.getWorld().getBlockAt(entity.getLocation().add(1, 0, 0)).getType().isSolid()) ||
                entity.getWorld().getBlockAt(entity.getLocation().add(-1, 0, 0)).getType().isSolid() ||
                entity.getWorld().getBlockAt(entity.getLocation().add(0, 0, 1)).getType().isSolid() ||
                entity.getWorld().getBlockAt(entity.getLocation().add(0, 0, -1)).getType().isSolid()) {
            direction.setY(0.5);
        }
        if (entity.isOnGround()) {
            entity.setVelocity(direction);
        }
    }
}
