package me.cubixor.sheepquest.common.packets.classes;

import me.cubixor.sheepquest.common.packets.Packet;
import me.cubixor.sheepquest.common.packets.PacketType;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;

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
