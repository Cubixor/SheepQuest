package me.cubixor.sheepquest.game.kits;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;
import me.cubixor.minigamesapi.spigot.game.arena.GameState;
import me.cubixor.minigamesapi.spigot.utils.Particles;
import me.cubixor.minigamesapi.spigot.utils.Sounds;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.items.SQItemsRegistry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class KitStandard extends Kit implements Listener {

    private final Vector dashVector;
    private final double dashPower;
    private final int dashCooldown;

    public KitStandard(ArenasRegistry arenasRegistry, SQItemsRegistry itemsRegistry) {
        super(KitType.STANDARD, arenasRegistry, itemsRegistry);

        dashVector = new Vector(0, config.getDouble("kits.standard.dash-power-y"), 0);
        dashPower = config.getDouble("kits.standard.dash-power");
        dashCooldown = (int) Math.round(config.getDouble("kits.standard.dash-cooldown") * 20);
    }

    public void giveKit(Player player) {
        giveItems(player);
        if (config.getBoolean("kits.standard.dash")) {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent evt) {
        Player player = evt.getPlayer();
        SQArena arena = (SQArena) arenasRegistry.getPlayerLocalArena(player);

        if (arena == null) {
            return;
        }

        if (!arena.getState().equals(GameState.GAME)) {
            return;
        }

        if (arena.getRespawnTimer().containsKey(player)) {
            return;
        }

        if (!arena.getPlayerKit().get(player).equals(KitType.STANDARD)) {
            return;
        }

        evt.setCancelled(true);

        if (player.getPassenger() == null/* || !BonusEntity.isCarrying((LivingEntity) player.getPassenger()))*/) {
            useDash(player, arena);
        }

    }

    private void useDash(Player player, SQArena localArena) {
        player.setFlying(false);
        player.setAllowFlight(false);

        Sounds.playSound("dash", player.getLocation(), localArena.getBukkitPlayers());
        Particles.spawnParticle(player.getLocation().add(0, 1.5, 0), "dash");

        player.setVelocity(player.getLocation().getDirection().add(dashVector).multiply(dashPower));

        addCooldown(player);
    }

    private void addCooldown(Player player) {
        Bukkit.getScheduler().runTaskLater(MinigamesAPI.getPlugin(), () -> {
            SQArena arena = (SQArena) arenasRegistry.getPlayerLocalArena(player);
            if (arena == null) {
                return;
            }
            if (arena.getRespawnTimer().containsKey(player)) {
                return;
            }

            player.setAllowFlight(true);
        }, dashCooldown);
    }
}
