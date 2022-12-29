package me.cubixor.sheepquest.spigot.api;

import com.cryptomorin.xseries.XMaterial;

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

    public static void initialize() {
        is1_8 = !XMaterial.PURPUR_BLOCK.isSupported();
        isBefore17 = !XMaterial.AZALEA.isSupported();
        isBefore16 = !XMaterial.NETHERITE_INGOT.isSupported();
        is1416 = XMaterial.CROSSBOW.isSupported();
        isBefore18 = !XMaterial.MUSIC_DISC_OTHERSIDE.isSupported();
        before13 = !XMaterial.KELP.isSupported();
        before12 = !XMaterial.BLACK_CONCRETE_POWDER.isSupported();
        isBefore19 = !XMaterial.MUD.isSupported();
        isBefore193 = !XMaterial.ENDER_DRAGON_SPAWN_EGG.isSupported();
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
}
