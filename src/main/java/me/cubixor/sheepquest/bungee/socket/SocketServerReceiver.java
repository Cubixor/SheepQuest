package me.cubixor.sheepquest.bungee.socket;

import me.cubixor.sheepquest.bungee.SheepQuestBungee;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.utils.packets.Packet;
import me.cubixor.sheepquest.utils.packets.classes.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.*;
import java.util.logging.Level;

public class SocketServerReceiver {

    private final SheepQuestBungee plugin;
    private final SocketServer socketServer;
    private final SocketServerSender sender;

    public SocketServerReceiver(SocketServer socketServer) {
        plugin = SheepQuestBungee.getInstance();
        this.socketServer = socketServer;
        this.sender = socketServer.getSender();
    }

    public void serverMessageReader(String server, ObjectInputStream in) throws IOException {
        while (!socketServer.getServerSocket().isClosed()) {
            try {
                Object object = in.readObject();
                Packet packet = (Packet) object;

                if (socketServer.isDebug()) {
                    socketServer.log(Level.INFO, "Packet received: " + packet.getPacketType().toString() + " from server: " + server);
                }
                handlePacket(packet);

            } catch (ClassNotFoundException |
                     InvalidClassException |
                     StreamCorruptedException |
                     OptionalDataException e) {
                if (socketServer.isDebug()) {
                    e.printStackTrace();
                }

            }
        }

    }

    private void handlePacket(Packet packet) {

        switch (packet.getPacketType()) {
            case ARENA_UPDATE: {
                ArenaPacket arenaPacket = (ArenaPacket) packet;
                plugin.getArenas().put(arenaPacket.getArena().getName(), arenaPacket.getArena());
                sender.sendPacketToAllExcept(arenaPacket, arenaPacket.getArena().getServer());
                break;
            }
            case ARENA_REMOVE: {
                ArenaPacket arenaPacket = (ArenaPacket) packet;
                plugin.getArenas().remove(arenaPacket.getArena().getName());
                sender.sendPacketToAllExcept(arenaPacket, arenaPacket.getArena().getServer());
                break;
            }
            case SERVER_ARENAS_ADD: {
                ServerArenasPacket serverArenasPacket = (ServerArenasPacket) packet;
                for (Arena arena : serverArenasPacket.getArenas()) {
                    plugin.getArenas().put(arena.getName(), arena);
                }
                sender.sendPacketToAllExcept(serverArenasPacket, serverArenasPacket.getServer());
                break;
            }
            case ARENA_JOIN: {
                JoinPacket joinPacket = (JoinPacket) packet;

                if (joinArena(joinPacket)) {
                    sender.sendPacketToServer(joinPacket, joinPacket.getArena().getServer());
                }
                break;
            }
            case ARENA_LEAVE:
            case FORCE_START:
            case FORCE_STOP: {
                ArenaPlayerPacket arenaPlayerPacket = (ArenaPlayerPacket) packet;
                sender.sendPacketToServer(arenaPlayerPacket, arenaPlayerPacket.getArena().getServer());
                break;
            }
            case KICK: {
                KickPacket kickPacket = (KickPacket) packet;
                sender.sendPacketToServer(kickPacket, kickPacket.getArena().getServer());
                break;
            }
            case BACK_TO_SERVER: {
                BackToServerPacket backToServerPacket = (BackToServerPacket) packet;
                sendBackToServer(backToServerPacket);
                break;
            }
        }
    }

    private boolean joinArena(JoinPacket joinPacket) {
        ProxiedPlayer proxiedPlayer = plugin.getProxy().getPlayer(joinPacket.getPlayer());
        if (proxiedPlayer == null) {
            return false;
        }
        if (joinPacket.isFirstJoin()) {
            plugin.getPlayerServers().put(joinPacket.getPlayer(), proxiedPlayer.getServer().getInfo().getName());
        }
        if (!joinPacket.isLocalJoin()) {
            proxiedPlayer.connect(plugin.getProxy().getServerInfo(joinPacket.getArena().getServer()));

            return true;
        }

        return false;
    }

    private void sendBackToServer(BackToServerPacket backToServerPacket) {
        ProxiedPlayer proxiedPlayer = plugin.getProxy().getPlayer(backToServerPacket.getPlayer());
        if (proxiedPlayer == null) {
            return;
        }

        String firstChoiceServer;
        String secondChoiceServer;

        if (backToServerPacket.getServerPriority().equals(BackToServerPacket.ServerPriority.PREVIOUS)) {
            firstChoiceServer = plugin.getPlayerServers().get(proxiedPlayer.getName());
            secondChoiceServer = backToServerPacket.getLobby();
        } else {
            firstChoiceServer = backToServerPacket.getLobby();
            secondChoiceServer = plugin.getPlayerServers().get(proxiedPlayer.getName());
        }

        if (!connectToServer(proxiedPlayer, firstChoiceServer)) {
            connectToServer(proxiedPlayer, secondChoiceServer);
        }


        plugin.getPlayerServers().remove(backToServerPacket.getPlayer());
    }

    private boolean connectToServer(ProxiedPlayer player, String serverString) {
        if (serverString == null) {
            return false;
        }

        ServerInfo targetServer = plugin.getProxy().getServerInfo(serverString);

        if (targetServer == null) {
            return false;
        }

        if (player.getServer().getInfo().equals(targetServer)) {
            return true;
        }

        if (socketServer.getSpigotSockets().containsKey(serverString)) {
            player.connect(targetServer);
            return true;
        }


        return false;
    }

}