package me.cubixor.sheepquest.SheepFix;


import net.minecraft.server.v1_10_R1.PacketPlayOutMount;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PassengerFix_1_10_R1 implements PassengerFix {

    public void updatePassengers(Player stacker) {
        PacketPlayOutMount packetMount = new PacketPlayOutMount(((CraftEntity) stacker).getHandle());
        ((CraftPlayer) stacker).getHandle().playerConnection.sendPacket(packetMount);
    }
}
