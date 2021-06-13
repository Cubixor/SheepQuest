package me.cubixor.sheepquest.bungee;

import me.cubixor.sheepquest.bungee.socket.SocketServer;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.utils.SocketConnection;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.util.HashMap;

public class SheepQuestBungee extends Plugin {

    private static SheepQuestBungee instance;
    private final HashMap<String, Arena> arenas = new HashMap<>();
    private final HashMap<String, String> playerServers = new HashMap<>();
    private final HashMap<String, SocketConnection> spigotSocket = new HashMap<>();
    private ServerSocket serverSocket;
    private Configuration bungeeConfig;
    private boolean disabling;

    public static SheepQuestBungee getInstance() {
        return instance;
    }

    public static void setInstance(SheepQuestBungee instance) {
        SheepQuestBungee.instance = instance;
    }

    @Override
    public void onEnable() {
        setInstance(this);
        loadConfigs();
        new SocketServer().serverSetup(getBungeeConfig().getInt("socket-port"));
    }

    @Override
    public void onDisable() {
        setDisabling(true);
        try {
            getServerSocket().close();
            for (SocketConnection socketConnection : spigotSocket.values()) {
                socketConnection.getSocket().close();
                socketConnection.getOutputStream().close();
                socketConnection.getInputStream().close();
            }
        } catch (IOException ignored) {
        }
        getProxy().getScheduler().cancel(this);
    }

    public void loadConfigs() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "bungee-config.yml");

        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("bungee-config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            setBungeeConfig(ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "bungee-config.yml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Arena> getArenas() {
        return arenas;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public HashMap<String, SocketConnection> getSpigotSocket() {
        return spigotSocket;
    }

    public Configuration getBungeeConfig() {
        return bungeeConfig;
    }

    public void setBungeeConfig(Configuration bungeeConfig) {
        this.bungeeConfig = bungeeConfig;
    }

    public HashMap<String, String> getPlayerServers() {
        return playerServers;
    }

    public boolean isDisabling() {
        return disabling;
    }

    public void setDisabling(boolean disabling) {
        this.disabling = disabling;
    }
}
