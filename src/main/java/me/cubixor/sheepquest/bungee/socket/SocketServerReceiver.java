package me.cubixor.sheepquest.bungee.socket;

import me.cubixor.sheepquest.bungee.SheepQuestBungee;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.utils.packets.Packet;
import me.cubixor.sheepquest.utils.packets.classes.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.io.ObjectInputStream;

public class SocketServerReceiver {

    private final SheepQuestBungee plugin;

    public SocketServerReceiver() {
        plugin = SheepQuestBungee.getInstance();
    }


    public void serverMessageReader(ObjectInputStream in) throws IOException, ClassNotFoundException {
        while (true) {
            Object object = in.readObject();
            Packet packet = (Packet) object;

            switch (packet.getPacketType()) {
                case ARENA_UPDATE: {
                    ArenaPacket arenaPacket = (ArenaPacket) object;
                    plugin.getArenas().put(arenaPacket.getArena().getName(), arenaPacket.getArena());
                    new SocketServerSender().sendArena(arenaPacket);
                    break;
                }
                case ARENA_REMOVE: {
                    ArenaPacket arenaPacket = (ArenaPacket) object;
                    plugin.getArenas().remove(arenaPacket.getArena().getName());
                    new SocketServerSender().removeArena(arenaPacket);
                    break;
                }
                case SERVER_ARENAS_ADD: {
                    ServerArenasPacket serverArenasPacket = (ServerArenasPacket) object;
                    for (Arena arena : serverArenasPacket.getArenas()) {
                        plugin.getArenas().put(arena.getName(), arena);
                    }
                    new SocketServerSender().sendServerArenas(serverArenasPacket);
                    break;
                }
                case ARENA_JOIN: {
                    JoinPacket joinPacket = (JoinPacket) object;
                    ProxiedPlayer proxiedPlayer = plugin.getProxy().getPlayer(joinPacket.getPlayer());
                    if (proxiedPlayer == null) {
                        break;
                    }
                    if (joinPacket.isFirstJoin()) {
                        plugin.getPlayerServers().put(joinPacket.getPlayer(), proxiedPlayer.getServer().getInfo().getName());
                    }
                    if (!joinPacket.isLocalJoin()) {
                        new SocketServerSender().joinArena(joinPacket);
                    }
                    break;
                }
                case ARENA_LEAVE: {
                    ArenaPlayerPacket arenaPlayerPacket = (ArenaPlayerPacket) object;
                    new SocketServerSender().leaveArena(arenaPlayerPacket);
                    break;
                }
                case FORCE_START: {
                    ArenaPlayerPacket arenaPlayerPacket = (ArenaPlayerPacket) object;
                    new SocketServerSender().forceStart(arenaPlayerPacket);
                    break;
                }
                case FORCE_STOP: {
                    ArenaPlayerPacket arenaPlayerPacket = (ArenaPlayerPacket) object;
                    new SocketServerSender().forceStop(arenaPlayerPacket);
                    break;
                }
                case KICK: {
                    KickPacket kickPacket = (KickPacket) object;
                    new SocketServerSender().kick(kickPacket);
                    break;
                }
                case BACK_TO_SERVER: {
                    BackToServerPacket backToServerPacket = (BackToServerPacket) object;
                    ProxiedPlayer proxiedPlayer = plugin.getProxy().getPlayer(backToServerPacket.getPlayer());
                    if (proxiedPlayer == null) {
                        break;
                    }

                    if (backToServerPacket.getServerPriority().equals(BackToServerPacket.ServerPriority.PREVIOUS)) {
                        if (!connectToServer(backToServerPacket.getPlayer(), plugin.getPlayerServers().get(proxiedPlayer.getName()))) {
                            connectToServer(backToServerPacket.getPlayer(), backToServerPacket.getLobby());
                        }
                    } else if (backToServerPacket.getServerPriority().equals(BackToServerPacket.ServerPriority.LOBBY)) {
                        if (!connectToServer(backToServerPacket.getPlayer(), backToServerPacket.getLobby())) {
                            connectToServer(backToServerPacket.getPlayer(), plugin.getPlayerServers().get(proxiedPlayer.getName()));
                        }
                    }
                    plugin.getPlayerServers().remove(backToServerPacket.getPlayer());

                    break;
                }
            }
        }

    }

    private boolean connectToServer(String player, String serverString) {
        ProxiedPlayer proxiedPlayer = plugin.getProxy().getPlayer(player);
        if (serverString != null) {
            ServerInfo targetServer = plugin.getProxy().getServerInfo(serverString);
            if (targetServer != null) {
                if (proxiedPlayer.getServer().getInfo().equals(targetServer)) {
                    return true;
                }
                if (plugin.getSpigotSocket().containsKey(serverString)) {
                    proxiedPlayer.connect(targetServer);
                    return true;
                }
            }
        }
        return false;
    }

}