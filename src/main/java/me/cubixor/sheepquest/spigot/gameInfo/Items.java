package me.cubixor.sheepquest.spigot.gameInfo;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class Items {

    private ItemStack teamItem;
    private ItemStack leaveItem;
    private ItemStack setupWandItem;
    private ItemStack sheepItem;
    private ItemStack weaponItem;
    private HashMap<Team, ItemStack> teamItems = new HashMap<>();

    private int teamItemSlot;
    private int leaveItemSlot;
    private int sheepItemSlot;
    private int weaponItemSlot;

    public Items() {
        SheepQuest plugin = SheepQuest.getInstance();

        setTeamItem(Utils.setItemStack("items.team-choose-item.type", "game.team-item-name", "game.team-item-lore"));
        setTeamItemSlot(plugin.getConfig().getInt("items.team-choose-item.slot"));

        setLeaveItem(Utils.setItemStack("items.leave-item.type", "game.leave-item-name", "game.leave-item-lore"));
        setLeaveItemSlot(plugin.getConfig().getInt("items.leave-item.slot"));

        setSheepItem(Utils.setItemStack("items.sheep-item.type", "game.sheep-item-name", "game.sheep-item-lore"));
        setSheepItemSlot(plugin.getConfig().getInt("items.sheep-item.slot"));

        setWeaponItem(Utils.setItemStack("items.weapon-item.type", "game.weapon-item-name", "game.weapon-item-lore"));
        setWeaponItemSlot(plugin.getConfig().getInt("items.weapon-item.slot"));
        //setWeaponItem(NBTEditor.set(getWeaponItem(), (byte) 1, "Unbreakable"));

        setSetupWandItem(Utils.setItemStack(XMaterial.BLAZE_ROD.parseMaterial(), "arena-setup.wand-item-name", "arena-setup.wand-item-lore"));

        for (Team team : Team.values()) {
            if (!team.equals(Team.NONE)) {
                getTeamItems().put(team, Utils.setItemStack(team.getWool(), "game.team-menu-team-" + team.getCode()));
            }
        }
    }

    public ItemStack getTeamItem() {
        return teamItem;
    }

    public void setTeamItem(ItemStack teamItem) {
        this.teamItem = teamItem;
    }

    public ItemStack getLeaveItem() {
        return leaveItem;
    }

    public void setLeaveItem(ItemStack leaveItem) {
        this.leaveItem = leaveItem;
    }

    public ItemStack getSetupWandItem() {
        return setupWandItem;
    }

    public void setSetupWandItem(ItemStack setupWandItem) {
        this.setupWandItem = setupWandItem;
    }

    public ItemStack getSheepItem() {
        return sheepItem;
    }

    public void setSheepItem(ItemStack sheepItem) {
        this.sheepItem = sheepItem;
    }

    public ItemStack getWeaponItem() {
        return weaponItem;
    }

    public void setWeaponItem(ItemStack weaponItem) {
        this.weaponItem = weaponItem;
    }

    public HashMap<Team, ItemStack> getTeamItems() {
        return teamItems;
    }

    public void setTeamItems(HashMap<Team, ItemStack> teamItems) {
        this.teamItems = teamItems;
    }

    public int getTeamItemSlot() {
        return teamItemSlot;
    }

    public void setTeamItemSlot(int teamItemSlot) {
        this.teamItemSlot = teamItemSlot;
    }

    public int getLeaveItemSlot() {
        return leaveItemSlot;
    }

    public void setLeaveItemSlot(int leaveItemSlot) {
        this.leaveItemSlot = leaveItemSlot;
    }

    public int getSheepItemSlot() {
        return sheepItemSlot;
    }

    public void setSheepItemSlot(int sheepItemSlot) {
        this.sheepItemSlot = sheepItemSlot;
    }

    public int getWeaponItemSlot() {
        return weaponItemSlot;
    }

    public void setWeaponItemSlot(int weaponItemSlot) {
        this.weaponItemSlot = weaponItemSlot;
    }
}
