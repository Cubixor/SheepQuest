package me.cubixor.sheepquest.game;

import me.cubixor.sheepquest.Arena;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.Team;
import me.cubixor.sheepquest.Utils;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


public class PathFinding {

    private final SheepQuest plugin;

    public PathFinding(SheepQuest s) {
        plugin = s;
    }

    public static float[] getRotations(Location one, Location two) {
        double diffX = two.getX() - one.getX();
        double diffZ = two.getZ() - one.getZ();
        double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    public void walkToLocation(LivingEntity entity, Location location, double speed, Arena arena, Team team) {
        if (arena.sheep.containsKey(entity)) {
            arena.sheep.get(entity).cancel();
        }

        arena.sheep.put((Sheep) entity, new BukkitRunnable() {
            public void run() {
                Utils utils = new Utils(plugin);
                if (!utils.isInRegion(entity, utils.getArenaString(arena), team)) {
                    float yaw = getRotations(entity.getLocation(), location)[0];

                    Vector direction = new Vector(-Math.sin(yaw * 3.1415927F / 180.0F) * (float) 1 * 0.5F, 0, Math.cos(yaw * 3.1415927F / 180.0F) * (float) 1 * 0.5F).multiply(speed);

                    if (entity.getLocation().getY() - location.getY() > 0 && entity.isOnGround()) {
                        direction.setY(Math.min(0.42, entity.getLocation().getY() - location.getY()));
                    }
                    if (entity.isOnGround()) {
                        entity.setVelocity(direction);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L));
    }
}
