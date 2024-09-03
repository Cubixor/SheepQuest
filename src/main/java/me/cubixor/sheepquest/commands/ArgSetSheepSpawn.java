package me.cubixor.sheepquest.commands;

import me.cubixor.minigamesapi.spigot.commands.arguments.LocationArgument;
import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.sheepquest.config.SQConfigField;

public class ArgSetSheepSpawn extends LocationArgument {

    public ArgSetSheepSpawn(ArenasManager arenasManager) {
        super(arenasManager, "setsheepspawn", "setup.setsheepspawn", "arena-setup.set-sheep-spawn", SQConfigField.SHEEP_SPAWN);
    }
}
