package me.cubixor.sheepquest.bungee.socket;

import me.cubixor.sheepquest.bungee.SheepQuestBungee;
import me.cubixor.sheepquest.utils.SocketConnection;
import net.md_5.bungee.api.ChatColor;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class SocketServer {

    private final SheepQuestBungee plugin;

    public SocketServer() {
        plugin = SheepQuestBungee.getInstance();
    }


    public void serverSetup(int port) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            try {
                plugin.setServerSocket(new ServerSocket(port));

                plugin.getProxy().getLogger().info("[SheepQuest]" + ChatColor.GREEN + " Successfully started a socket server!");
                while (true) {
                    Socket socket = plugin.getServerSocket().accept();

                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    String server = objectInputStream.readUTF();

                    SocketConnection socketConnection = new SocketConnection(socket, objectInputStream, objectOutputStream);
                    plugin.getSpigotSocket().put(server, socketConnection);

                    plugin.getProxy().getLogger().info("[SheepQuest]" + ChatColor.GREEN + " Successfully connected to " + server + " server!");

                    new SocketServerSender().sendAllArenas(server, new ArrayList<>(plugin.getArenas().values()));

                    serverReceive(server);
                }
            } catch (SocketException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private void serverReceive(String server) {
        plugin.getProxy().getScheduler().runAsync(plugin, () -> {
            try {
                new SocketServerReceiver().serverMessageReader(plugin.getSpigotSocket().get(server).getInputStream());
            } catch (IOException e) {
                if (!plugin.isDisabling()) {
                    plugin.getProxy().getLogger().warning("[SheepQuest] Disconnected from " + server + " server!");
                    new SocketServerSender().removeServerArenas(server);
                    plugin.getSpigotSocket().remove(server);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
}