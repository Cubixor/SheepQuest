package me.cubixor.sheepquest.common.packets;

import java.io.Serializable;

public enum PacketType implements Serializable {
    ARENA_UPDATE, ARENA_REMOVE, SERVER_ARENAS_ADD, SERVER_ARENAS_REMOVE, ARENA_JOIN, ARENA_LEAVE, ARENAS_ADD, BACK_TO_SERVER, FORCE_START, FORCE_STOP, KICK
}
