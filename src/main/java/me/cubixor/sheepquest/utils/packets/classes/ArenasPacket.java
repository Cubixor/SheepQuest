package me.cubixor.sheepquest.utils.packets.classes;

import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.utils.packets.Packet;
import me.cubixor.sheepquest.utils.packets.PacketType;

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