package me.cubixor.sheepquest.gameInfo;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public class ArenaInventories {

    private String arena;
    private Inventory arenasInventory;
    private HashMap<Integer, String> arenaSlot = new HashMap<>();
    private Inventory optionsInventory;
    private Inventory setupInventory;
    private Inventory spawnSetupInventory;
    private Inventory areaSetupInventory;
    private Inventory staffInventory;
    private Inventory playersInventory;
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

    public Inventory getArenasInventory() {
        return arenasInventory;
    }

    public void setArenasInventory(Inventory arenasInventory) {
        this.arenasInventory = arenasInventory;
    }

    public HashMap<Integer, String> getArenaSlot() {
        return arenaSlot;
    }

    public void setArenaSlot(HashMap<Integer, String> arenaSlot) {
        this.arenaSlot = arenaSlot;
    }

    public Inventory getOptionsInventory() {
        return optionsInventory;
    }

    public void setOptionsInventory(Inventory optionsInventory) {
        this.optionsInventory = optionsInventory;
    }

    public Inventory getSetupInventory() {
        return setupInventory;
    }

    public void setSetupInventory(Inventory setupInventory) {
        this.setupInventory = setupInventory;
    }

    public Inventory getSpawnSetupInventory() {
        return spawnSetupInventory;
    }

    public void setSpawnSetupInventory(Inventory spawnSetupInventory) {
        this.spawnSetupInventory = spawnSetupInventory;
    }

    public Inventory getAreaSetupInventory() {
        return areaSetupInventory;
    }

    public void setAreaSetupInventory(Inventory areaSetupInventory) {
        this.areaSetupInventory = areaSetupInventory;
    }

    public Inventory getStaffInventory() {
        return staffInventory;
    }

    public void setStaffInventory(Inventory staffInventory) {
        this.staffInventory = staffInventory;
    }

    public Inventory getPlayersInventory() {
        return playersInventory;
    }

    public void setPlayersInventory(Inventory playersInventory) {
        this.playersInventory = playersInventory;
    }

    public HashMap<Integer, Player> getPlayerSlot() {
        return playerSlot;
    }

    public void setPlayerSlot(HashMap<Integer, Player> playerSlot) {
        this.playerSlot = playerSlot;
    }
}
