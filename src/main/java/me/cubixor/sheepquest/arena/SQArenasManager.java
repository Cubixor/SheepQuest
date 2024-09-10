package me.cubixor.sheepquest.arena;

import me.cubixor.minigamesapi.spigot.config.arenas.ArenasConfigManager;
import me.cubixor.minigamesapi.spigot.config.stats.StatsManager;
import me.cubixor.minigamesapi.spigot.game.ArenaFactory;
import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;
import me.cubixor.minigamesapi.spigot.game.SignManager;
import me.cubixor.minigamesapi.spigot.game.items.ItemsRegistry;
import me.cubixor.minigamesapi.spigot.sockets.PacketSenderSpigot;
import me.cubixor.sheepquest.config.SQConfigField;
import org.bukkit.Location;

import java.util.List;
import java.util.stream.Collectors;

public class SQArenasManager extends ArenasManager {

    public SQArenasManager(ArenasRegistry registry, ArenasConfigManager configManager, SignManager signManager, PacketSenderSpigot packetSender, StatsManager statsManager, ItemsRegistry itemsRegistry, ArenaFactory arenaFactory) {
        super(registry, configManager, signManager, packetSender, statsManager, itemsRegistry, arenaFactory);
    }

    public List<Team> getTeamList(String arena) {
        List<String> teamsStr = getConfigManager().getStringList(arena, SQConfigField.TEAMS);
        return teamsStr.stream().map(String::toUpperCase).map(Team::valueOf).collect(Collectors.toList());
    }

    public void addTeam(String arena, Team team) {
        List<Team> teams = getTeamList(arena);
        teams.add(team);
        setTeamList(arena, teams);

        SQArena sqArena = (SQArena) getRegistry().getLocalArenas().get(arena);
        sqArena.getTeamRegions().put(team, null);
    }

    public void removeTeam(String arena, Team team) {
        List<Team> teams = getTeamList(arena);
        teams.remove(team);
        setTeamList(arena, teams);

        SQArena sqArena = (SQArena) getRegistry().getLocalArenas().get(arena);
        sqArena.getTeamRegions().remove(team);
    }

    private void setTeamList(String arena, List<Team> teams) {
        List<String> teamsStr = teams.stream().map(Team::toString).collect(Collectors.toList());
        updateArenaField(arena, SQConfigField.TEAMS, teamsStr);
    }

    public void setTeamSpawn(String arena, Team team, Location loc) {
        getConfigManager().updateField(arena, SQConfigField.SPAWN, team.toString(), loc);
    }

    public void setTeamArea(String arena, Team team, Location[] locs) {
        getConfigManager().updateField(arena, SQConfigField.AREA, team.toString(), locs);

        SQArena sqArena = (SQArena) getRegistry().getLocalArenas().get(arena);
        if (locs == null) {
            sqArena.getTeamRegions().remove(team);
        } else {
            sqArena.getTeamRegions().replace(team, new TeamRegion(locs));
        }
    }
}
