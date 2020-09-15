package me.cubixor.sheepquest.game;

import me.cubixor.sheepquest.Arena;
import me.cubixor.sheepquest.GameState;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class Kill implements Listener {

    private final SheepQuest plugin;

    public Kill(SheepQuest s) {
        plugin = s;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent evt) {
        if (!(evt.getEntity().getType().equals(EntityType.PLAYER) && evt.getDamager().getType().equals(EntityType.PLAYER))) {
            return;
        }

        if (evt.getEntity().getType().equals(EntityType.PLAYER) && !evt.getDamager().getType().equals(EntityType.PLAYER)) {
            evt.setDamage(0.0F);
            evt.setCancelled(true);
        }

        Utils utils = new Utils(plugin);
        Player player = (Player) evt.getEntity();
        Player attacker = (Player) evt.getDamager();
        Arena arena = utils.getArena(player);

        if (arena == null) {
            return;
        }

        if (!(arena.state.equals(GameState.GAME) && attacker.getInventory().getItemInMainHand().equals(plugin.items.weaponItem) && !arena.respawnTimer.containsKey(attacker) && !arena.respawnTimer.containsKey(player))) {
            evt.setCancelled(true);
            return;
        }
        if (arena.playerTeam.get(attacker).equals(arena.playerTeam.get(player))) {
            evt.setCancelled(true);
            return;
        }

        sheepCooldown(player);
        utils.removeSheep(player);

        if (((player.getHealth() - evt.getFinalDamage()) <= 0)) {
            player.setHealth(20);
            player.setAllowFlight(true);

            String killerColor = plugin.getMessage("general."+utils.getTeamString(arena.playerTeam.get(attacker)) + "-color");
            String playerColor = plugin.getMessage("general."+utils.getTeamString(arena.playerTeam.get(player)) + "-color");
            for (Player p : arena.playerTeam.keySet()) {
                p.hidePlayer(player);
                p.sendMessage(plugin.getMessage("game.kill").replace("%killer%", attacker.getName()).replace("%player%", player.getName())
                        .replace("%killerTeam%", killerColor).replace("%playerTeam%", playerColor));
            }

            player.getInventory().setItem(0, new ItemStack(Material.AIR));
            player.getInventory().setItem(1, new ItemStack(Material.AIR));
            player.setFlying(true);
            Location killLoc = player.getLocation();
            killLoc.setY(killLoc.getY() + 3);
            player.teleport(killLoc);
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 2, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 3, false, false));


            arena.playerStats.get(player).deaths++;
            arena.playerStats.get(attacker).kills++;

            arena.respawnTimer.put(player, plugin.getConfig().getInt("respawn-time"));

            respawn(player);
        }
    }

    private void respawn(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Utils utils = new Utils(plugin);
                Arena arena = utils.getArena(player);
                if (arena.respawnTimer.get(player) > 0 && arena.state.equals(GameState.GAME)) {
                    arena.respawnTimer.replace(player, arena.respawnTimer.get(player) - 1);
                    player.sendTitle(plugin.getMessage("game.respawn-in-title"), plugin.getMessage("game.respawn-in-subtitle").replace("%time%", Integer.toString(arena.respawnTimer.get(player))));
                } else {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.getInventory().setItem(0, plugin.items.weaponItem);
                    player.getInventory().setItem(1, plugin.items.sheepItem);
                    for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                        player.removePotionEffect(potionEffect.getType());
                    }

                    player.teleport((Location) plugin.getArenasConfig().get("Arenas." + utils.getArenaString(arena) + ".teams." + utils.getTeamString(arena.playerTeam.get(player)) + "-spawn"));

                    for (Player p : arena.playerTeam.keySet()) {
                        p.showPlayer(player);
                    }

                    arena.respawnTimer.remove(player);

                    this.cancel();
                }
            }

        }.runTaskTimer(plugin, 0, 20);
    }

    private void sheepCooldown(Player player) {
        Arena arena = new Utils(plugin).getArena(player);
        if (arena.playerStats.get(player).sheepCooldown != null) {
            arena.playerStats.get(player).sheepCooldown.cancel();
            arena.playerStats.get(player).sheepCooldown = null;
        }
        arena.playerStats.get(player).sheepCooldown = new BukkitRunnable() {
            int cooldown = 1;

            @Override
            public void run() {
                if (cooldown > 0) {
                    cooldown--;
                } else {
                    this.cancel();
                    arena.playerStats.get(player).sheepCooldown = null;
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20);
    }

}
