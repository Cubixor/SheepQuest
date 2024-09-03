package me.cubixor.sheepquest.config;

import me.cubixor.minigamesapi.spigot.config.arenas.ConfigField;

public enum SQConfigField implements ConfigField {
    TEAMS, SHEEP_SPAWN, SPAWN, AREA;

    @Override
    public String toString() {
        return super.toString().toLowerCase().replace('_', '-');
    }

}
