package me.cubixor.sheepquest.game.kits;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;
import me.cubixor.minigamesapi.spigot.game.items.GameItem;
import me.cubixor.sheepquest.items.SQItemsRegistry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class Kit implements Listener {

    protected final FileConfiguration config;
    protected final ArenasRegistry arenasRegistry;
    private final SQItemsRegistry itemsRegistry;
    private final KitType kitType;
    private final GameItem primaryWeapon;
    private GameItem secondaryWeapon;
    private GameItem shield;

    protected Kit(KitType kitType, ArenasRegistry arenasRegistry, SQItemsRegistry itemsRegistry) {
        this.kitType = kitType;
        this.arenasRegistry = arenasRegistry;
        this.itemsRegistry = itemsRegistry;
        this.config = MinigamesAPI.getPlugin().getConfig();

        String rootPath = "kits." + kitType.toString();

        primaryWeapon = new GameItem(rootPath + ".primary-weapon", rootPath + "-primary-weapon-name", rootPath + "-primary-weapon-lore");
        if (config.getString(rootPath + ".secondary-weapon") != null) {
            secondaryWeapon = new GameItem(rootPath + ".secondary-weapon", rootPath + "-secondary-weapon-name", rootPath + "-secondary-weapon-lore");
        }
        if (config.getBoolean(rootPath + ".shield")) {
            shield = new GameItem(XMaterial.SHIELD.parseItem(), "kits.shield-name", "kits.shield-lore");
        }
    }

    public abstract void giveKit(Player player);

    protected void giveItems(Player player) {
        player.getInventory().setItem(0, getPrimaryWeapon().getItem());
        if (getSecondaryWeapon() != null) {
            player.getInventory().setItem(1, getSecondaryWeapon().getItem());
        }
        if (getShield() != null) {
            player.getInventory().setItemInOffHand(getShield().getItem());
        }
        itemsRegistry.getSheepItem().give(player);
    }

    public KitType getKitType() {
        return kitType;
    }

    public GameItem getPrimaryWeapon() {
        return primaryWeapon;
    }

    public GameItem getSecondaryWeapon() {
        return secondaryWeapon;
    }

    public GameItem getShield() {
        return shield;
    }
}