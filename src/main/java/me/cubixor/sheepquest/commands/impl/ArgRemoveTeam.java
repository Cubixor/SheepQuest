package me.cubixor.sheepquest.commands.impl;

import com.google.common.collect.ImmutableMap;
import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.arena.Team;
import me.cubixor.sheepquest.commands.ArenaTeamCommandArgument;
import org.bukkit.entity.Player;

public class ArgRemoveTeam extends ArenaTeamCommandArgument {

    public ArgRemoveTeam(ArenasManager arenasManager) {
        super(arenasManager, "removeteam", "setup.changeteams", "arena-setup.remove-team");
    }

    @Override
    protected void handle(Player player, String[] args) {
        Team team = validateTeamAdded(player, args);
        if (team == null) return;

        sqArenasManager.removeTeam(args[1], team);
        sqArenasManager.setTeamSpawn(args[1], team, null);
        sqArenasManager.setTeamArea(args[1], team, null);

        SQArena sqArena = (SQArena) arenasRegistry.getLocalArenas().get(args[1]);
        sqArena.getTeamRegions().remove(team);

        Messages.send(player, "arena-setup.remove-team-success",
                ImmutableMap.of("%arena%", args[1], "%team%", team.getName()));
    }
}
