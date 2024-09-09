package me.cubixor.sheepquest.game.events;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.messages.Titles;
import com.google.common.collect.ImmutableMap;
import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.config.arenas.ArenasConfigManager;
import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;
import me.cubixor.minigamesapi.spigot.game.arena.GameState;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.minigamesapi.spigot.utils.Particles;
import me.cubixor.minigamesapi.spigot.utils.Sounds;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.config.SQConfigField;
import me.cubixor.sheepquest.game.SheepPickupHandler;
import me.cubixor.sheepquest.game.kits.KitManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class DamageHandler implements Listener {

    private final FileConfiguration config;

    private final ArenasRegistry arenasRegistry;
    private final ArenasConfigManager arenasConfigManager;
    private final KitManager kitManager;
    private final SheepPickupHandler sheepPickupHandler;

    public DamageHandler(ArenasManager arenasManager, KitManager kitManager, SheepPickupHandler sheepPickupHandler) {
        this.arenasRegistry = arenasManager.getRegistry();
        this.arenasConfigManager = arenasManager.getConfigManager();
        this.kitManager = kitManager;
        this.sheepPickupHandler = sheepPickupHandler;
        this.config = MinigamesAPI.getPlugin().getConfig();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent evt) {
        if (!(evt.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) evt.getEntity();
        SQArena arena = (SQArena) arenasRegistry.getPlayerLocalArena(player);
        if (arena == null) {
            return;
        }

        Player attacker;
        if (evt.getDamager() instanceof Player) {
            attacker = (Player) evt.getDamager();
        } else if (evt.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) evt.getDamager();
            if (!(projectile.getShooter() instanceof Player)) {
                return;
            }

            attacker = (Player) projectile.getShooter();
        } else {
            return;
        }

        if (!arena.getState().equals(GameState.GAME)) return;


        if (arena.getRespawnTimer().containsKey(player) ||
                arena.getRespawnTimer().containsKey(attacker) ||
                arena.getPlayerTeam().get(attacker).equals(arena.getPlayerTeam().get(player))) {
            evt.setCancelled(true);
            return;
        }

        if (damagePlayer(player, attacker, arena, evt.getFinalDamage())) {
            evt.setDamage(0);
        }
    }

    public boolean damagePlayer(Player player, Player attacker, SQArena arena, double damage) {
        sheepPickupHandler.addCooldown(player);
        sheepPickupHandler.removePassengers(player);

        Location loc = player.getLocation().add(0, 1, 0);
        Particles.dropBlood(loc);


        if (((player.getHealth() - damage) <= 0)) {
            handleKill(player, attacker, arena);
            return true;
        }

        return false;
    }

    private void handleKill(Player player, Player attacker, SQArena arena) {
        player.setHealth(player.getMaxHealth());
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().clear(0);
        player.getInventory().clear(1);
        player.getInventory().clear(2);
        player.getInventory().clear(7);

        for (Player p : arena.getPlayerTeam().keySet()) {
            p.playSound(player.getLocation(), XSound.ENTITY_PLAYER_DEATH.parseSound(), 1, 1);
        }

        Sounds.playSound("kill", attacker);
        Sounds.playSound("death", player);
        Particles.spawnParticle(player.getLocation().add(0, 1.5, 0), "death");

        Titles.sendTitle(attacker, 5, 40, 5, " ",
                Messages.get("game.kill-subtitle", ImmutableMap.of(
                        "%team-color%", arena.getPlayerTeam().get(player).getChatColor() + "",
                        "%player%", player.getName())));

        String killerColor = arena.getPlayerTeam().get(attacker).getChatColor() + "";
        String playerColor = arena.getPlayerTeam().get(player).getChatColor() + "";
        String killMessage = Messages.get("game.kill", ImmutableMap.of(
                "%killer%", attacker.getName(),
                "%player%", player.getName(),
                "%killerTeam%", killerColor,
                "%playerTeam%", playerColor));

        for (Player p : arena.getBukkitPlayers()) {
            p.hidePlayer(player);
            p.sendMessage(killMessage);
        }

        if (config.getBoolean("effects.kill-blindness")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 2, false, false));
        }
        if (config.getBoolean("effects.kill-slowness")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 3, false, false));
        }

        Location killLoc = player.getLocation().add(0, 3, 0);
        player.teleport(killLoc);

        arena.getPlayerGameStats().get(player).addDeath();
        arena.getPlayerGameStats().get(attacker).addKill();

        respawnTimer(player, attacker, arena);
    }

    private void respawnTimer(Player player, Player killer, SQArena arena) {
        arena.getRespawnTimer().put(player, config.getInt("respawn-time"));

        String killerName = killer.getName();
        String killerColor = arena.getPlayerTeam().get(killer).getChatColor() + "";

        arena.addTask(new BukkitRunnable() {
            @Override
            public void run() {
                if (!arena.getBukkitPlayers().contains(player)) {
                    this.cancel();
                    return;
                }

                int time = arena.getRespawnTimer().get(player);

                if (time <= 0) {
                    respawn(arena, player);
                    this.cancel();
                    return;
                }

                if (time <= 5) {
                    Sounds.playSound("respawn-countdown", player);
                }

                Titles.sendTitle(player, 0, 30, 0,
                        Messages.get("game.respawn-in-title", ImmutableMap.of(
                                "%team-color%", killerColor,
                                "%player%", killerName)),
                        Messages.get("game.respawn-in-subtitle", "%time%", Integer.toString(time)));


                arena.getRespawnTimer().merge(player, -1, Integer::sum);
            }

        }.runTaskTimer(MinigamesAPI.getPlugin(), 0, 20));
    }

    public void respawn(SQArena arena, Player player) {
        player.teleport(arenasConfigManager.getLocation(arena.getName(), SQConfigField.SPAWN, arena.getPlayerTeam().get(player).toString()));

        player.setAllowFlight(false);
        player.setFlying(false);
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }
        for (Player p : arena.getBukkitPlayers()) {
            p.showPlayer(player);
        }

        kitManager.getKits().get(arena.getPlayerKit().get(player)).giveKit(player);

        Titles.sendTitle(player, 0, 40, 10, Messages.get("game.respawned-title"), Messages.get("game.respawned-subtitle"));
        Sounds.playSound("respawn", player);
        Particles.spawnParticle(player.getLocation().add(0, 1.5, 0), "respawn");


        arena.getRespawnTimer().remove(player);
    }
}
