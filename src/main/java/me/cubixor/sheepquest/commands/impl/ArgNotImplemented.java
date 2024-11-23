package me.cubixor.sheepquest.commands.impl;

import me.cubixor.minigamesapi.spigot.commands.arguments.CommandArgument;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ArgNotImplemented extends CommandArgument {

    public ArgNotImplemented(String name, String permission) {
        super(name, permission, 1, null);
    }

    @Override
    protected void handle(Player player, String[] args) {
        player.sendMessage(Messages.get("prefix") + " " + ChatColor.RED + "This feature is not implemented yet in this version. Check plugin resource page on spigotmc for more info.");
    }
}
