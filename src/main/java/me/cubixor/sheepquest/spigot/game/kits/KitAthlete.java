package me.cubixor.sheepquest.spigot.game.kits;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.game.Kill;
import me.cubixor.sheepquest.spigot.game.SheepCarrying;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class KitAthlete extends Kit implements Listener {

    public KitAthlete() {
        super(KitType.ATHLETE);
    }

    @Override
    public void giveKit(Player player) {
        giveItems(player);
    }

    @EventHandler
    public void onThrow(PlayerInteractEvent evt) {
        Player player = evt.getPlayer();
        LocalArena localArena = Utils.getLocalArena(player);
        if (evt.getItem() != null && evt.getHand() != null && localArena != null && localArena.getState().equals(GameState.GAME)
                && localArena.getPlayerKit().get(player).equals(KitType.ATHLETE)) {
            if (plugin.getConfig().getBoolean("kits.athlete.throw-sheep") &&
                    evt.getItem().equals(plugin.getItems().getSheepItem()) && player.getPassenger() != null && player.getPassenger().getType().equals(EntityType.SHEEP)) {

                List<Entity> sheep = Utils.removeSheep(player);
                throwSheep(localArena, sheep, player);

            } else if (plugin.getConfig().getBoolean("kits.athlete.launch-players") && evt.getItem().equals(getSecondaryWeapon())) {
                launchNearbyPlayers(player, localArena);
            }
        }
    }


    private void throwSheep(LocalArena localArena, List<Entity> entityList, Player player) {
        Entity firstEntity = entityList.get(0);
        Team team = localArena.getPlayerTeam().get(player);
        Utils.playSound(localArena, firstEntity.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.throw-sheep")).get().parseSound(), 1, 1);
        firstEntity.getWorld().spawnParticle(XParticle.getParticle(plugin.getConfig().getString("particles.throw-sheep")),
                firstEntity.getLocation().getX(), firstEntity.getLocation().getY() + 1.5, firstEntity.getLocation().getZ(), 50, 1, 1, 1, 0.1);
        for (Entity e : entityList) {
            e.setVelocity(player.getLocation().getDirection().add(new Vector(0, plugin.getConfig().getDouble("kits.athlete.throw-sheep-power-y"), 0)).multiply(plugin.getConfig().getDouble("kits.athlete.throw-sheep-power")));
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (e.isOnGround()) {
                        if (Utils.isInRegion(e, localArena.getName(), team)) {
                            new SheepCarrying().sheepBring(player, (Sheep) e);
                        }
                        damageNearbyEntities(e, plugin.getConfig().getDouble("kits.athlete.throw-sheep-damage"), player, localArena);

                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 1);

        }
    }

    private void launchNearbyPlayers(Player player, LocalArena localArena) {
        if (plugin.getPlayerInfo().get(player).isAthleteCooldown()) {
            return;
        }

        Team team = localArena.getPlayerTeam().get(player);
        double range = plugin.getConfig().getDouble("kits.athlete.launch-players-range");
        double power = plugin.getConfig().getDouble("kits.athlete.launch-players-power");
        double powerY = plugin.getConfig().getDouble("kits.athlete.launch-players-power-y");
        double damage = plugin.getConfig().getDouble("kits.athlete.launch-players-damage");
        double cooldown = plugin.getConfig().getDouble("kits.athlete.launch-players-cooldown");

        Utils.playSound(localArena, player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.launch-players")).get().parseSound(), 1, 1);
        player.getWorld().spawnParticle(XParticle.getParticle(plugin.getConfig().getString("particles.launch-players")), player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ(), 50, 0.1, 0.1, 0.1, 0.1);

        for (Entity targetEntity : player.getNearbyEntities(range, range, range)) {
            if (!targetEntity.getType().equals(EntityType.PLAYER)) {
                continue;
            }

            Player target = (Player) targetEntity;

            if (localArena.getPlayerTeam().get(target).equals(team)) {
                continue;
            }

            Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize()
                    .add(new Vector(0, powerY, 0)).multiply(power);
            target.setVelocity(direction);
            target.damage(damage);
            new Kill().damagePlayer(target, player, localArena, damage);
        }
        addCooldown(player, cooldown);
    }

    private void damageNearbyEntities(Entity entity, double damage, Player damager, LocalArena localArena) {
        List<Entity> entities = new ArrayList<>(entity.getNearbyEntities(1, 1, 1));
        for (Entity e : entities) {
            if (e.getType().equals(EntityType.PLAYER)) {
                Player p = (Player) e;
                if (!localArena.getPlayerTeam().get(damager).equals(localArena.getPlayerTeam().get(p))) {
                    p.damage(damage);
                    new Kill().damagePlayer(p, damager, Utils.getLocalArena(damager), damage);
                }
            }
        }
    }

    public void addCooldown(Player player, double cooldown) {
        if (plugin.getPlayerInfo().get(player).isAthleteCooldown()) {
            return;
        }

        plugin.getPlayerInfo().get(player).setAthleteCooldown(true);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (plugin.getPlayerInfo().get(player) != null) {
                plugin.getPlayerInfo().get(player).setAthleteCooldown(false);
            }
        }, Math.round(cooldown * 20));
    }

}