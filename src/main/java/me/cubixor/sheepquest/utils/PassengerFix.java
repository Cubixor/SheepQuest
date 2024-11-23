package me.cubixor.sheepquest.utils;

import com.cryptomorin.xseries.reflection.XReflection;
import com.cryptomorin.xseries.reflection.jvm.classes.ClassHandle;
import com.cryptomorin.xseries.reflection.minecraft.MinecraftConnection;
import com.cryptomorin.xseries.reflection.minecraft.MinecraftPackage;
import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;


public class PassengerFix {

    private final boolean enabled;

    private MethodHandle packetPlayOutMountConstructor;

    public PassengerFix() throws ReflectiveOperationException {
        enabled = XReflection.supports(9) && !XReflection.supports(11);

        if (enabled) {
            ClassHandle entity = XReflection.ofMinecraft()
                    .inPackage(MinecraftPackage.NMS, "world.entity")
                    .named("Entity");

            packetPlayOutMountConstructor = XReflection.ofMinecraft()
                    .inPackage(MinecraftPackage.NMS, "network.protocol.game")
                    .named("PacketPlayOutMount")
                    .constructor(entity)
                    .reflect();
        }
    }

    public void updatePassengers(Player stacker) {
        if (!enabled) return;

        try {
            Object handle = stacker.getClass().getMethod("getHandle").invoke(stacker);
            Object packet = packetPlayOutMountConstructor.invoke(handle);

            MinecraftConnection.sendPacket(stacker, packet);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
