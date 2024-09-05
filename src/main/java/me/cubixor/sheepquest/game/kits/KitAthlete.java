package me.cubixor.sheepquest.game.kits;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;
import me.cubixor.minigamesapi.spigot.game.arena.GameState;
import me.cubixor.minigamesapi.spigot.game.arena.LocalArena;
import me.cubixor.minigamesapi.spigot.utils.Particles;
import me.cubixor.minigamesapi.spigot.utils.Sounds;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.arena.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class KitAthlete extends Kit implements Listener {

    private final Vector throwSheepVector;
    private final double throwSheepPower;
    private final double throwSheepDamage;


    public KitAthlete(ArenasRegistry arenasRegistry) {
        super(arenasRegistry, KitType.ATHLETE);

        throwSheepVector = new Vector(0, config.getDouble("kits.athlete.throw-sheep-power-y"), 0);
        throwSheepPower = config.getDouble("kits.athlete.throw-sheep-power");
        throwSheepDamage = config.getDouble("kits.athlete.throw-sheep-damage");
    }

    @Override
    public void giveKit(Player player) {
        giveItems(player);
    }

    @EventHandler
    public void onThrow(PlayerInteractEvent evt) {
        if (evt.getHand() == null || !evt.getHand().equals(EquipmentSlot.HAND)) {
            return;
        }

        if (evt.getItem() == null) {
            return;
        }

        Player player = evt.getPlayer();
        SQArena arena = (SQArena) arenasRegistry.getPlayerLocalArena(player);

        if (arena == null) {
            return;
        }

        if (!arena.getState().equals(GameState.GAME)) {
            return;
        }

        if (!arena.getPlayerKit().get(player).equals(KitType.ATHLETE)) {
            return;
        }

        if (config.getBoolean("kits.athlete.throw-sheep") &&
                evt.getItem().equals(plugin.getItems().getSheepItem()) && player.getPassenger() != null && player.getPassenger().getType().equals(EntityType.SHEEP)
            /* && !BonusEntity.isCarrying((Sheep) player.getPassenger())*/) {

            List<Entity> sheep = Utils.removeSheep(player);
            throwSheep(arena, sheep, player);

        } else if (config.getBoolean("kits.athlete.launch-players") && evt.getItem().equals(getSecondaryWeapon().getItem())) {
            launchNearbyPlayers(player, arena);
        }

    }


    private void throwSheep(SQArena arena, List<Entity> entityList, Player player) {
        Entity firstEntity = entityList.get(0);
        Team team = arena.getPlayerTeam().get(player);


        Sounds.playSound("throw-sheep", firstEntity.getLocation(), arena.getBukkitPlayers());
        Particles.spawnParticle(firstEntity.getLocation().add(0, 1.5, 0), "throw-sheep");


        for (Entity e : entityList) {
            e.setVelocity(player.getLocation().getDirection().add(throwSheepVector).multiply(throwSheepPower));

            arena.addTask(new BukkitRunnable() {
                @Override
                public void run() {
                    if (!e.isOnGround()) {
                        return;
                    }

                    if (Utils.isInRegion(e, localArena, team)) {
                        new SheepCarrying().sheepBring(player, (LivingEntity) e);
                    }
                    damageNearbyEntities(e, throwSheepDamage, player, arena);

                    this.cancel();

                }
            }.runTaskTimer(MinigamesAPI.getPlugin(), 0, 1));

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

        Sounds.playSound(localArena, player.getLocation(), "launch-players");
        Particles.spawnParticle(localArena, player.getLocation().add(0, 1.5, 0), "launch-players");

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
            if (target.getHealth() - damage > 0) {
                target.damage(damage);
            }
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
                    if (new Kill().damagePlayer(p, damager, Utils.getLocalArena(damager), damage)) {
                        p.setHealth(20);
                    }
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