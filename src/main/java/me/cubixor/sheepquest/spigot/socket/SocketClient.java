package me.cubixor.sheepquest.spigot.socket;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.utils.SocketConnection;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class SocketClient {

    private final SheepQuest plugin;

    public SocketClient() {
        plugin = SheepQuest.getInstance();
    }

    public void clientSetup(String host, int port, String server) {
        final boolean msgSent = plugin.getBungeeSocket() != null;
        plugin.setBungeeSocket(null);

        new BukkitRunnable() {
            @Override
            public void run() {
                boolean done = false;
                boolean msg = msgSent;
                while (!done && plugin.isEnabled()) {
                    done = clientConnect(host, port, server);
                    if (!msg && !done) {
                        msg = true;
                        plugin.getLogger().warning(ChatColor.YELLOW + "Couldn't connect to the bungeecord server. Plugin will try to connect until it succeeds.");

                    }
                }
            }

        }.runTaskAsynchronously(plugin);
    }

    private boolean clientConnect(String host, int port, String server) {
        try {
            Socket sock = new Socket(host, port);

            ObjectInputStream objectInputStream = new ObjectInputStream(sock.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(sock.getOutputStream());
            objectOutputStream.writeUTF(server);
            objectOutputStream.flush();

            SocketConnection socketConnectionSpigot = new SocketConnection(sock, objectInputStream, objectOutputStream);
            plugin.setBungeeSocket(socketConnectionSpigot);

            List<Arena> arenas = new ArrayList<>();
            for (String arenaStr : plugin.getLocalArenas().keySet()) {
                LocalArena localArena = plugin.getLocalArenas().get(arenaStr);
                Arena arena = new Arena(localArena.getName(), localArena.getServer(), localArena.getState(), localArena.getPlayers());
                arenas.add(arena);
            }

            new SocketClientSender().sendArenasPacket(plugin.getServerName(), arenas);

            clientReceive();

            plugin.getLogger().info(ChatColor.GREEN + "Successfully connected to a bungeecord server!");
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    private void clientReceive() {
        new BukkitRunnable() {

            @Override
            public void run() {
                new SocketClientReceiver().clientMessageReader(plugin.getBungeeSocket().getInputStream());
            }

        }.runTaskAsynchronously(plugin);
    }

}
