package me.cubixor.sheepquest.commands.impl;

import com.google.common.collect.ImmutableMap;
import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.sheepquest.arena.Team;
import me.cubixor.sheepquest.commands.ArenaTeamCommandArgument;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class ArgSetArea extends ArenaTeamCommandArgument {
    public ArgSetArea(ArenasManager arenasManager) {
        super(arenasManager, "setteamarea", "setup.setteamarea", "arena-setup.set-teams-area");
    }

    @Override
    protected void handle(Player player, String[] args) {
        Team team = validateTeamAdded(player, args);
        if (team == null) return;

        if (!player.hasMetadata("MGAPI-selMin") || !player.hasMetadata("MGAPI-selMax")) {
            Messages.send(player, "arena-setup.selection-empty");
            return;
        }

        MetadataValue selMinData = player.getMetadata("MGAPI-selMin").get(0);
        MetadataValue selMaxData = player.getMetadata("MGAPI-selMax").get(0);

        Location selMin = (Location) selMinData.value();
        Location selMax = (Location) selMaxData.value();

        sqArenasManager.setTeamArea(args[1], team, new Location[]{selMin, selMax});

        Messages.send(player, "arena-setup.set-teams-area-success",
                ImmutableMap.of("%arena%", args[1], "%team%", team.getName()));
    }
}
