package me.cubixor.sheepquest.common.packets;

import java.util.Set;

public class TargetPacket {

    private final Packet packet;
    private final Set<String> servers;

    public TargetPacket(Packet packet, Set<String> servers) {
        this.packet = packet;
        this.servers = servers;
    }

    public Packet getPacket() {
        return packet;
    }

    public Set<String> getServers() {
        return servers;
    }
}
