package me.cubixor.sheepquest.spigot.socket;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.utils.packets.PacketType;
import me.cubixor.sheepquest.utils.packets.classes.*;
import org.bukkit.Bukkit;

import java.io.ObjectOutputStream;
import java.util.List;

public class SocketClientSender {

    private final SheepQuest plugin;

    public SocketClientSender() {
        plugin = SheepQuest.getInstance();
    }

    private ObjectOutputStream getOutputStream() {
        try {
            return plugin.getBungeeSocket().getOutputStream();
        } catch (Exception e) {
            return null;
        }
    }

    public void sendJoinPacket(Arena arena, String player, boolean joinServer, boolean localJoin) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                ObjectOutputStream out = getOutputStream();
                if (out == null) return;

                JoinPacket joinPacket = new JoinPacket(arena, player, joinServer, localJoin);

                out.writeObject(joinPacket);
                out.flush();
                out.reset();


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    public void sendArenasPacket(String server, List<Arena> arenas) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                ObjectOutputStream out = getOutputStream();
                if (out == null) return;

                ServerArenasPacket serverArenasPacket = new ServerArenasPacket(PacketType.SERVER_ARENAS_ADD, server, arenas);

                out.writeObject(serverArenasPacket);
                out.flush();
                out.reset();


            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    public void sendUpdateArenaPacket(Arena arena) {

        try {
            ObjectOutputStream out = getOutputStream();
            if (out == null) return;

            ArenaPacket arenaPacket = new ArenaPacket(PacketType.ARENA_UPDATE, arena);

            out.writeObject(arenaPacket);
            out.flush();
            out.reset();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendRemoveArenaPacket(Arena arena) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                ObjectOutputStream out = getOutputStream();
                if (out == null) return;

                ArenaPacket arenaPacket = new ArenaPacket(PacketType.ARENA_REMOVE, arena);

                out.writeObject(arenaPacket);
                out.flush();
                out.reset();


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sendBackToServerPacket(BackToServerPacket.ServerPriority serverPriority, String player, String lobby) {
        try {
            ObjectOutputStream out = getOutputStream();
            if (out == null) return;

            BackToServerPacket backToServerPacket = new BackToServerPacket(serverPriority, player, lobby);

            out.writeObject(backToServerPacket);
            out.flush();
            out.reset();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendForceStartPacket(String player, Arena arena) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                ObjectOutputStream out = getOutputStream();
                if (out == null) return;

                ArenaPlayerPacket arenaPlayerPacket = new ArenaPlayerPacket(PacketType.FORCE_START, arena, player);

                out.writeObject(arenaPlayerPacket);
                out.flush();
                out.reset();


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sendForceStopPacket(String player, Arena arena) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                ObjectOutputStream out = getOutputStream();
                if (out == null) return;

                ArenaPlayerPacket arenaPlayerPacket = new ArenaPlayerPacket(PacketType.FORCE_STOP, arena, player);

                out.writeObject(arenaPlayerPacket);
                out.flush();
                out.reset();


            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sendKickPacket(String player, String target, Arena arena) {
        try {
            ObjectOutputStream out = getOutputStream();
            if (out == null) return;

            KickPacket kickPacket = new KickPacket(player, target, arena);

            out.writeObject(kickPacket);
            out.flush();
            out.reset();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
