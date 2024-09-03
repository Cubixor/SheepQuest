package me.cubixor.sheepquest.commands.impl;

import com.google.common.collect.ImmutableMap;
import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.sheepquest.arena.SQArenasManager;
import me.cubixor.sheepquest.arena.Team;
import me.cubixor.sheepquest.commands.ArenaTeamCommandArgument;
import org.bukkit.entity.Player;

public class ArgSetSpawn extends ArenaTeamCommandArgument {

    private final SQArenasManager sqArenasManager;

    protected ArgSetSpawn(ArenasManager arenasManager) {
        super(arenasManager, "setspawn", "setup.setspawn", 3, "arena-setup.set-spawn", true, false);
        this.sqArenasManager = (SQArenasManager) arenasManager;
    }

    @Override
    protected void handle(Player player, String[] args) {
        Team team = validateTeam(player, args);
        if (team == null) return;

        sqArenasManager.setTeamSpawn(args[1], team, player.getLocation());
        Messages.send(player, "arena-setup.set-spawn-success",
                ImmutableMap.of("%arena%", args[1], "%team%", team.getName()));
    }
}
