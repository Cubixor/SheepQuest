package me.cubixor.sheepquest.spigot.game.kits;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.api.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class Kit implements Listener {

    protected final SheepQuest plugin;
    private final KitType kitType;
    private ItemStack primaryWeapon;
    private ItemStack secondaryWeapon;
    private ItemStack shield;

    public Kit(KitType kitType) {
        plugin = SheepQuest.getInstance();
        this.kitType = kitType;
    }

    public void loadItems() {
        primaryWeapon = Utils.setItemStack("kits." + kitType.getCode() + ".primary-weapon",
                "kits." + kitType.getCode() + "-primary-weapon-name", "kits." + kitType.getCode() + "-primary-weapon-lore");
        if (plugin.getConfig().getString("kits." + kitType.getCode() + ".secondary-weapon") != null) {
            secondaryWeapon = Utils.setItemStack("kits." + kitType.getCode() + ".secondary-weapon",
                    "kits." + kitType.getCode() + "-secondary-weapon-name", "kits." + kitType.getCode() + "-secondary-weapon-lore");
        }
        if (plugin.getConfig().getBoolean("kits." + kitType.getCode() + ".shield") && !VersionUtils.is1_8()) {
            shield = Utils.setItemStack(XMaterial.SHIELD.parseMaterial(), "kits.shield-name", "kits.shield-lore");
        }
    }

    public abstract void giveKit(Player player);

    protected void giveItems(Player player) {
        player.getInventory().setItem(0, getPrimaryWeapon());
        int sheepItemSlot = 1;
        if (getSecondaryWeapon() != null) {
            player.getInventory().setItem(1, getSecondaryWeapon());
            sheepItemSlot = 2;
        }
        player.getInventory().setItem(sheepItemSlot, plugin.getItems().getSheepItem());
        if (getShield() != null) {
            player.getInventory().setItemInOffHand(getShield());
        }
    }

    public KitType getKitType() {
        return kitType;
    }

    public ItemStack getPrimaryWeapon() {
        return primaryWeapon;
    }

    public ItemStack getSecondaryWeapon() {
        return secondaryWeapon;
    }

    public ItemStack getShield() {
        return shield;
    }
}