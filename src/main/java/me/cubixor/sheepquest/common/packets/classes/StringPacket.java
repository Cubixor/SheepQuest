package me.cubixor.sheepquest.common.packets.classes;

import me.cubixor.sheepquest.common.packets.Packet;
import me.cubixor.sheepquest.common.packets.PacketType;

import java.io.Serializable;

public class StringPacket extends Packet implements Serializable {

    private final String string;

    public StringPacket(PacketType packetType, String string) {
        super(packetType);
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
