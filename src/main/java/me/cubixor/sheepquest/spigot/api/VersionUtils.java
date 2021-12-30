package me.cubixor.sheepquest.spigot.api;

import org.bukkit.Material;

public class VersionUtils {

    private static boolean is1_8 = false;
    private static boolean isBefore17 = false;
    private static boolean isBefore16 = false;
    private static boolean is1416 = true;
    private static boolean isBefore18 = false;

    public static void initialize() {
        try {
            Material.PURPUR_BLOCK.getClass();
        } catch (NoSuchFieldError e) {
            is1_8 = true;
        }

        try {
            Material.AZALEA.getClass();
        } catch (NoSuchFieldError e) {
            isBefore17 = true;
        }

        try {
            Material.NETHERITE_INGOT.getClass();
        } catch (NoSuchFieldError e) {
            isBefore16 = true;
        }

        try {
            Material.CROSSBOW.getClass();
        } catch (NoSuchFieldError e) {
            is1416 = false;
        }

        try {
            Material.MUSIC_DISC_OTHERSIDE.getClass();
        } catch (NoSuchFieldError e) {
            isBefore18 = true;
        }
    }

    public static boolean is1_8() {
        return is1_8;
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


}
