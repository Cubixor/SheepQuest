package me.cubixor.sheepquest;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class ArenaInventories {

    public String arena;
    public Inventory arenasInventory;
    public HashMap<Integer, String> arenaSlot = new HashMap<>();
    public Inventory optionsInventory;
    public Inventory setupInventory;
    public Inventory spawnSetupInventory;
    public Inventory areaSetupInventory;
    public Inventory staffInventory;
    public Inventory playersInventory;
    public HashMap<Integer, Player> playerSlot = new HashMap<>();

    public ArenaInventories(String a) {
        arena = a;
    }
}
