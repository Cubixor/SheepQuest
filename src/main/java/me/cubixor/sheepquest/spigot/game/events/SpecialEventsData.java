package me.cubixor.sheepquest.spigot.game.events;

import me.cubixor.sheepquest.spigot.gameInfo.Team;
import org.bukkit.entity.Entity;

import java.util.HashMap;

public class SpecialEventsData {

    private final HashMap<Entity, Team> bonusEntityTeam = new HashMap<>();
    private int specialEventsTimer = -1;

    public int getSpecialEventsTimer() {
        return specialEventsTimer;
    }

    public void setSpecialEventsTimer(int specialEventsTimer) {
        this.specialEventsTimer = specialEventsTimer;
    }

    public HashMap<Entity, Team> getBonusEntityTeam() {
        return bonusEntityTeam;
    }
}
