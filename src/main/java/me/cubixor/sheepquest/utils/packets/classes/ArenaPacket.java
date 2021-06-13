package me.cubixor.sheepquest.utils.packets.classes;

import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.utils.packets.Packet;
import me.cubixor.sheepquest.utils.packets.PacketType;

import java.io.Serializable;

public class ArenaPacket extends Packet implements Serializable {

    private final Arena arena;

    public ArenaPacket(PacketType packetType, Arena arena) {
        super(packetType);
        this.arena = arena;
    }

    public Arena getArena() {
        return arena;
    }
}
