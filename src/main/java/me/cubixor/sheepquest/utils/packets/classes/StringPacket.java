package me.cubixor.sheepquest.utils.packets.classes;

import me.cubixor.sheepquest.utils.packets.Packet;
import me.cubixor.sheepquest.utils.packets.PacketType;

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
