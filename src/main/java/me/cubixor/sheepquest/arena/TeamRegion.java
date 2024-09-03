package me.cubixor.sheepquest.arena;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

public class TeamRegion {

    World world;

    double bxMin;
    double byMin;
    double bzMin;

    double bxMax;
    double byMax;
    double bzMax;

    Location loc;

    public TeamRegion(Location[] area) {
        Location min = area[0];
        Location max = area[1];

        world = min.getWorld();

        double b1x = min.getX();
        double b2x = max.getX();
        double b1y = min.getY();
        double b2y = max.getY();
        double b1z = min.getZ();
        double b2z = max.getZ();

        if (b1x < b2x) {
            bxMin = b1x - 1;
            bxMax = b2x + 1;
        } else {
            bxMin = b2x - 1;
            bxMax = b1x + 1;
        }

        if (b1y < b2y) {
            byMin = b1y - 1;
            byMax = b2y + 1;
        } else {
            byMin = b2y - 1;
            byMax = b1y + 1;
        }

        if (b1z < b2z) {
            bzMin = b1z - 1;
            bzMax = b2z + 1;
        } else {
            bzMin = b2z - 1;
            bzMax = b1z + 1;
        }
    }

    public boolean isInRegion(Entity entity) {
        Location entityLoc = entity.getLocation();

        double x = entityLoc.getX();
        double y = entityLoc.getY();
        double z = entityLoc.getZ();

        if (!entity.getWorld().equals(world)) {
            return false;
        }

        if (!(x >= bxMin && x <= bxMax && entity.getWorld().equals(world))) {
            return false;
        }
        if (!(y >= byMin && y <= byMax && entity.getWorld().equals(world))) {
            return false;
        }
        return z >= bzMin && z <= bzMax && entity.getWorld().equals(world);

    }

    public Location getLoc() {
        return loc;
    }
}
