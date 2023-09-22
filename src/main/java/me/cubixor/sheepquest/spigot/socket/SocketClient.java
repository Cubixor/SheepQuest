package me.cubixor.sheepquest.spigot.socket;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketClient {

    private final SheepQuest plugin;
    private final Logger logger;
    private final boolean debug;
    private final String host;
    private final int port;
    private final String server;
    private final SocketClientSender sender;
    private final SocketClientReceiver receiver;
    private Socket socket;


    public SocketClient(String host, int port, String server) {
        plugin = SheepQuest.getInstance();
        logger = Bukkit.getLogger();
        debug = plugin.getConfig().getBoolean("debug");

        sender = new SocketClientSender(this);
        receiver = new SocketClientReceiver(this);

        this.host = host;
        this.port = port;
        this.server = server;
    }

    public void clientSetup() {
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean done = false;
                boolean msg = socket != null;
                while (!done && plugin.isEnabled()) {
                    done = clientConnect(host, port, server);
                    if (!msg && !done) {
                        msg = true;
                        log(Level.WARNING, "§eCouldn't connect to the bungeecord server. Plugin will try to connect until it succeeds.");

                    }
                }
            }

        }.runTaskAsynchronously(plugin);
    }

    private boolean clientConnect(String host, int port, String server) {
        try {
            socket = new Socket(host, port);

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeUTF(server);
            out.flush();

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> sender.send(socket, out));
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> clientReceive(in));

            List<Arena> arenas = new ArrayList<>();
            for (String arenaStr : plugin.getLocalArenas().keySet()) {
                LocalArena localArena = plugin.getLocalArenas().get(arenaStr);
                Arena arena = new Arena(
                        localArena.getName(),
                        localArena.getServer(),
                        localArena.getState(),
                        localArena.getPlayers());
                arenas.add(arena);
            }

            sender.sendArenasPacket(plugin.getServerName(), arenas);

            log(Level.INFO, "§aSuccessfully connected to the bungeecord server!");
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    private void clientReceive(ObjectInputStream in) {
        try {
            receiver.clientMessageReader(socket, in);
        } catch (IOException e) {
            if (!socket.isClosed()) {
                closeConnections();

                clientSetup();
                log(Level.WARNING, "§eLost connection with the bungeecord server. Trying to reconnect...");

                if (debug) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void closeConnections() {
        try {
            socket.close();
        } catch (IOException e) {
            if (debug) {
                e.printStackTrace();
            }
        }
    }

    public void log(Level level, String message, String... args) {
        logger.log(level, MessageFormat.format("[{0}] {1}", plugin.getName(), message), args);
    }

    public boolean isDebug() {
        return debug;
    }

    public SocketClientSender getSender() {
        return sender;
    }
}
