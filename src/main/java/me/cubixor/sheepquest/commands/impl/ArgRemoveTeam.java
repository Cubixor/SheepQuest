package me.cubixor.sheepquest.commands.impl;

import com.google.common.collect.ImmutableMap;
import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.sheepquest.arena.SQArenasManager;
import me.cubixor.sheepquest.arena.Team;
import me.cubixor.sheepquest.commands.ArenaTeamCommandArgument;
import org.bukkit.entity.Player;

import java.util.List;

public class ArgRemoveTeam extends ArenaTeamCommandArgument {

    private final SQArenasManager sqArenasManager;

    public ArgRemoveTeam(ArenasManager arenasManager) {
        super(arenasManager, "removeteam", "setup.changeteams", 3, "arena-setup.remove-team", true, false);
        sqArenasManager = (SQArenasManager) arenasManager;
    }

    @Override
    protected void handle(Player player, String[] args) {
        Team team = validateTeam(player, args);
        if (team == null) return;

        List<Team> teams = sqArenasManager.getTeamList(args[1]);
        if (!teams.contains(team)) {
            Messages.send(player, "arena-setup.remove-team-not-added");
            return;
        }

        teams.remove(team);
        sqArenasManager.setTeamList(args[1], teams);
        sqArenasManager.setTeamSpawn(args[1], team, null);
        //TODO remove area


        //TODO remove in messages.yml
        Messages.send(player, "arena-setup.remove-team-success",
                ImmutableMap.of("%arena%", args[1], "%team%", team.getName()));
    }
}
