package me.cubixor.sheepquest.spigot.gameInfo;

import me.cubixor.sheepquest.spigot.menu.MenuType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class ArenaInventories {

    private String arena;
    private Inventory activeInventory;
    private MenuType inventoryType;
    private HashMap<Integer, String> arenaSlot = new HashMap<>();
    private HashMap<Integer, Player> playerSlot = new HashMap<>();

    public ArenaInventories(String a) {
        setArena(a);
    }

    public String getArena() {
        return arena;
    }

    public void setArena(String arena) {
        this.arena = arena;
    }

    public Inventory getActiveInventory() {
        return activeInventory;
    }

    public void setActiveInventory(Inventory activeInventory) {
        this.activeInventory = activeInventory;
    }

    public MenuType getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(MenuType inventoryType) {
        this.inventoryType = inventoryType;
    }

    public HashMap<Integer, String> getArenaSlot() {
        return arenaSlot;
    }

    public void setArenaSlot(HashMap<Integer, String> arenaSlot) {
        this.arenaSlot = arenaSlot;
    }

    public HashMap<Integer, Player> getPlayerSlot() {
        return playerSlot;
    }

    public void setPlayerSlot(HashMap<Integer, Player> playerSlot) {
        this.playerSlot = playerSlot;
    }


}
