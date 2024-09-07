package me.cubixor.sheepquest.game.kits;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;
import me.cubixor.minigamesapi.spigot.game.arena.GameState;
import me.cubixor.minigamesapi.spigot.utils.Particles;
import me.cubixor.minigamesapi.spigot.utils.Sounds;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.arena.Team;
import me.cubixor.sheepquest.arena.TeamRegion;
import me.cubixor.sheepquest.game.SheepPickupHandler;
import me.cubixor.sheepquest.items.SQItemsRegistry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KitAthlete extends Kit implements Listener {

    private final SheepPickupHandler sheepPickupHandler;

    private final Set<Player> athleteCooldown = new HashSet<>();

    private final boolean throwSheepEnabled;
    private final Vector throwSheepVector;
    private final double throwSheepPower;
    private final double throwSheepDamage;

    private final boolean launchPlayerEnabled;
    private final Vector launchPlayerVector;
    private final double launchPlayerPower;
    private final double launchPlayerRange;
    private final double launchPlayerDamage;
    private final long launchPlayerCooldown;


    public KitAthlete(ArenasRegistry arenasRegistry, SheepPickupHandler sheepPickupHandler, SQItemsRegistry itemsRegistry) {
        super(KitType.ATHLETE, arenasRegistry, itemsRegistry);
        this.sheepPickupHandler = sheepPickupHandler;

        throwSheepEnabled = config.getBoolean("kits.athlete.throw-sheep");
        throwSheepVector = new Vector(0, config.getDouble("kits.athlete.throw-sheep-power-y"), 0);
        throwSheepPower = config.getDouble("kits.athlete.throw-sheep-power");
        throwSheepDamage = config.getDouble("kits.athlete.throw-sheep-damage");

        launchPlayerEnabled = config.getBoolean("kits.athlete.launch-players");
        launchPlayerVector = new Vector(0, config.getDouble("kits.athlete.launch-players-power-y"), 0);
        launchPlayerPower = config.getDouble("kits.athlete.launch-players-power");
        launchPlayerRange = config.getDouble("kits.athlete.launch-players-range");
        launchPlayerDamage = config.getDouble("kits.athlete.launch-players-damage");
        launchPlayerCooldown = Math.round(config.getDouble("kits.athlete.launch-players-cooldown") * 20);
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

        if (throwSheepEnabled && player.getPassenger() instanceof Sheep
            /* && !BonusEntity.isCarrying((Sheep) player.getPassenger())*/) {

            List<Entity> sheep = sheepPickupHandler.removeSheep(player);
            throwSheep(arena, sheep, player);

        } else if (launchPlayerEnabled && evt.getItem().equals(getSecondaryWeapon().getItem())) {
            launchNearbyPlayers(player, arena);
        }

    }


    private void throwSheep(SQArena arena, List<Entity> entityList, Player player) {
        Entity firstEntity = entityList.get(0);
        Team team = arena.getPlayerTeam().get(player);

        for (Entity e : entityList) {
            e.setVelocity(player.getLocation().getDirection().add(throwSheepVector).multiply(throwSheepPower));

            arena.addTask(new BukkitRunnable() {
                @Override
                public void run() {
                    if (!e.isOnGround()) {
                        return;
                    }

                    TeamRegion teamRegion = arena.getTeamRegions().get(team);

                    if (teamRegion.isInRegion(e)) {
                        sheepPickupHandler.bringSheep(player, (Sheep) e, arena);
                    }
                    damageNearbyPlayers(e, throwSheepDamage, player, arena);

                    this.cancel();

                }
            }.runTaskTimer(MinigamesAPI.getPlugin(), 0, 1));
        }

        Sounds.playSound("throw-sheep", firstEntity.getLocation(), arena.getBukkitPlayers());
        Particles.spawnParticle(firstEntity.getLocation().add(0, 1.5, 0), "throw-sheep");
    }

    private void launchNearbyPlayers(Player player, SQArena arena) {
        if (athleteCooldown.contains(player)) {
            return;
        }

        Team team = arena.getPlayerTeam().get(player);

        Sounds.playSound("launch-players", player.getLocation(), arena.getBukkitPlayers());
        Particles.spawnParticle(player.getLocation().add(0, 1.5, 0), "launch-players");

        for (Entity targetEntity : player.getNearbyEntities(launchPlayerRange, launchPlayerRange, launchPlayerRange)) {
            if (targetEntity instanceof Player) {
                Player target = (Player) targetEntity;

                if (arena.getPlayerTeam().get(target).equals(team)) {
                    continue;
                }

                target.damage(launchPlayerDamage, player);
            }

            Vector direction = targetEntity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize()
                    .add(launchPlayerVector).multiply(launchPlayerPower);
            targetEntity.setVelocity(direction);
        }
        addCooldown(player, launchPlayerCooldown);
    }

    private void damageNearbyPlayers(Entity entity, double damage, Player damager, SQArena arena) {
        for (Entity e : entity.getNearbyEntities(1, 1, 1)) {
            if (!(e instanceof Player)) continue;

            Player target = (Player) e;

            if (!arena.getPlayerTeam().get(damager).equals(arena.getPlayerTeam().get(target))) {
                target.damage(damage, damager);
            }

        }
    }

    public void addCooldown(Player player, long cooldown) {
        athleteCooldown.add(player);

        new BukkitRunnable() {
            @Override
            public void run() {
                athleteCooldown.remove(player);
            }
        }.runTaskLater(MinigamesAPI.getPlugin(), cooldown);
    }

}