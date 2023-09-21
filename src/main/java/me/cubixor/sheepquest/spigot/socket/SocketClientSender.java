package me.cubixor.sheepquest.spigot.socket;

import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.utils.packets.Packet;
import me.cubixor.sheepquest.utils.packets.PacketType;
import me.cubixor.sheepquest.utils.packets.classes.*;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;

public class SocketClientSender {

    private final SocketClient socketClient;
    private final LinkedBlockingDeque<Packet> sendQueue = new LinkedBlockingDeque<>();

    public SocketClientSender(SocketClient socketClient) {
        this.socketClient = socketClient;
    }

    // Run this asynchronously!
    public void send(ObjectOutputStream out) {
        while (!socketClient.getSocket().isClosed()) {
            if (sendQueue.isEmpty()) continue;
            Packet packet = sendQueue.pop();

            try {
                out.writeObject(packet);
                out.flush();
                out.reset();

                if (socketClient.isDebug()) {
                    socketClient.log(Level.INFO, "Sent packet: " + packet.getPacketType().toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPacket(Packet packet) {
        sendQueue.add(packet);
    }

    public void sendJoinPacket(Arena arena, String player, boolean joinServer, boolean localJoin) {
        JoinPacket joinPacket = new JoinPacket(arena, player, joinServer, localJoin);
        sendPacket(joinPacket);
    }

    public void sendArenasPacket(String server, List<Arena> arenas) {
        ServerArenasPacket serverArenasPacket = new ServerArenasPacket(PacketType.SERVER_ARENAS_ADD, server, arenas);
        sendPacket(serverArenasPacket);
    }

    public void sendUpdateArenaPacket(Arena arena) {
        ArenaPacket arenaPacket = new ArenaPacket(PacketType.ARENA_UPDATE, arena);
        sendPacket(arenaPacket);
    }

    public void sendRemoveArenaPacket(Arena arena) {
        ArenaPacket arenaPacket = new ArenaPacket(PacketType.ARENA_REMOVE, arena);
        sendPacket(arenaPacket);
    }

    public void sendBackToServerPacket(BackToServerPacket.ServerPriority serverPriority, String player, String lobby) {
        BackToServerPacket backToServerPacket = new BackToServerPacket(serverPriority, player, lobby);
        sendPacket(backToServerPacket);
    }

    public void sendForceStartPacket(String player, Arena arena) {
        ArenaPlayerPacket arenaPlayerPacket = new ArenaPlayerPacket(PacketType.FORCE_START, arena, player);
        sendPacket(arenaPlayerPacket);
    }

    public void sendForceStopPacket(String player, Arena arena) {
        ArenaPlayerPacket arenaPlayerPacket = new ArenaPlayerPacket(PacketType.FORCE_STOP, arena, player);
        sendPacket(arenaPlayerPacket);
    }

    public void sendKickPacket(String player, String target, Arena arena) {
        KickPacket kickPacket = new KickPacket(player, target, arena);
        sendPacket(kickPacket);
    }
}
