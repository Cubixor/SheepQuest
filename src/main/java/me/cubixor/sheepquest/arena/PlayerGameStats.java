package me.cubixor.sheepquest.arena;

public class PlayerGameStats {

    private int sheepTaken = 0;
    private int bonusSheepTaken = 0;
    private int kills = 0;
    private int deaths = 0;

    public int getSheepTaken() {
        return sheepTaken;
    }

    public void addSheepTaken() {
        sheepTaken++;
    }

    public int getKills() {
        return kills;
    }

    public void addKill() {
        kills++;
    }

    public int getDeaths() {
        return deaths;
    }

    public void addDeath() {
        deaths++;
    }

    public int getBonusSheepTaken() {
        return bonusSheepTaken;
    }

    public void addBonusSheepTaken() {
        bonusSheepTaken++;
    }
}
