package me.cubixor.sheepquest.spigot.game;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.game.kits.Kits;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Kill implements Listener {

    private final SheepQuest plugin;

    public Kill() {
        plugin = SheepQuest.getInstance();
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent evt) {
        if (Utils.getLocalArena(evt.getPlayer()) != null) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent evt) {
        if (!(evt.getEntity().getType().equals(EntityType.PLAYER) && (evt.getDamager().getType().equals(EntityType.PLAYER))
                || (evt.getDamager() instanceof Projectile && ((Projectile) evt.getDamager()).getShooter() instanceof Player))) {
            return;
        }

        Player player = (Player) evt.getEntity();
        LocalArena localArena = Utils.getLocalArena(player);
        Player attacker;
        if (evt.getDamager() instanceof Player) {
            attacker = (Player) evt.getDamager();
        } else {
            Projectile projectile = (Projectile) evt.getDamager();
            attacker = (Player) projectile.getShooter();
        }

        if (localArena == null) {
            return;
        }

        if (!(localArena.getState().equals(GameState.GAME) && !localArena.getRespawnTimer().containsKey(player) && !localArena.getRespawnTimer().containsKey(attacker))) {
            evt.setCancelled(true);
            return;
        }

        if (!(attacker.getInventory().getItemInMainHand().equals(Kits.getPlayerKit(attacker).getPrimaryWeapon())
                || attacker.getInventory().getItemInMainHand().equals(Kits.getPlayerKit(attacker).getSecondaryWeapon()))) {
            evt.setCancelled(true);
            return;
        }
        if (localArena.getPlayerTeam().get(attacker).equals(localArena.getPlayerTeam().get(player))) {
            evt.setCancelled(true);
            return;
        }
        damagePlayer(player, attacker, localArena, evt.getFinalDamage());
    }

    public void damagePlayer(Player player, Player attacker, LocalArena localArena, double damage) {
        sheepCooldown(player);
        Utils.removeSheep(player);

        Location loc = player.getLocation();
        loc.setY(loc.getY() + 1);

        if (plugin.getConfig().getBoolean("particles.enable-blood")) {
            attacker.playEffect(loc, Effect.STEP_SOUND, (Object) Material.REDSTONE_BLOCK);
        }


        if (((player.getHealth() - damage) <= 0)) {
            player.setHealth(20);
            player.setAllowFlight(true);

            Utils.playSound(localArena, player.getLocation(), XSound.ENTITY_PLAYER_DEATH.parseSound(), 1, 1);
            attacker.playSound(attacker.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.kill")).get().parseSound(), 100, 2);
            player.playSound(player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.death")).get().parseSound(), 100, 0);
            player.getWorld().spawnParticle(XParticle.getParticle(plugin.getConfig().getString("particles.death")), player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ(), 50, 0.1, 0.1, 0.1, 0.1);
            attacker.sendTitle(" ", plugin.getMessage("game.kill-subtitle")
                    .replace("%team-color%", localArena.getPlayerTeam().get(player).getChatColor() + "")
                    .replace("%player%", player.getName()), 5, 40, 5);


            String killerColor = localArena.getPlayerTeam().get(attacker).getChatColor() + "";
            String playerColor = localArena.getPlayerTeam().get(player).getChatColor() + "";
            for (Player p : localArena.getPlayerTeam().keySet()) {
                p.hidePlayer(plugin, player);
                p.sendMessage(plugin.getMessage("game.kill").replace("%killer%", attacker.getName()).replace("%player%", player.getName())
                        .replace("%killerTeam%", killerColor).replace("%playerTeam%", playerColor));
            }

            player.getInventory().setItem(0, new ItemStack(Material.AIR));
            player.getInventory().setItem(1, new ItemStack(Material.AIR));
            player.getInventory().setItem(2, new ItemStack(Material.AIR));
            player.getInventory().setItem(7, new ItemStack(Material.AIR));
            player.setFlying(true);
            Location killLoc = player.getLocation();
            killLoc.setY(killLoc.getY() + 3);
            player.teleport(killLoc);
            if (plugin.getConfig().getBoolean("effects.kill-blindness")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 2, false, false));
            }
            if (plugin.getConfig().getBoolean("effects.kill-slowness")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 3, false, false));
            }


            localArena.getPlayerStats().get(player).setDeaths(localArena.getPlayerStats().get(player).getDeaths() + 1);
            localArena.getPlayerStats().get(attacker).setKills(localArena.getPlayerStats().get(attacker).getKills() + 1);

            localArena.getRespawnTimer().put(player, plugin.getConfig().getInt("respawn-time"));

            respawnTimer(player, attacker);
        }

    }

    private void respawnTimer(Player player, Player killer) {
        String killerName = killer.getName();
        String killerColor = Utils.getLocalArena(player).getPlayerTeam().get(killer).getChatColor() + "";
        new BukkitRunnable() {
            @Override
            public void run() {
                LocalArena localArena = Utils.getLocalArena(player);
                if (localArena != null && localArena.getRespawnTimer().get(player) != null) {
                    int time = localArena.getRespawnTimer().get(player);
                    if (time > 0 && localArena.getState().equals(GameState.GAME)) {

                        player.sendTitle(
                                plugin.getMessage("game.respawn-in-title")
                                        .replace("%team-color%", killerColor)
                                        .replace("%player%", killerName),
                                plugin.getMessage("game.respawn-in-subtitle").replace("%time%", Integer.toString(time)), 0, 30, 0);

                        List<Integer> timeStamps = new ArrayList<Integer>() {{
                            add(5);
                            add(4);
                            add(3);
                            add(2);
                            add(1);
                        }};
                        if (timeStamps.contains(time)) {
                            player.playSound(player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.respawn-countdown")).get().parseSound(), 100, 1);
                        }

                        localArena.getRespawnTimer().replace(player, time - 1);
                    } else {
                        respawn(localArena, player);
                        this.cancel();
                    }
                } else {
                    this.cancel();
                }
            }

        }.runTaskTimer(plugin, 0, 20);
    }

    public void respawn(LocalArena localArena, Player player) {
        player.teleport(ConfigUtils.getSpawn(localArena.getName(), localArena.getPlayerTeam().get(player)));

        player.setAllowFlight(false);
        player.setFlying(false);
        Kits.getPlayerKit(player).giveKit(player);
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        for (Player p : localArena.getPlayerTeam().keySet()) {
            p.showPlayer(plugin, player);
        }

        player.sendTitle(plugin.getMessage("game.respawned-title"), plugin.getMessage("game.respawned-subtitle"), 0, 40, 10);
        Utils.playSound(localArena, player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.respawn")).get().parseSound(), 1, 1);
        player.getWorld().spawnParticle(XParticle.getParticle(plugin.getConfig().getString("particles.respawn")), player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ(), 50, 1, 1, 1, 0.1);


        localArena.getRespawnTimer().remove(player);

    }


    private void sheepCooldown(Player player) {
        LocalArena localArena = Utils.getLocalArena(player);
        if (localArena.getPlayerStats().get(player).getSheepCooldown() != null) {
            localArena.getPlayerStats().get(player).getSheepCooldown().cancel();
            localArena.getPlayerStats().get(player).setSheepCooldown(null);
        }
        localArena.getPlayerStats().get(player).setSheepCooldown(new BukkitRunnable() {
            int cooldown = 1;

            @Override
            public void run() {
                if (cooldown > 0) {
                    cooldown--;
                } else {
                    this.cancel();
                    localArena.getPlayerStats().get(player).setSheepCooldown(null);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20));
    }

}
