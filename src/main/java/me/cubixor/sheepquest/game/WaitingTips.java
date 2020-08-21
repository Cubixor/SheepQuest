package me.cubixor.sheepquest.game;

import me.cubixor.sheepquest.Arena;
import me.cubixor.sheepquest.GameState;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WaitingTips {

    private final SheepQuest plugin;

    public WaitingTips(SheepQuest s) {
        plugin = s;
    }

    public void playerTips(Player player) {
        if(plugin.playerInfo.get(player).tipTask != null && !plugin.playerInfo.get(player).tipTask.isCancelled()){
            plugin.playerInfo.get(player).tipTask.cancel();
        }
        plugin.playerInfo.get(player).tipTask= new BukkitRunnable() {
            @Override
            public void run() {
                Utils utils = new Utils(plugin);
                Arena arena = utils.getArena(player);
                if (arena == null || !(arena.state.equals(GameState.WAITING) || arena.state.equals(GameState.STARTING))) {
                    this.cancel();
                    return;
                }

                List<String> tips = new ArrayList<>(plugin.getMessageList("other.tips"));
                Random random = new Random();
                String tip = tips.get(random.nextInt(tips.size())).replace("%tip-prefix%", plugin.getMessage("other.tip-prefix"));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(tip));

                new BukkitRunnable() {
                    private int period = 0;
                    @Override
                    public void run() {
                        Arena arena1 = utils.getArena(player);
                        if (arena1 == null || !(arena1.state.equals(GameState.WAITING) || arena1.state.equals(GameState.STARTING))) {
                            this.cancel();
                            return;
                        }
                        if (period < 100) {
                            period+=20;
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(tip));
                        } else {
                            period = 0;
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 20);
            }
        }.runTaskTimerAsynchronously(plugin, 0, 100);
    }

}
