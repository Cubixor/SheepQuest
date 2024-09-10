package me.cubixor.sheepquest.arena;

import me.cubixor.minigamesapi.spigot.config.arenas.ArenaSetupChecker;
import me.cubixor.minigamesapi.spigot.config.arenas.ArenasConfigManager;
import me.cubixor.minigamesapi.spigot.config.arenas.ConfigField;
import me.cubixor.minigamesapi.spigot.game.arena.LocalArena;
import me.cubixor.sheepquest.config.SQConfigField;

import java.util.Map;

public class SQSetupChecker extends ArenaSetupChecker {
    public SQSetupChecker(ArenasConfigManager arenasConfigManager) {
        super(arenasConfigManager);
    }


    @Override
    public Map<ConfigField, Boolean> getReadyMap(LocalArena arena) {
        Map<ConfigField, Boolean> ready = super.getReadyMap(arena);
        SQArena sqArena = (SQArena) arena;

        boolean teamsAdded = !sqArena.getTeams().isEmpty();

        ready.put(SQConfigField.SHEEP_SPAWN, arenasConfigManager.getLocation(arena.getName(), SQConfigField.SHEEP_SPAWN) != null);
        ready.put(SQConfigField.TEAMS, teamsAdded);
        ready.put(SQConfigField.SPAWN, teamsAdded && checkSpawns(sqArena));
        ready.put(SQConfigField.AREA, teamsAdded && checkAreas(sqArena));

        return ready;
    }

    private boolean checkSpawns(SQArena arena) {
        for (Team team : arena.getTeams()) {
            if (arenasConfigManager.getLocation(arena.getName(), SQConfigField.SPAWN, team.toString()) == null) {
                return false;
            }
        }
        return true;
    }

    private boolean checkAreas(SQArena arena) {
        for (Team team : arena.getTeams()) {
            if (arenasConfigManager.getArea(arena.getName(), SQConfigField.AREA, team.toString()) == null) {
                return false;
            }
        }
        return true;
    }
}
