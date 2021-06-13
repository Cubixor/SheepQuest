package me.cubixor.sheepquest.utils.packets.classes;

import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.utils.packets.Packet;
import me.cubixor.sheepquest.utils.packets.PacketType;

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