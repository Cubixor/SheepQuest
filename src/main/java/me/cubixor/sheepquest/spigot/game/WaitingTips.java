package me.cubixor.sheepquest.spigot.game;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
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
        if (!plugin.getPlayerInfo().containsKey(player)) {
            return;
        }
        if (plugin.getPlayerInfo().get(player).getTipTask() != null) {
            plugin.getPlayerInfo().get(player).getTipTask().cancel();
            plugin.getPlayerInfo().get(player).setTipTask(null);
        }
        plugin.getPlayerInfo().get(player).setTipTask(new BukkitRunnable() {
            @Override
            public void run() {
                LocalArena localArena = Utils.getLocalArena(player);
                if (localArena == null || !(localArena.getState().equals(GameState.WAITING) || localArena.getState().equals(GameState.STARTING))) {
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
                        LocalArena localArena1 = Utils.getLocalArena(player);
                        if (localArena1 == null || !(localArena1.getState().equals(GameState.WAITING) || localArena1.getState().equals(GameState.STARTING))) {
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
        }.runTaskTimer(plugin, 0, 100));
    }

}
