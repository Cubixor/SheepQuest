package me.cubixor.sheepquest.spigot.game.kits;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.api.Particles;
import me.cubixor.sheepquest.spigot.api.Sounds;
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
        Sounds.playSound(localArena, player.getLocation(), "dash");
        Particles.spawnParticle(localArena, player.getLocation().add(0, 1.5, 0), "dash");

        addCooldown(player);
        player.setVelocity(player.getLocation().getDirection().add(new Vector(0, plugin.getConfig().getDouble("kits.standard.dash-power-y"), 0)).multiply(plugin.getConfig().getDouble("kits.standard.dash-power")));
    }

}
