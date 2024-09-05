package me.cubixor.sheepquest.game.kits;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;
import me.cubixor.minigamesapi.spigot.game.items.GameItem;
import me.cubixor.sheepquest.arena.SQArena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KitArcher extends Kit implements Listener {

    private final GameItem arrow;
    private final int arrowInterval;

    public KitArcher(ArenasRegistry arenasRegistry) {
        super(arenasRegistry, KitType.ARCHER);
        arrow = new GameItem("kits.archer.arrow", "kits.arrow-name", "kits.arrow-lore");
        arrowInterval = (int) Math.round(config.getDouble("kits.archer.arrow-interval") * 20);
    }

    @Override
    public void giveKit(Player player) {
        giveItems(player);
    }

    public void addArrow(Player player) {
        ItemStack itemStack = new ItemStack(arrow.getItem());
        int amount = 1;
        if (player.getInventory().getItem(7) != null) {
            amount = player.getInventory().getItem(7).getAmount() + 1;
        }
        itemStack.setAmount(amount);
        player.getInventory().setItem(7, itemStack);
    }

    public void arrowTimer(SQArena arena) {
        List<Player> archers = arena.getPlayerKit()
                .entrySet()
                .stream()
                .filter(e -> e.getValue().equals(KitType.ARCHER))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (archers.isEmpty()) {
            return;
        }

        arena.addTask(new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : archers) {
                    if (!arena.getRespawnTimer().containsKey(player)) {
                        addArrow(player);
                    }
                }
            }
        }.runTaskTimer(MinigamesAPI.getPlugin(), 0, arrowInterval));
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent evt) {
        if (!(evt.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) evt.getEntity().getShooter();

        if (arenasRegistry.getPlayerLocalArena(player) == null) {
            return;
        }

        evt.getEntity().remove();
    }
}
