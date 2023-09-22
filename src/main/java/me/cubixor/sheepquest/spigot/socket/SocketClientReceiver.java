package me.cubixor.sheepquest.spigot.socket;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.commands.PlayCommands;
import me.cubixor.sheepquest.spigot.commands.StaffCommands;
import me.cubixor.sheepquest.spigot.config.StatsUtils;
import me.cubixor.sheepquest.spigot.game.Signs;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.utils.packets.Packet;
import me.cubixor.sheepquest.utils.packets.classes.*;
import org.bukkit.Bukkit;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SocketClientReceiver {

    private final SheepQuest plugin;
    private final SocketClient socketClient;

    public SocketClientReceiver(SocketClient socketClient) {
        plugin = SheepQuest.getInstance();
        this.socketClient = socketClient;
    }


    public void clientMessageReader(Socket socket, ObjectInputStream in) throws IOException {
        while (!socket.isClosed()) {
            try {
                Object object = in.readObject();
                Packet packet = (Packet) object;

                if (socketClient.isDebug()) {
                    socketClient.log(Level.INFO, "Packet received: " + packet.getPacketType().toString());
                }

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> handlePacket(packet));

            } catch (ClassNotFoundException |
                     InvalidClassException |
                     StreamCorruptedException |
                     OptionalDataException e) {
                if (socketClient.isDebug()) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void handlePacket(Packet packet) {
        switch (packet.getPacketType()) {
            case ARENA_UPDATE: {
                ArenaPacket arenaPacket = (ArenaPacket) packet;
                Arena arena = arenaPacket.getArena();

                Arena oldArena = plugin.getArenas().get(arena.getName());
                if (oldArena == null) {
                    plugin.getSigns().put(arena.getName(), new ArrayList<>());
                } else if (oldArena.getState().equals(GameState.ENDING) && !arena.getState().equals(GameState.ENDING)) {
                    StatsUtils.updateRankingOrdered();
                }
                plugin.getArenas().put(arena.getName(), arena);
                new Signs().updateSigns(arena.getName());
                break;
            }
            case ARENA_REMOVE: {
                ArenaPacket arenaPacket = (ArenaPacket) packet;
                plugin.getArenas().remove(arenaPacket.getArena().getName());
                new Signs().removeSigns(arenaPacket.getArena().getName());
                break;
            }
            case SERVER_ARENAS_ADD: {
                ServerArenasPacket serverArenasPacket = (ServerArenasPacket) packet;
                addArenas(serverArenasPacket.getArenas());
                break;
            }
            case SERVER_ARENAS_REMOVE: {
                StringPacket stringPacket = (StringPacket) packet;
                for (String arena : plugin.getArenas().keySet()) {
                    if (plugin.getArenas().get(arena).getServer().equals(stringPacket.getString())) {
                        plugin.getArenas().remove(arena);
                        new Signs().updateSigns(arena);
                        break;
                    }
                }
                break;
            }
            case ARENA_JOIN: {
                JoinPacket joinPacket = (JoinPacket) packet;
                new JoinRunnable().runTask(joinPacket.getPlayer(), joinPacket.getArena());
                break;
            }
            case ARENA_LEAVE: {
                ArenaPlayerPacket arenaPlayerPacket = (ArenaPlayerPacket) packet;
                new PlayCommands().kickFromLocalArena(Bukkit.getPlayerExact(arenaPlayerPacket.getPlayer()), plugin.getLocalArenas().get(arenaPlayerPacket.getArena().getName()), false, false);
                break;
            }
            case ARENAS_ADD: {
                ArenasPacket arenasPacket = (ArenasPacket) packet;
                addArenas(arenasPacket.getArenas());
                break;
            }
            case FORCE_START: {
                ArenaPlayerPacket arenaPlayerPacket = (ArenaPlayerPacket) packet;
                new StaffCommands().forceLocalArenaStart(arenaPlayerPacket.getPlayer(), arenaPlayerPacket.getArena().getName());
                break;
            }
            case FORCE_STOP: {
                ArenaPlayerPacket arenaPlayerPacket = (ArenaPlayerPacket) packet;
                new StaffCommands().forceLocalArenaStop(arenaPlayerPacket.getPlayer(), arenaPlayerPacket.getArena().getName());
                break;
            }
            case KICK: {
                KickPacket kickPacket = (KickPacket) packet;
                new StaffCommands().kickFromLocalArena(kickPacket.getPlayer(), kickPacket.getTarget(), kickPacket.getArena().getName());
                break;
            }
        }
    }

    private void addArenas(List<Arena> arenas) {
        for (Arena arena : arenas) {
            plugin.getArenas().put(arena.getName(), arena);
            new Signs().updateSigns(arena.getName());
        }
    }
}
