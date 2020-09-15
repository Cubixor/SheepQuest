package me.cubixor.sheepquest.SheepFix;

import net.minecraft.server.v1_9_R2.PacketPlayOutMount;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PassengerFix_1_9_R2 implements PassengerFix {

    public void updatePassengers(Player stacker) {
        PacketPlayOutMount packetMount = new PacketPlayOutMount(((CraftEntity) stacker).getHandle());
        ((CraftPlayer) stacker).getHandle().playerConnection.sendPacket(packetMount);
    }
}
