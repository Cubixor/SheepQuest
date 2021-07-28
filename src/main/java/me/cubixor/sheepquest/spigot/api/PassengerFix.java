package me.cubixor.sheepquest.spigot.api;

import com.cryptomorin.xseries.ReflectionUtils;
import me.cubixor.sheepquest.spigot.SheepQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public class PassengerFix {

    public static void setupPassengerFix() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            return;
        }

        if (version.equals("v1_9_R1") || version.equals("v1_9_R2") || version.equals("v1_10_R1")) {
            SheepQuest.getInstance().setPassengerFix(true);
        }
    }

    public static void updatePassengers(Player stacker) {
        if (!SheepQuest.getInstance().isPassengerFix()) {
            return;
        }
        try {
            Object handle = stacker.getClass().getMethod("getHandle").invoke(stacker);
            Constructor<?> constructor = ReflectionUtils.getNMSClass("PacketPlayOutMount").getConstructor(ReflectionUtils.getNMSClass("Entity"));
            Object packet = constructor.newInstance(handle);

            ReflectionUtils.sendPacket(stacker.getPlayer(), packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
