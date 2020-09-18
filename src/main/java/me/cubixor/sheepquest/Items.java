package me.cubixor.sheepquest;

import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Items {

    public ItemStack teamItem;
    public ItemStack leaveItem;
    public ItemStack setupWandItem;
    public ItemStack sheepItem;
    public ItemStack weaponItem;
    public HashMap<Team, ItemStack> teamItems = new HashMap<>();

    public int teamItemSlot;
    public int leaveItemSlot;
    public int sheepItemSlot;
    public int weaponItemSlot;

    public Items(SheepQuest plugin) {
        Utils utils = new Utils(plugin);

        teamItem = utils.setItemStack("items.team-choose-item.type", "game.team-item-name", "game.team-item-lore");
        teamItemSlot = plugin.getConfig().getInt("items.team-choose-item.slot");

        leaveItem = utils.setItemStack("items.leave-item.type", "game.leave-item-name", "game.leave-item-lore");
        leaveItemSlot = plugin.getConfig().getInt("items.leave-item.slot");

        sheepItem = utils.setItemStack("items.sheep-item.type", "game.sheep-item-name", "game.sheep-item-lore");
        sheepItemSlot = plugin.getConfig().getInt("items.sheep-item.slot");

        weaponItem = utils.setItemStack("items.weapon-item.type", "game.weapon-item-name", "game.weapon-item-lore");
        weaponItemSlot = plugin.getConfig().getInt("items.weapon-item.slot");
        weaponItem = NBTEditor.set(weaponItem, (byte) 1, "Unbreakable");

        setupWandItem = utils.setItemStack(XMaterial.BLAZE_ROD.parseMaterial(), "arena-setup.wand-item-name", "arena-setup.wand-item-lore");

        for (Team team : Team.values()) {
            if (!team.equals(Team.NONE)) {
                teamItems.put(team, utils.setItemStack(utils.getTeamWool(team), "game.team-menu-team-" + utils.getTeamString(team)));
            }
        }
    }
}
