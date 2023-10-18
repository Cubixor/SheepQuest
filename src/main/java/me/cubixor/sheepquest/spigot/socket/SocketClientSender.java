package me.cubixor.sheepquest.spigot.socket;

import me.cubixor.sheepquest.common.packets.Packet;
import me.cubixor.sheepquest.common.packets.PacketType;
import me.cubixor.sheepquest.common.packets.classes.*;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

public class SocketClientSender {

    private final SocketClient socketClient;
    private final LinkedBlockingQueue<Packet> sendQueue = new LinkedBlockingQueue<>();

    public SocketClientSender(SocketClient socketClient) {
        this.socketClient = socketClient;
    }

    // Run this asynchronously!
    public void send(Socket socket, ObjectOutputStream out) {
        while (!socket.isClosed()) {
            try {
                Packet packet = sendQueue.take();
                if (socket.isClosed()) return;

                out.writeObject(packet);
                out.flush();
                out.reset();

                if (socketClient.isDebug()) {
                    socketClient.log(Level.INFO, "Sent packet: " + packet.getPacketType().toString());
                }
            } catch (IOException | InterruptedException e) {
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
