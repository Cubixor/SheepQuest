package me.cubixor.sheepquest.commands;

import me.cubixor.minigamesapi.spigot.commands.arguments.ArenaCommandArgument;
import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.minigamesapi.spigot.utils.Permissions;
import me.cubixor.sheepquest.arena.SQArenasManager;
import me.cubixor.sheepquest.arena.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ArenaTeamCommandArgument extends ArenaCommandArgument {
    protected final SQArenasManager sqArenasManager;
    private final String allTeams;

    protected ArenaTeamCommandArgument(ArenasManager arenasManager, String name, String permission, String messagesPath) {
        super(arenasManager, name, permission, 3, messagesPath, true, false);
        sqArenasManager = (SQArenasManager) arenasManager;
        allTeams = Arrays.stream(Team.values()).map(Team::getName).collect(Collectors.joining(" "));
    }

    protected Team validateTeam(Player player, String[] args) {
        String teamStr = args[2];
        Team team = Team.getByName(teamStr);
        if (team == null) {
            Messages.send(player, "arena-setup.invalid-team", "%teams%", allTeams);
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

    @Override
    public List<String> handleTabComplete(CommandSender sender, String[] args) {
        List<String> result = super.handleTabComplete(sender, args);
        if (args.length == 3 &&
                args[0].equalsIgnoreCase(getName()) &&
                Permissions.has(sender, getPermission())) {

            for (Team team : Team.values()) {
                if (team.toString().startsWith(args[2].toLowerCase())) {
                    result.add(team.toString());
                }
            }

        }

        return result;
    }
}
