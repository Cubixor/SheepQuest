package me.cubixor.sheepquest.bungee;

import me.cubixor.sheepquest.bungee.socket.SocketServer;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class SheepQuestBungee extends Plugin {

    private static SheepQuestBungee instance;
    private final Map<String, Arena> arenas = new HashMap<>();
    private final Map<String, String> playerServers = new HashMap<>();
    private Configuration bungeeConfig;
    private SocketServer socketServer;

    public static SheepQuestBungee getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        loadConfigs();
        socketServer = new SocketServer();
        socketServer.serverSetup(getBungeeConfig().getInt("socket-port"));
    }

    @Override
    public void onDisable() {
        socketServer.closeConnections();
        getProxy().getScheduler().cancel(this);
    }

    public void loadConfigs() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        try {
            String fileName = "bungee-config.yml";
            File file = new File(getDataFolder(), fileName);
            if (!file.exists()) {
                InputStream in = getResourceAsStream(fileName);
                Files.copy(in, file.toPath());
                in.close();
            }

            bungeeConfig = ConfigurationProvider.getProvider(YamlConfiguration.class)
                    .load(new File(getDataFolder(), fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Arena> getArenas() {
        return arenas;
    }

    public Configuration getBungeeConfig() {
        return bungeeConfig;
    }

    public Map<String, String> getPlayerServers() {
        return playerServers;
    }
}
