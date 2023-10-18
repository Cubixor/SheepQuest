package me.cubixor.sheepquest.common.packets.classes;

import me.cubixor.sheepquest.common.packets.Packet;
import me.cubixor.sheepquest.common.packets.PacketType;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;

import java.io.Serializable;

public class KickPacket extends Packet implements Serializable {

    private final Arena arena;
    private final String player;
    private final String target;

    public KickPacket(String player, String target, Arena arena) {
        super(PacketType.KICK);
        this.arena = arena;
        this.player = player;
        this.target = target;
    }

    public Arena getArena() {
        return arena;
    }

    public String getPlayer() {
        return player;
    }

    public String getTarget() {
        return target;
    }
}
