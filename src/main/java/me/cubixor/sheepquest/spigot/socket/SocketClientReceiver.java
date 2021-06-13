package me.cubixor.sheepquest.spigot.socket;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.commands.PlayCommands;
import me.cubixor.sheepquest.spigot.commands.StaffCommands;
import me.cubixor.sheepquest.spigot.game.Signs;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.utils.packets.Packet;
import me.cubixor.sheepquest.utils.packets.classes.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class SocketClientReceiver {

    private final SheepQuest plugin;

    public SocketClientReceiver() {
        plugin = SheepQuest.getInstance();
    }


    public void clientMessageReader(ObjectInputStream in) {
        while (true) {
            Object object;

            try {
                object = in.readObject();
            } catch (IOException e) {
                if (!plugin.getBungeeSocket().getSocket().isClosed()) {
                    new SocketClient().clientSetup(plugin.getConnectionConfig().getString("host"),
                            plugin.getConnectionConfig().getInt("port"),
                            plugin.getConnectionConfig().getString("server-name"));
                    plugin.getLogger().warning(ChatColor.YELLOW + "Lost connection with bungeecord server. Trying to reconnect...");
                }
                return;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return;
            }

            Packet packet = (Packet) object;

            new BukkitRunnable() {
                @Override
                public void run() {
                    switch (packet.getPacketType()) {
                        case ARENA_UPDATE: {
                            ArenaPacket arenaPacket = (ArenaPacket) object;
                            Arena arena = arenaPacket.getArena();
                            if (!plugin.getArenas().containsKey(arena.getName())) {
                                plugin.getSigns().put(arena.getName(), new ArrayList<>());
                            }
                            plugin.getArenas().put(arena.getName(), arena);
                            new Signs().updateSigns(arena.getName());
                            break;
                        }
                        case ARENA_REMOVE: {
                            ArenaPacket arenaPacket = (ArenaPacket) object;
                            plugin.getArenas().remove(arenaPacket.getArena().getName());
                            new Signs().removeSigns(arenaPacket.getArena().getName());
                            break;
                        }
                        case SERVER_ARENAS_ADD: {
                            ServerArenasPacket serverArenasPacket = (ServerArenasPacket) object;
                            addArenas(serverArenasPacket.getArenas());
                            break;
                        }
                        case SERVER_ARENAS_REMOVE: {
                            StringPacket stringPacket = (StringPacket) object;
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
                            JoinPacket joinPacket = (JoinPacket) object;
                            new JoinRunnable().runTask(joinPacket.getPlayer(), joinPacket.getArena());
                            break;
                        }
                        case ARENA_LEAVE: {
                            ArenaPlayerPacket arenaPlayerPacket = (ArenaPlayerPacket) object;
                            new PlayCommands().kickFromLocalArena(Bukkit.getPlayerExact(arenaPlayerPacket.getPlayer()), plugin.getLocalArenas().get(arenaPlayerPacket.getArena().getName()), false, false);
                            break;
                        }
                        case ARENAS_ADD: {
                            ArenasPacket arenasPacket = (ArenasPacket) object;
                            addArenas(arenasPacket.getArenas());
                            break;
                        }
                        case FORCE_START: {
                            ArenaPlayerPacket arenaPlayerPacket = (ArenaPlayerPacket) object;
                            new StaffCommands().forceLocalArenaStart(arenaPlayerPacket.getPlayer(), arenaPlayerPacket.getArena().getName());
                            break;
                        }
                        case FORCE_STOP: {
                            ArenaPlayerPacket arenaPlayerPacket = (ArenaPlayerPacket) object;
                            new StaffCommands().forceLocalArenaStop(arenaPlayerPacket.getPlayer(), arenaPlayerPacket.getArena().getName());
                            break;
                        }
                        case KICK: {
                            KickPacket kickPacket = (KickPacket) object;
                            new StaffCommands().kickFromLocalArena(kickPacket.getPlayer(), kickPacket.getTarget(), kickPacket.getArena().getName());
                            break;
                        }
                    }
                }
            }.runTask(plugin);
        }
    }

    private void addArenas(List<Arena> arenas) {
        for (Arena arena : arenas) {
            plugin.getArenas().put(arena.getName(), arena);
            new Signs().updateSigns(arena.getName());
        }
    }
}
