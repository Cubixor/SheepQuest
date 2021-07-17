package me.cubixor.sheepquest.spigot.gameInfo;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.game.kits.KitType;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Items {

    private final ItemStack teamItem;
    private final ItemStack kitsItem;
    private final ItemStack leaveItem;
    private final ItemStack setupWandItem;
    private final ItemStack sheepItem;
    private final HashMap<Team, ItemStack> teamItems = new HashMap<>();
    private final Inventory kitsInventory;

    private final int teamItemSlot;
    private final int kitsItemSlot;
    private final int leaveItemSlot;
    private final int sheepItemSlot;

    public Items() {
        SheepQuest plugin = SheepQuest.getInstance();

        teamItem = Utils.setItemStack("items.team-choose-item.type", "game.team-item-name", "game.team-item-lore");
        teamItemSlot = plugin.getConfig().getInt("items.team-choose-item.slot");

        kitsItem = Utils.setItemStack("items.kit-choose-item.type", "kits.item-name", "kits.item-lore");
        kitsItemSlot = plugin.getConfig().getInt("items.kit-choose-item.slot");

        leaveItem = Utils.setItemStack("items.leave-item.type", "game.leave-item-name", "game.leave-item-lore");
        leaveItemSlot = plugin.getConfig().getInt("items.leave-item.slot");

        sheepItem = Utils.setItemStack("items.sheep-item.type", "game.sheep-item-name", "game.sheep-item-lore");
        sheepItemSlot = plugin.getConfig().getInt("items.sheep-item.slot");

        setupWandItem = Utils.setItemStack(XMaterial.BLAZE_ROD.parseMaterial(), "arena-setup.wand-item-name", "arena-setup.wand-item-lore");

        for (Team team : Utils.getTeams()) {
            getTeamItems().put(team, Utils.setItemStack(team.getWool(), "game.team-menu-team-" + team.getCode()));
        }

        kitsInventory = Bukkit.createInventory(null, 9, plugin.getMessage("kits.menu-name"));
        for (KitType kitType : KitType.values()) {
            ItemStack kitItem = Utils.setItemStack("kits." + kitType.getCode() + ".menu-icon", "kits." + kitType.getCode() + "-name", "kits." + kitType.getCode() + "-lore");
            kitsInventory.setItem(kitType.getId(), kitItem);
        }
    }

    public ItemStack getTeamItem() {
        return teamItem;
    }

    public ItemStack getKitsItem() {
        return kitsItem;
    }

    public ItemStack getLeaveItem() {
        return leaveItem;
    }

    public ItemStack getSetupWandItem() {
        return setupWandItem;
    }

    public ItemStack getSheepItem() {
        return sheepItem;
    }

    public HashMap<Team, ItemStack> getTeamItems() {
        return teamItems;
    }

    public Inventory getKitsInventory() {
        return kitsInventory;
    }

    public int getTeamItemSlot() {
        return teamItemSlot;
    }

    public int getKitsItemSlot() {
        return kitsItemSlot;
    }

    public int getLeaveItemSlot() {
        return leaveItemSlot;
    }

    public int getSheepItemSlot() {
        return sheepItemSlot;
    }
}
