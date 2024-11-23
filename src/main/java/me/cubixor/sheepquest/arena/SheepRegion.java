package me.cubixor.sheepquest.arena;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class SheepRegion implements Region {

    private final Location sheepSpawn;
    private final int sheepSpawnSize;

    public SheepRegion(Location sheepSpawn, int sheepSpawnSize) {
        this.sheepSpawn = sheepSpawn;
        this.sheepSpawnSize = sheepSpawnSize;
    }

    @Override
    public boolean isInRegion(Entity entity) {
        return entity.getLocation().distance(sheepSpawn) < sheepSpawnSize;
    }

    @Override
    public Location getMiddle() {
        return sheepSpawn;
    }
}
