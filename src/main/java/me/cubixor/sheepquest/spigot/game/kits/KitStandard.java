package me.cubixor.sheepquest.spigot.game.kits;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class KitStandard extends Kit implements Listener {

    public KitStandard() {
        super(KitType.STANDARD);
    }

    public static void addCooldown(Player player) {
        SheepQuest plugin = SheepQuest.getInstance();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (Utils.getLocalArena(player) != null) {
                player.setAllowFlight(true);
            }
        }, Math.round(plugin.getConfig().getDouble("kits.standard.dash-cooldown") * 20));
    }

    @Override
    public void giveKit(Player player) {
        giveItems(player);
        if (plugin.getConfig().getBoolean("kits.standard.dash")) {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent evt) {
        Player player = evt.getPlayer();
        LocalArena localArena = Utils.getLocalArena(player);
        if (localArena != null && plugin.getConfig().getBoolean("kits.standard.dash")
                && Kits.getPlayerKit(player).getKitType().equals(KitType.STANDARD)
                && !localArena.getRespawnTimer().containsKey(player)) {
            evt.setCancelled(true);
            player.setFlying(false);
            player.setAllowFlight(false);
            useDash(player, localArena);
        }
    }

    private void useDash(Player player, LocalArena localArena) {
        Utils.playSound(localArena, player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.dash")).get().parseSound(), 1, 1);
        player.getWorld().spawnParticle(XParticle.getParticle(plugin.getConfig().getString("particles.dash")),
                player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ(), 50, 1, 1, 1, 0.1);
        addCooldown(player);
        player.setVelocity(player.getLocation().getDirection().add(new Vector(0, plugin.getConfig().getDouble("kits.standard.dash-power-y"), 0)).multiply(plugin.getConfig().getDouble("kits.standard.dash-power")));
    }

}
