package me.cubixor.sheepquest.common.packets.classes;

import me.cubixor.sheepquest.common.packets.Packet;
import me.cubixor.sheepquest.common.packets.PacketType;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;

import java.io.Serializable;

public class JoinPacket extends Packet implements Serializable {

    private final Arena arena;
    private final String player;
    private final boolean firstJoin;
    private final boolean localJoin;

    public JoinPacket(Arena arena, String player, boolean firstJoin, boolean localJoin) {
        super(PacketType.ARENA_JOIN);
        this.arena = arena;
        this.player = player;
        this.firstJoin = firstJoin;
        this.localJoin = localJoin;
    }

    public Arena getArena() {
        return arena;
    }

    public String getPlayer() {
        return player;
    }

    public boolean isFirstJoin() {
        return firstJoin;
    }

    public boolean isLocalJoin() {
        return localJoin;
    }
}

