package me.cubixor.sheepquest.game.events;

import me.cubixor.sheepquest.gameInfo.Team;
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
