package me.cubixor.sheepquest.game;

import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.GameState;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WaitingTips {

    private final SheepQuest plugin;

    public WaitingTips() {
        plugin = SheepQuest.getInstance();
    }

    public void playerTips(Player player) {
        if (plugin.getPlayerInfo().get(player).getTipTask() != null) {
            plugin.getPlayerInfo().get(player).getTipTask().cancel();
            plugin.getPlayerInfo().get(player).setTipTask(null);
        }
        plugin.getPlayerInfo().get(player).setTipTask(new BukkitRunnable() {
            @Override
            public void run() {
                Arena arena = Utils.getArena(player);
                if (arena == null || !(arena.getState().equals(GameState.WAITING) || arena.getState().equals(GameState.STARTING))) {
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
                        Arena arena1 = Utils.getArena(player);
                        if (arena1 == null || !(arena1.getState().equals(GameState.WAITING) || arena1.getState().equals(GameState.STARTING))) {
                            this.cancel();
                            return;
                        }
                        if (period < 100) {
                            period += 20;
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(tip));
                        } else {
                            period = 0;
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 20);
            }
        }.runTaskTimerAsynchronously(plugin, 0, 100));
    }

}
