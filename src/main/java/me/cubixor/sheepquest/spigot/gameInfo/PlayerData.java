package me.cubixor.sheepquest.spigot.gameInfo;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.Collection;

public class PlayerData {

    private final ItemStack[] inventory;
    private final Location location;
    private final Collection<PotionEffect> potionEffects;
    private final GameMode gameMode;
    private final double health;
    private final int food;
    private final float exp;
    private final int level;

    public PlayerData(ItemStack[] inventory, Location location, Collection<PotionEffect> potionEffects, GameMode gameMode, double health, int food, float exp, int level) {
        this.inventory = inventory;
        this.location = location;
        this.potionEffects = potionEffects;
        this.gameMode = gameMode;
        this.health = health;
        this.food = food;
        this.exp = exp;
        this.level = level;
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "inventory=" + Arrays.toString(inventory) +
                ", location=" + location +
                ", potionEffects=" + potionEffects +
                ", gameMode=" + gameMode +
                ", health=" + health +
                ", food=" + food +
                ", exp=" + exp +
                ", level=" + level +
                '}';
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public Location getLocation() {
        return location;
    }

    public Collection<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public double getHealth() {
        return health;
    }

    public int getFood() {
        return food;
    }

    public float getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }
}
