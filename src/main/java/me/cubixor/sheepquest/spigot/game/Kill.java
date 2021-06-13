package me.cubixor.sheepquest.spigot.game;

import com.cryptomorin.xseries.XSound;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
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
/*
            if(evt.getItem().equals(plugin.getItems().getWeaponItem())) {
                evt.setCancelled(true);
            }
            if(Arrays.asList(evt.getPlayer().getInventory().getArmorContents()).contains(evt.getItem())){

            }
*/
            evt.setCancelled(true);
        }
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

        Player player = (Player) evt.getEntity();
        Player attacker = (Player) evt.getDamager();
        LocalArena localArena = Utils.getLocalArena(player);

        if (localArena == null) {
            return;
        }

        if (!(localArena.getState().equals(GameState.GAME) && attacker.getInventory().getItemInMainHand().equals(plugin.getItems().getWeaponItem()) && !localArena.getRespawnTimer().containsKey(attacker) && !localArena.getRespawnTimer().containsKey(player))) {
            evt.setCancelled(true);
            return;
        }
        if (localArena.getPlayerTeam().get(attacker).equals(localArena.getPlayerTeam().get(player))) {
            evt.setCancelled(true);
            return;
        }

        //attacker.getInventory().getItemInMainHand().

        sheepCooldown(player);
        Utils.removeSheep(player);

        Location loc = player.getLocation();
        loc.setY(loc.getY() + 1);

        if (plugin.getConfig().getBoolean("particles.enable-blood")) {
            attacker.playEffect(loc, Effect.STEP_SOUND, (Object) Material.REDSTONE_BLOCK);
        }


        if (((player.getHealth() - evt.getFinalDamage()) <= 0)) {
            player.setHealth(20);
            player.setAllowFlight(true);

            Utils.playSound(localArena, player.getLocation(), XSound.ENTITY_PLAYER_DEATH.parseSound(), 1, 1);
            attacker.playSound(attacker.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.kill")).get().parseSound(), 100, 2);
            player.playSound(player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.death")).get().parseSound(), 100, 0);
            player.getWorld().spawnParticle(Particle.valueOf(plugin.getConfig().getString("particles.death")), player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ(), 50, 0.1, 0.1, 0.1, 0.1);
            attacker.sendTitle("", plugin.getMessage("game.kill-subtitle")
                    .replace("%team-color%", plugin.getMessage("general." + localArena.getPlayerTeam().get(player).getCode() + "-color"))
                    .replace("%player%", player.getName()), 5, 40, 5);


            String killerColor = plugin.getMessage("general." + localArena.getPlayerTeam().get(attacker).getCode() + "-color");
            String playerColor = plugin.getMessage("general." + localArena.getPlayerTeam().get(player).getCode() + "-color");
            for (Player p : localArena.getPlayerTeam().keySet()) {
                p.hidePlayer(plugin, player);
                p.sendMessage(plugin.getMessage("game.kill").replace("%killer%", attacker.getName()).replace("%player%", player.getName())
                        .replace("%killerTeam%", killerColor).replace("%playerTeam%", playerColor));
            }

            player.getInventory().setItem(0, new ItemStack(Material.AIR));
            player.getInventory().setItem(1, new ItemStack(Material.AIR));
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
        String killerColor = plugin.getMessage("general." + Utils.getLocalArena(player).getPlayerTeam().get(killer).getCode() + "-color");
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
        player.teleport(ConfigUtils.getLocation(localArena.getName(), Utils.getTeamSpawn(localArena.getPlayerTeam().get(player).getCode())));

        player.setAllowFlight(false);
        player.setFlying(false);
        player.getInventory().setItem(0, plugin.getItems().getWeaponItem());
        player.getInventory().setItem(1, plugin.getItems().getSheepItem());
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        for (Player p : localArena.getPlayerTeam().keySet()) {
            p.showPlayer(plugin, player);
        }

        player.sendTitle(plugin.getMessage("game.respawned-title"), plugin.getMessage("game.respawned-subtitle"), 0, 40, 10);
        Utils.playSound(localArena, player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.respawn")).get().parseSound(), 1, 1);
        player.getWorld().spawnParticle(Particle.valueOf(plugin.getConfig().getString("particles.respawn")), player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ(), 50, 1, 1, 1, 0.1);


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
