package me.cubixor.sheepquest.bungee.socket;

import me.cubixor.sheepquest.bungee.SheepQuestBungee;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.utils.packets.PacketType;
import me.cubixor.sheepquest.utils.packets.classes.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SocketServerSender {

    private final SheepQuestBungee plugin;

    public SocketServerSender() {
        plugin = SheepQuestBungee.getInstance();
    }

    private ObjectOutputStream getOutputStream(String server) {
        try {
            return plugin.getSpigotSocket().get(server).getOutputStream();
        } catch (Exception e) {
            return null;
        }
    }

    public void joinArena(JoinPacket joinPacket) {
        try {
            ObjectOutputStream out = getOutputStream(joinPacket.getArena().getServer());
            if (out == null) return;

            ProxiedPlayer proxiedPlayer = plugin.getProxy().getPlayer(joinPacket.getPlayer());
            proxiedPlayer.connect(plugin.getProxy().getServerInfo(joinPacket.getArena().getServer()));

            out.writeObject(joinPacket);
            out.flush();
            out.reset();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void leaveArena(ArenaPlayerPacket arenaPlayerPacket) {
        try {
            ObjectOutputStream out = getOutputStream(arenaPlayerPacket.getArena().getServer());
            if (out == null) return;

            out.writeObject(arenaPlayerPacket);
            out.flush();
            out.reset();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendArena(ArenaPacket arenaPacket) {
        try {
            List<String> servers = new ArrayList<>(getServersToSend(arenaPacket.getArena().getServer()));

            for (String server : servers) {
                ObjectOutputStream out = getOutputStream(server);
                if (out == null) return;

                out.writeObject(arenaPacket);
                out.flush();
                out.reset();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeArena(ArenaPacket arenaPacket) {
        try {
            List<String> servers = new ArrayList<>(getServersToSend(arenaPacket.getArena().getServer()));

            for (String server : servers) {
                ObjectOutputStream out = getOutputStream(server);
                if (out == null) return;

                out.writeObject(arenaPacket);
                out.flush();
                out.reset();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendAllArenas(String server, List<Arena> arenas) {
        try {
            ObjectOutputStream out = getOutputStream(server);
            if (out == null) return;

            ArenasPacket arenasPacket = new ArenasPacket(PacketType.ARENAS_ADD, arenas);

            out.writeObject(arenasPacket);
            out.flush();
            out.reset();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendServerArenas(ServerArenasPacket serverArenasPacket) {
        try {
            List<String> servers = new ArrayList<>(getServersToSend(serverArenasPacket.getServer()));
            for (String serverToSend : servers) {
                ObjectOutputStream out = getOutputStream(serverToSend);
                if (out == null) return;

                out.writeObject(serverArenasPacket);
                out.flush();
                out.reset();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeServerArenas(String server) {
        try {
            List<String> arenas = new ArrayList<>(plugin.getArenas().keySet());
            for (String arena : arenas) {
                if (plugin.getArenas().get(arena).getServer().equals(server)) {
                    plugin.getArenas().remove(arena);
                }
            }
            List<String> servers = new ArrayList<>(getServersToSend(server));
            for (String serverToSend : servers) {
                ObjectOutputStream out = getOutputStream(serverToSend);
                if (out == null) return;

                StringPacket stringPacket = new StringPacket(PacketType.SERVER_ARENAS_REMOVE, server);

                out.writeObject(stringPacket);
                out.flush();
                out.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void forceStart(ArenaPlayerPacket arenaPlayerPacket) {
        try {
            ObjectOutputStream out = getOutputStream(arenaPlayerPacket.getArena().getServer());
            if (out == null) return;

            out.writeObject(arenaPlayerPacket);
            out.flush();
            out.reset();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void forceStop(ArenaPlayerPacket arenaPlayerPacket) {
        try {
            ObjectOutputStream out = getOutputStream(arenaPlayerPacket.getArena().getServer());
            if (out == null) return;

            out.writeObject(arenaPlayerPacket);
            out.flush();
            out.reset();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void kick(KickPacket kickPacket) {
        try {
            ObjectOutputStream out = getOutputStream(kickPacket.getArena().getServer());
            if (out == null) return;

            out.writeObject(kickPacket);
            out.flush();
            out.reset();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> getServersToSend(String server) {
        List<String> servers = new ArrayList<>();
        for (String srv : plugin.getSpigotSocket().keySet()) {
            if (!srv.equals(server)) {
                servers.add(srv);
            }
        }
        return servers;
    }
}