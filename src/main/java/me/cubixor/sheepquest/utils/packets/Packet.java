package me.cubixor.sheepquest.utils.packets;

import java.io.Serializable;

public class Packet implements Serializable {

    private PacketType packetType;

    public Packet(PacketType packetType) {
        this.packetType = packetType;
    }

    public PacketType getPacketType() {
        return packetType;
    }

    public void setPacketType(PacketType packetType) {
        this.packetType = packetType;
    }
}
