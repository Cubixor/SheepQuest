package me.cubixor.sheepquest.utils.packets.classes;

import me.cubixor.sheepquest.utils.packets.Packet;
import me.cubixor.sheepquest.utils.packets.PacketType;

import java.io.Serializable;

public class BackToServerPacket extends Packet implements Serializable {

    private final String player;
    private final String lobby;
    private final ServerPriority serverPriority;

    public BackToServerPacket(ServerPriority serverPriority, String player, String lobby) {
        super(PacketType.BACK_TO_SERVER);
        this.serverPriority = serverPriority;
        this.player = player;
        this.lobby = lobby;
    }

    public ServerPriority getServerPriority() {
        return serverPriority;
    }

    public String getPlayer() {
        return player;
    }

    public String getLobby() {
        return lobby;
    }

    public enum ServerPriority {LOBBY, PREVIOUS}
}
