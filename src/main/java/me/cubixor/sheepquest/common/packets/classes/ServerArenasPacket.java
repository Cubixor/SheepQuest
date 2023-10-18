package me.cubixor.sheepquest.common.packets.classes;

import me.cubixor.sheepquest.common.packets.Packet;
import me.cubixor.sheepquest.common.packets.PacketType;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;

import java.io.Serializable;
import java.util.List;

public class ServerArenasPacket extends Packet implements Serializable {

    private final String server;
    private final List<Arena> arenas;

    public ServerArenasPacket(PacketType packetType, String server, List<Arena> arenas) {
        super(packetType);
        this.server = server;
        this.arenas = arenas;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public String getServer() {
        return server;
    }

}