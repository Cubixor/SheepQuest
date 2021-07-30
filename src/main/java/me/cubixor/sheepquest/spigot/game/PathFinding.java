package me.cubixor.sheepquest.spigot.game;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


public class PathFinding {

    public static float[] getRotations(Location one, Location two) {
        double diffX = two.getX() - one.getX();
        double diffZ = two.getZ() - one.getZ();
        double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    public static Location getMiddleArea(String arena, Team team) {
        Location[] loc = ConfigUtils.getArea(arena, team);
        double x = (loc[0].getX() + loc[1].getX()) / 2;
        double y = (loc[0].getY() + loc[1].getY()) / 2;
        double z = (loc[0].getZ() + loc[1].getZ()) / 2;

        return new Location(loc[0].getWorld(), x, y, z);
    }

    public static void walkToLocation(LivingEntity entity, Location location, double speed, LocalArena localArena, Team team) {
        if (localArena.getSheep().containsKey(entity)) {
            localArena.getSheep().get(entity).cancel();
        }
        localArena.getSheep().put((Sheep) entity, new BukkitRunnable() {
            public void run() {
                if (!Utils.isInRegion(entity, localArena.getName(), team)) {
                    applyMovement(entity, location, speed);
                }
            }
        }.runTaskTimer(SheepQuest.getInstance(), 0L, 1L));
    }

    private static void applyMovement(Entity entity, Location location, double speed) {
        float yaw = getRotations(entity.getLocation(), location)[0];

        Vector direction = new Vector(-Math.sin(yaw * 3.1415927F / 180.0F) * (float) 1 * 0.5F, 0, Math.cos(yaw * 3.1415927F / 180.0F) * (float) 1 * 0.5F).multiply(speed);

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
