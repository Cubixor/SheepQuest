package me.cubixor.sheepquest.spigot.game.kits;

import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class KitArcher extends Kit implements Listener {

    private ItemStack arrow;

    public KitArcher() {
        super(KitType.ARCHER);
    }

    public void loadArrows() {
        arrow = Utils.setItemStack("kits.archer.arrow-type", "kits.arrow-name", "kits.arrow-lore");
    }

    @Override
    public void giveKit(Player player) {
        if (arrow == null) {
            loadArrows();
        }
        giveItems(player);
    }

    public void addArrow(Player player) {
        ItemStack itemStack = new ItemStack(arrow);
        int amount = 1;
        if (player.getInventory().getItem(7) != null) {
            amount = player.getInventory().getItem(7).getAmount() + 1;
        }
        itemStack.setAmount(amount);
        player.getInventory().setItem(7, itemStack);
    }

    public void arrowTimer(String arena) {
        LocalArena localArena = plugin.getLocalArenas().get(arena);
        List<Player> archers = new ArrayList<>();
        for (Player player : localArena.getPlayerKit().keySet()) {
            if (localArena.getPlayerKit().get(player).equals(KitType.ARCHER)) {
                archers.add(player);
            }
        }
        if (archers.isEmpty()) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                LocalArena localArena = plugin.getLocalArenas().get(arena);

                if (!localArena.getState().equals(GameState.GAME)) {
                    this.cancel();
                    return;
                }
                for (Player player : archers) {
                    if ((localArena.getRespawnTimer().get(player) == null || localArena.getRespawnTimer().get(player) == 0)) {
                        addArrow(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, Math.round(plugin.getConfig().getDouble("kits.archer.arrow-interval") * 20));
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent evt) {
        if (!(evt.getEntity().getShooter() instanceof Player && Utils.getLocalArena((Player) evt.getEntity().getShooter()) != null)) {
            return;
        }
        evt.getEntity().remove();

    }
}
