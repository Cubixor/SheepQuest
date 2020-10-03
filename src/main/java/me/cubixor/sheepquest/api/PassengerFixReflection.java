package me.cubixor.sheepquest.api;

import me.cubixor.sheepquest.SheepQuest;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

import static me.cubixor.sheepquest.api.ReflectionAPI.getNMSClass;
import static me.cubixor.sheepquest.api.ReflectionAPI.sendPacket;

public class PassengerFixReflection {

    public void setupPassengerFix() {
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

    public void updatePassengers(Player stacker) {
        try {
            Object handle = stacker.getClass().getMethod("getHandle").invoke(stacker);
            Constructor<?> constructor = getNMSClass("PacketPlayOutMount").getConstructor(getNMSClass("Entity"));
            Object packet = constructor.newInstance(handle);

            sendPacket(stacker.getPlayer(), packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
