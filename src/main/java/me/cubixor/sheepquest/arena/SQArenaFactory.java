package me.cubixor.sheepquest.arena;

import me.cubixor.minigamesapi.spigot.config.arenas.ArenasConfigManager;
import me.cubixor.minigamesapi.spigot.config.arenas.BasicConfigField;
import me.cubixor.minigamesapi.spigot.config.stats.StatsManager;
import me.cubixor.minigamesapi.spigot.game.ArenaFactory;
import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.game.arena.LocalArena;
import me.cubixor.minigamesapi.spigot.game.arena.StateManager;
import me.cubixor.sheepquest.config.SQConfigField;
import me.cubixor.sheepquest.game.BossBarManager;
import me.cubixor.sheepquest.game.inventories.SQMenuRegistry;
import org.bukkit.Location;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SQArenaFactory implements ArenaFactory {

    private final BossBarManager bossBarManager;

    public SQArenaFactory(BossBarManager bossBarManager) {
        this.bossBarManager = bossBarManager;
    }

    @Override
    public LocalArena createBlankArena(String name, String serverName, ArenasManager arenasManager, StatsManager statsManager) {
        SQArena arena = new SQArena(name, serverName);
        initArena(arena, arenasManager, statsManager);
        return arena;
    }

    @Override
    public LocalArena loadArenaFromConfig(String name, String serverName, ArenasManager arenasManager, StatsManager statsManager) {
        ArenasConfigManager configManager = arenasManager.getConfigManager();

        List<String> teams = configManager.getStringList(name, SQConfigField.TEAMS);
        Map<Team, TeamRegion> teamRegionMap = new EnumMap<>(Team.class);
        for (String teamName : teams) {
            Team team = Team.getByName(teamName);
            Location[] loc = configManager.getArea(name, SQConfigField.AREA, teamName);
            TeamRegion teamRegion = loc == null ? null : new TeamRegion(loc);

            teamRegionMap.put(team, teamRegion);
        }

        SQArena arena = new SQArena(
                name,
                serverName,
                configManager.getBoolean(name, BasicConfigField.ACTIVE),
                configManager.getBoolean(name, BasicConfigField.VIP),
                configManager.getInt(name, BasicConfigField.MIN_PLAYERS),
                configManager.getInt(name, BasicConfigField.MAX_PLAYERS),
                teamRegionMap
        );
        initArena(arena, arenasManager, statsManager);

        return arena;
    }

    @Override
    public void initArena(LocalArena localArena, ArenasManager arenasManager, StatsManager statsManager) {
        localArena.initialize(new StateManager(localArena, arenasManager, statsManager), new SQMenuRegistry(localArena, bossBarManager));
    }
}
