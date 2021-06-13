package me.cubixor.sheepquest.utils.packets.classes;

import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.utils.packets.Packet;
import me.cubixor.sheepquest.utils.packets.PacketType;

import java.io.Serializable;

public class ArenaPlayerPacket extends Packet implements Serializable {

    private final Arena arena;
    private final String player;

    public ArenaPlayerPacket(PacketType packetType, Arena arena, String player) {
        super(packetType);
        this.arena = arena;
        this.player = player;
    }

    public Arena getArena() {
        return arena;
    }

    public String getPlayer() {
        return player;
    }
}
