package me.cubixor.sheepquest.commands.impl;

import com.google.common.collect.ImmutableMap;
import me.cubixor.minigamesapi.spigot.commands.arguments.ArenaCommandArgument;
import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.sheepquest.arena.SQArenasManager;
import me.cubixor.sheepquest.arena.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class ArgListTeams extends ArenaCommandArgument {

    private final SQArenasManager sqArenasManager;

    public ArgListTeams(ArenasManager arenasManager) {
        super(arenasManager, "listteams", "setup.listteams", 2, "arena-setup.list-teams", true, false);
        this.sqArenasManager = (SQArenasManager) arenasManager;
    }

    @Override
    protected void handle(Player player, String[] args) {
        List<Team> arenaTeams = sqArenasManager.getTeamList(args[1]);

        StringBuilder addedTeams = new StringBuilder();
        StringBuilder otherTeams = new StringBuilder();
        String comma = ChatColor.translateAlternateColorCodes('&', "&f, ");

        for (Team team : Team.getAll()) {
            String toAppend = team.getName() + comma;
            if (arenaTeams.contains(team)) {
                addedTeams.append(toAppend);
            } else {
                otherTeams.append(toAppend);
            }
        }

        //TODO add in messages.yml
        Map<String, String> toReplace = ImmutableMap.of(
                "%arena%", args[1],
                "%teams-added%", addedTeams.toString(),
                "teams-available", otherTeams.toString()
        );

        Messages.send(player, "arena-setup.list-teams-success", toReplace);

    }
}
