package me.cubixor.sheepquest.commands;

import com.google.common.collect.ImmutableMap;
import me.cubixor.minigamesapi.spigot.commands.arguments.ArenaCommandArgument;
import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.sheepquest.arena.SQArenasManager;
import me.cubixor.sheepquest.arena.Team;
import org.bukkit.entity.Player;

public class ArgSetSpawn extends ArenaCommandArgument {

    private final SQArenasManager sqArenasManager;

    protected ArgSetSpawn(ArenasManager arenasManager) {
        super(arenasManager, "setspawn", "setup.setspawn", 3, "arena-setup.set-spawn", true, false);
        this.sqArenasManager = (SQArenasManager) arenasManager;
    }

    @Override
    protected void handle(Player player, String[] args) {

        String teamStr = args[2];
        Team team = Team.getByName(teamStr);
        if (team == null) {
            Messages.send(player, "arena-setup.invalid-team");
            return;
        }

        sqArenasManager.setTeamSpawn(args[1], team, player.getLocation());
        Messages.send(player, "arena-setup.set-spawn-success",
                ImmutableMap.of("%arena%", args[1], "%team%", team.getName()));
    }
}
