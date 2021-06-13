package me.cubixor.sheepquest.spigot.game.events;

import me.cubixor.sheepquest.spigot.gameInfo.Team;
import org.bukkit.entity.Sheep;

import java.util.HashMap;

public class SpecialEventsData {

    private int specialEventsTimer = -1;

    private HashMap<Sheep, Team> bonusSheepTeam = new HashMap<>();

    public int getSpecialEventsTimer() {
        return specialEventsTimer;
    }

    public void setSpecialEventsTimer(int specialEventsTimer) {
        this.specialEventsTimer = specialEventsTimer;
    }

    public HashMap<Sheep, Team> getBonusSheepTeam() {
        return bonusSheepTeam;
    }

    public void setBonusSheepTeam(HashMap<Sheep, Team> bonusSheepTeam) {
        this.bonusSheepTeam = bonusSheepTeam;
    }
}
