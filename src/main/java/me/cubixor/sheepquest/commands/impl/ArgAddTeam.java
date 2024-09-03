package me.cubixor.sheepquest.commands.impl;

import com.google.common.collect.ImmutableMap;
import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.sheepquest.arena.Team;
import me.cubixor.sheepquest.commands.ArenaTeamCommandArgument;
import org.bukkit.entity.Player;

import java.util.List;

public class ArgAddTeam extends ArenaTeamCommandArgument {

    public ArgAddTeam(ArenasManager arenasManager) {
        super(arenasManager, "addteam", "setup.changeteams", 3, "arena-setup.add-team", true, false);
    }

    @Override
    protected void handle(Player player, String[] args) {
        Team team = validateTeam(player, args);
        if (team == null) return;

        List<Team> teams = sqArenasManager.getTeamList(args[1]);
        if (teams.contains(team)) {
            Messages.send(player, "arena-setup.add-team-already-added");
            return;
        }

        teams.add(team);
        sqArenasManager.setTeamList(args[1], teams);
        //TODO Add BossBar


        //TODO add in messages.yml
        Messages.send(player, "arena-setup.add-team-success",
                ImmutableMap.of("%arena%", args[1], "%team%", team.getName()));
    }
}
