package me.cubixor.sheepquest.spigot.api;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class VersionUtils {

    private static boolean is1_8;
    private static boolean before13;
    private static boolean before12;
    private static boolean isBefore17;
    private static boolean isBefore16;
    private static boolean is1416;
    private static boolean isBefore18;
    private static boolean isBefore19;
    private static boolean isBefore193;
    private static boolean isBefore120;

    public static void initialize() {
        is1_8 = !isSupported(XMaterial.PURPUR_BLOCK);
        isBefore17 = !isSupported(XMaterial.AZALEA);
        isBefore16 = !isSupported(XMaterial.NETHERITE_INGOT);
        is1416 = isSupported(XMaterial.CROSSBOW);
        isBefore18 = !isSupported(XMaterial.MUSIC_DISC_OTHERSIDE);
        before13 = !isSupported(XMaterial.KELP);
        before12 = !isSupported(XMaterial.BLACK_CONCRETE_POWDER);
        isBefore19 = !isSupported(XMaterial.MUD);
        isBefore193 = !isSupported(XMaterial.ENDER_DRAGON_SPAWN_EGG);
        isBefore120 = !isSupported(XMaterial.BAMBOO_PLANKS);
    }

    private static boolean isSupported(XMaterial xMaterial) {
        Material material = xMaterial.parseMaterial();
        if (material == null) return false;

        try {
            return material.isEnabledByFeature(Bukkit.getWorlds().get(0));
        } catch (Throwable t) {
            return true;
        }

    }

    public static boolean is1_8() {
        return is1_8;
    }

    public static boolean isBefore13() {
        return before13;
    }

    public static boolean isBefore17() {
        return isBefore17;
    }

    public static boolean isBefore16() {
        return isBefore16;
    }

    public static boolean is1416() {
        return is1416;
    }

    public static boolean isBefore18() {
        return isBefore18;
    }

    public static boolean isBefore19() {
        return isBefore19;
    }

    public static boolean isBefore12() {
        return before12;
    }

    public static boolean isBefore193() {
        return isBefore193;
    }

    public static boolean isBefore120() {
        return isBefore120;
    }
}
