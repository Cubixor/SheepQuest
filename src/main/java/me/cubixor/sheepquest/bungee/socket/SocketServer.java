package me.cubixor.sheepquest.bungee.socket;

import me.cubixor.sheepquest.bungee.SheepQuestBungee;
import me.cubixor.sheepquest.common.SocketConnection;
import net.md_5.bungee.api.ProxyServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SocketServer {

    private final SheepQuestBungee plugin;
    private final Logger logger;
    private final boolean debug;
    private final Map<String, SocketConnection> spigotSocket = new HashMap<>();
    private final SocketServerSender sender;
    private final SocketServerReceiver receiver;
    private ServerSocket serverSocket;

    public SocketServer() {
        plugin = SheepQuestBungee.getInstance();
        logger = ProxyServer.getInstance().getLogger();
        debug = plugin.getBungeeConfig().getBoolean("debug");
        sender = new SocketServerSender(this);
        receiver = new SocketServerReceiver(this);
    }

    public void serverSetup(int port) {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            try {
                serverSocket = new ServerSocket(port);

                ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> sender.send(serverSocket));

                log(Level.INFO, "§aSuccessfully started the socket server!");

                acceptConnection(serverSocket);
            } catch (IOException e) {
                if (debug) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void acceptConnection(ServerSocket serverSocket) {
        try {
            Socket socket = serverSocket.accept();
            if (debug) {
                log(Level.INFO, "Accepted connection from client {0}", socket.getInetAddress().toString());
            }

            ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> acceptConnection(serverSocket));

            clientSetup(socket);
        } catch (IOException e) {
            if (debug) {
                e.printStackTrace();
            }
        }
    }

    private void clientSetup(Socket socket) throws IOException {
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
        String server = objectInputStream.readUTF();

        SocketConnection socketConnection = new SocketConnection(socket, objectOutputStream);
        spigotSocket.put(server, socketConnection);

        sender.sendAllArenas(server, new ArrayList<>(plugin.getArenas().values()));

        log(Level.INFO, "§aSuccessfully connected to the {0} server!", server);

        serverReceive(server, objectInputStream);
    }

    private void serverReceive(String server, ObjectInputStream in) {
        try {
            receiver.serverMessageReader(serverSocket, server, in);
        } catch (IOException e) {
            log(Level.WARNING, "§eDisconnected from the {0} server!", server);

            for (String arena : new ArrayList<>(plugin.getArenas().keySet())) {
                if (plugin.getArenas().get(arena).getServer().equals(server)) {
                    plugin.getArenas().remove(arena);
                }
            }
            sender.sendRemoveServerArenas(server);
            spigotSocket.remove(server);

            if (debug) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnections() {
        try {
            serverSocket.close();
            for (SocketConnection socketConnection : spigotSocket.values()) {
                socketConnection.getSocket().close();
            }
        } catch (IOException e) {
            if (debug) {
                e.printStackTrace();
            }
        }
    }

    public void log(Level level, String message, String... args) {
        logger.log(level, MessageFormat.format("[{0}] {1}", plugin.getDescription().getName(), message), args);
    }

    public boolean isDebug() {
        return debug;
    }

    public SocketConnection getSpigotSocket(String server) {
        return spigotSocket.get(server);
    }

    public Map<String, SocketConnection> getSpigotSockets() {
        return spigotSocket;
    }

    public SocketServerSender getSender() {
        return sender;
    }
}