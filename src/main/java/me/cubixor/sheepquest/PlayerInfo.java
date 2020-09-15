package me.cubixor.sheepquest;

import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

public class PlayerInfo {

    public Block selMin;
    public Block selMax;
    public String minPlayersChat;
    public String maxPlayersChat;
    public String delete;

    public int confirmTimer = 20;

    public BukkitTask tipTask;
}
