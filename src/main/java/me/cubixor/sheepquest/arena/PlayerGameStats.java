package me.cubixor.sheepquest.arena;

import org.bukkit.scheduler.BukkitTask;

public class PlayerGameStats {

    private int sheepTaken = 0;
    private int bonusSheepTaken = 0;
    private int kills = 0;
    private int deaths = 0;

    private BukkitTask sheepCooldown;

    public int getSheepTaken() {
        return sheepTaken;
    }

    public void setSheepTaken(int sheepTaken) {
        this.sheepTaken = sheepTaken;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public BukkitTask getSheepCooldown() {
        return sheepCooldown;
    }

    public void setSheepCooldown(BukkitTask sheepCooldown) {
        this.sheepCooldown = sheepCooldown;
    }

    public int getBonusSheepTaken() {
        return bonusSheepTaken;
    }

    public void setBonusSheepTaken(int bonusSheepTaken) {
        this.bonusSheepTaken = bonusSheepTaken;
    }
}
