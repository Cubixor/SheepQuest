package me.cubixor.sheepquest.game;

import com.cryptomorin.xseries.XSound;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.TitleAPI;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.GameState;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
        Arena arena = Utils.getArena(player);

        if (arena == null) {
            return;
        }

        if (!(arena.getState().equals(GameState.GAME) && attacker.getInventory().getItemInMainHand().equals(plugin.getItems().getWeaponItem()) && !arena.getRespawnTimer().containsKey(attacker) && !arena.getRespawnTimer().containsKey(player))) {
            evt.setCancelled(true);
            return;
        }
        if (arena.getPlayers().get(attacker).equals(arena.getPlayers().get(player))) {
            evt.setCancelled(true);
            return;
        }

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

            Utils.playSound(arena, player.getLocation(), XSound.ENTITY_PLAYER_DEATH.parseSound(), 1, 1);
            attacker.playSound(attacker.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.kill")).get().parseSound(), 100, 2);
            player.playSound(player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.death")).get().parseSound(), 100, 0);
            player.getWorld().spawnParticle(Particle.valueOf(plugin.getConfig().getString("particles.death")), player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ(), 50, 0.1, 0.1, 0.1, 0.1);
            TitleAPI.sendTitle(attacker, 5, 40, 5, "", plugin.getMessage("game.kill-subtitle")
                    .replace("%team-color%", plugin.getMessage("general." + Utils.getTeamString(arena.getPlayers().get(player)) + "-color"))
                    .replace("%player%", player.getName()));


            String killerColor = plugin.getMessage("general." + Utils.getTeamString(arena.getPlayers().get(attacker)) + "-color");
            String playerColor = plugin.getMessage("general." + Utils.getTeamString(arena.getPlayers().get(player)) + "-color");
            for (Player p : arena.getPlayers().keySet()) {
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


            arena.getPlayerStats().get(player).setDeaths(arena.getPlayerStats().get(player).getDeaths() + 1);
            arena.getPlayerStats().get(attacker).setKills(arena.getPlayerStats().get(attacker).getKills() + 1);

            arena.getRespawnTimer().put(player, plugin.getConfig().getInt("respawn-time"));

            respawnTimer(player, attacker);
        }
    }

    private void respawnTimer(Player player, Player killer) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Arena arena = Utils.getArena(player);
                if (arena != null && arena.getRespawnTimer().get(player) != null) {
                    int time = arena.getRespawnTimer().get(player);
                    if (time > 0 && arena.getState().equals(GameState.GAME)) {

                        TitleAPI.sendTitle(player, 0, 30, 0,
                                plugin.getMessage("game.respawn-in-title")
                                        .replace("%team-color%", plugin.getMessage("general." + Utils.getTeamString(arena.getPlayers().get(killer)) + "-color"))
                                        .replace("%player%", killer.getName()),
                                plugin.getMessage("game.respawn-in-subtitle").replace("%time%", Integer.toString(time)));

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

                        arena.getRespawnTimer().replace(player, time - 1);
                    } else {
                        respawn(arena, player);
                        this.cancel();
                    }
                } else {
                    this.cancel();
                }
            }

        }.runTaskTimer(plugin, 0, 20);
    }

    public void respawn(Arena arena, Player player) {
        player.teleport((Location) plugin.getArenasConfig().get("Arenas." + Utils.getArenaString(arena) + ".teams." + Utils.getTeamString(arena.getPlayers().get(player)) + "-spawn"));

        player.setAllowFlight(false);
        player.setFlying(false);
        player.getInventory().setItem(0, plugin.getItems().getWeaponItem());
        player.getInventory().setItem(1, plugin.getItems().getSheepItem());
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        for (Player p : arena.getPlayers().keySet()) {
            p.showPlayer(player);
        }

        TitleAPI.sendTitle(player, 0, 40, 10, plugin.getMessage("game.respawned-title"), plugin.getMessage("game.respawned-subtitle"));
        Utils.playSound(arena, player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.respawn")).get().parseSound(), 1, 1);
        player.getWorld().spawnParticle(Particle.valueOf(plugin.getConfig().getString("particles.respawn")), player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ(), 50, 1, 1, 1, 0.1);


        arena.getRespawnTimer().remove(player);

    }


    private void sheepCooldown(Player player) {
        Arena arena = Utils.getArena(player);
        if (arena.getPlayerStats().get(player).getSheepCooldown() != null) {
            arena.getPlayerStats().get(player).getSheepCooldown().cancel();
            arena.getPlayerStats().get(player).setSheepCooldown(null);
        }
        arena.getPlayerStats().get(player).setSheepCooldown(new BukkitRunnable() {
            int cooldown = 1;

            @Override
            public void run() {
                if (cooldown > 0) {
                    cooldown--;
                } else {
                    this.cancel();
                    arena.getPlayerStats().get(player).setSheepCooldown(null);
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 20));
    }

}
