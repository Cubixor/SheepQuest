package me.cubixor.sheepquest.arena;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface Region {

    boolean isInRegion(Entity entity);

    Location getMiddle();
}
