package me.cubixor.sheepquest.bungee.socket;

import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.utils.SocketConnection;
import me.cubixor.sheepquest.utils.packets.Packet;
import me.cubixor.sheepquest.utils.packets.PacketType;
import me.cubixor.sheepquest.utils.packets.TargetPacket;
import me.cubixor.sheepquest.utils.packets.classes.ArenasPacket;
import me.cubixor.sheepquest.utils.packets.classes.StringPacket;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

public class SocketServerSender {

    private final SocketServer socketServer;
    private final LinkedBlockingQueue<TargetPacket> sendQueue = new LinkedBlockingQueue<>();

    public SocketServerSender(SocketServer socketServer) {
        this.socketServer = socketServer;
    }

    // Run this asynchronously!
    public void send(ServerSocket socket) {
        while (!socket.isClosed()) {
            try {
                TargetPacket targetPacket = sendQueue.take();
                if (socket.isClosed()) return;
                Packet packet = targetPacket.getPacket();
                Set<String> servers = targetPacket.getServers();

                for (String server : servers) {
                    SocketConnection socketConnection = socketServer.getSpigotSocket(server);
                    if (socketConnection == null) continue;
                    ObjectOutputStream out = socketConnection.getOutputStream();

                    out.writeObject(packet);
                    out.flush();
                    out.reset();

                    if (socketServer.isDebug()) {
                        socketServer.log(Level.INFO, "Sent packet: " + packet.getPacketType().toString() + " to server: " + server);
                    }
                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendPacketToServer(Packet packet, String server) {
        sendQueue.add(new TargetPacket(packet, Collections.singleton(server)));
    }

    public void sendPacketToAllExcept(Packet packet, String exceptServer) {
        Set<String> servers = getServersExcept(exceptServer);
        sendQueue.add(new TargetPacket(packet, servers));
    }

    public void sendAllArenas(String server, List<Arena> arenas) {
        ArenasPacket arenasPacket = new ArenasPacket(PacketType.ARENAS_ADD, arenas);
        sendPacketToServer(arenasPacket, server);
    }

    public void sendRemoveServerArenas(String server) {
        StringPacket stringPacket = new StringPacket(PacketType.SERVER_ARENAS_REMOVE, server);
        sendPacketToAllExcept(stringPacket, server);
    }

    private Set<String> getServersExcept(String server) {
        Set<String> servers = new HashSet<>(socketServer.getSpigotSockets().keySet());
        servers.remove(server);
        return servers;
    }

}