package me.cubixor.sheepquest.spigot.game;

import com.cryptomorin.xseries.messages.ActionBar;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
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

    public void runTipTask() {
        if (plugin.getTipTask() != null) {
            plugin.getTipTask().cancel();
        }

        List<String> tips = new ArrayList<>(plugin.getMessageList("other.tips"));
        List<String> tipsReplaced = new ArrayList<>();
        for (String tip : tips) {
            tipsReplaced.add(tip.replace("%tip-prefix%", plugin.getMessage("other.tip-prefix")));
        }

        Random random = new Random();

        plugin.setTipTask(new BukkitRunnable() {
            @Override
            public void run() {
                String tip = tipsReplaced.get(random.nextInt(tipsReplaced.size()));

                for (LocalArena localArena : plugin.getLocalArenas().values()) {
                    if (!(localArena.getState().equals(GameState.WAITING) || localArena.getState().equals(GameState.STARTING))) {
                        continue;
                    }

                    for (Player player : localArena.getPlayerTeam().keySet()) {
                        ActionBar.sendActionBar(plugin, player, tip, 100);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, Math.round(plugin.getConfig().getDouble("tip-rate") * 20)));
    }

}
