package me.cubixor.sheepquest.spigot.gameInfo;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.config.StatsField;
import me.cubixor.sheepquest.spigot.config.StatsUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class PlayerInfo {

    private Block selMin;
    private Block selMax;
    private String minPlayersChat;
    private String maxPlayersChat;
    private String delete;
    private int confirmTimer = 20;
    private BukkitTask tipTask;
    private final HashMap<StatsField, Integer> stats = new HashMap<>();
    private boolean cooldown;

    public PlayerInfo(String player) {
        Bukkit.getScheduler().runTaskAsynchronously(SheepQuest.getInstance(), () -> {
            for (StatsField statsField : StatsField.values()) {
                stats.put(statsField, StatsUtils.getSavedStats(player, statsField));
            }
        });
    }

    public Block getSelMin() {
        return selMin;
    }

    public void setSelMin(Block selMin) {
        this.selMin = selMin;
    }

    public Block getSelMax() {
        return selMax;
    }

    public void setSelMax(Block selMax) {
        this.selMax = selMax;
    }

    public String getMinPlayersChat() {
        return minPlayersChat;
    }

    public void setMinPlayersChat(String minPlayersChat) {
        this.minPlayersChat = minPlayersChat;
    }

    public String getMaxPlayersChat() {
        return maxPlayersChat;
    }

    public void setMaxPlayersChat(String maxPlayersChat) {
        this.maxPlayersChat = maxPlayersChat;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public int getConfirmTimer() {
        return confirmTimer;
    }

    public void setConfirmTimer(int confirmTimer) {
        this.confirmTimer = confirmTimer;
    }

    public BukkitTask getTipTask() {
        return tipTask;
    }

    public void setTipTask(BukkitTask tipTask) {
        this.tipTask = tipTask;
    }

    public boolean isCooldown() {
        return cooldown;
    }

    public void setCooldown(boolean cooldown) {
        this.cooldown = cooldown;
    }

    public HashMap<StatsField, Integer> getStats() {
        return stats;
    }
}
