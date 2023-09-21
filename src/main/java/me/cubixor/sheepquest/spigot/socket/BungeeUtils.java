package me.cubixor.sheepquest.spigot.socket;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.utils.packets.classes.BackToServerPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BungeeUtils {

    private final SheepQuest plugin;

    public BungeeUtils() {
        plugin = SheepQuest.getInstance();
    }


    public void sendBackToServer(Player player) {
        switch (plugin.getConfig().getString("bungee.on-leave")) {
            case "JOIN_SERVER": {
                plugin.getSocketClient().getSender().sendBackToServerPacket(BackToServerPacket.ServerPriority.PREVIOUS,
                        player.getName(), plugin.getConfig().getString("bungee.lobby-server"));
                break;
            }
            case "LOBBY_SERVER": {
                plugin.getSocketClient().getSender().sendBackToServerPacket(BackToServerPacket.ServerPriority.LOBBY,
                        player.getName(), plugin.getConfig().getString("bungee.lobby-server"));
                break;
            }
            case "LEAVE_COMMAND": {
                for (String s : plugin.getConfig().getStringList("bungee.leave-commands")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", player.getName()));
                }
                break;
            }
        }
    }
}
