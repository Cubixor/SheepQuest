package me.cubixor.sheepquest.commands;

import me.cubixor.minigamesapi.spigot.commands.arguments.ArenaCommandArgument;
import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.sheepquest.arena.SQArenasManager;
import me.cubixor.sheepquest.arena.Team;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class ArenaTeamCommandArgument extends ArenaCommandArgument {
    protected final SQArenasManager sqArenasManager;

    protected ArenaTeamCommandArgument(ArenasManager arenasManager, String name, String permission, int argLength, String messagesPath, boolean requireInServer, Boolean shouldBeActive) {
        super(arenasManager, name, permission, argLength, messagesPath, requireInServer, shouldBeActive);
        sqArenasManager = (SQArenasManager) arenasManager;
    }

    protected Team validateTeam(Player player, String[] args) {
        String teamStr = args[2];
        Team team = Team.getByName(teamStr);
        if (team == null) {
            Messages.send(player, "arena-setup.invalid-team");
            return null;
        }
        return team;
    }

    protected Team validateTeamAdded(Player player, String[] args) {
        Team team = validateTeam(player, args);
        if (team == null) return null;

        List<Team> teams = sqArenasManager.getTeamList(args[1]);
        if (!teams.contains(team)) {
            Messages.send(player, "arena-setup.team-not-added");
            return null;
        }
        return team;
    }
}
