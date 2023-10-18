package me.cubixor.sheepquest.common.packets.classes;

import me.cubixor.sheepquest.common.packets.Packet;
import me.cubixor.sheepquest.common.packets.PacketType;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;

import java.io.Serializable;
import java.util.List;

public class ArenasPacket extends Packet implements Serializable {

    private final List<Arena> arenas;

    public ArenasPacket(PacketType packetType, List<Arena> arenas) {
        super(packetType);
        this.arenas = arenas;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

}