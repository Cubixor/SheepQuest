package me.cubixor.sheepquest.game;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.config.arenas.ArenasConfigManager;
import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;
import me.cubixor.minigamesapi.spigot.game.arena.GameState;
import me.cubixor.minigamesapi.spigot.utils.Particles;
import me.cubixor.minigamesapi.spigot.utils.Sounds;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.arena.Team;
import me.cubixor.sheepquest.config.SQConfigField;
import me.cubixor.sheepquest.game.kits.KitType;
import me.cubixor.sheepquest.items.SQItemsRegistry;
import me.cubixor.sheepquest.utils.PassengerFix;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SheepPickupHandler implements Listener {

    private final ArenasConfigManager arenasConfigManager;
    private final ArenasRegistry arenasRegistry;
    private final SQItemsRegistry itemsRegistry;

    private final int cooldownTime = 1;
    private final int maxPassengers;

    private final Set<Player> cooldownPlayers = new HashSet<>();

    private final SheepPathfinder sheepPathfinder;
    private PassengerFix passengerFix;

    public SheepPickupHandler(ArenasManager arenasManager, SQItemsRegistry itemsRegistry, SheepPathfinder sheepPathfinder) {
        this.arenasConfigManager = arenasManager.getConfigManager();
        this.arenasRegistry = arenasManager.getRegistry();
        this.itemsRegistry = itemsRegistry;
        this.sheepPathfinder = sheepPathfinder;
        this.maxPassengers = MinigamesAPI.getPlugin().getConfig().getInt("max-sheep-carried");


        try {
            this.passengerFix = new PassengerFix();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent evt) {
        Player player = evt.getPlayer();
        SQArena arena = (SQArena) arenasRegistry.getPlayerLocalArena(player);
        if (arena == null) {
            return;
        }

        if (!arena.getState().equals(GameState.GAME)) {
            return;
        }

        Team team = arena.getPlayerTeam().get(player);

        if (evt.getTo().getBlockY() < -64) {
            player.teleport(arenasConfigManager.getLocation(arena.getName(), SQConfigField.SPAWN, team.toString()));
        }

        if (arena.getRespawnTimer().containsKey(evt.getPlayer())) {
            return;
        }

        if (!evt.getPlayer().getInventory().getItemInMainHand().equals(itemsRegistry.getSheepItem().getItem())) {
            return;
        }

        if (cooldownPlayers.contains(player)) {
            return;
        }

        if (player.getPassenger() != null && arena.getTeamRegions().get(team).isInRegion(player)) {
            regionEnter(player, arena);
        }

        for (Entity e : evt.getPlayer().getNearbyEntities(1, 1, 1)) {
            tryPickup(player, e, arena);
        }
    }

    private void tryPickup(Player player, Entity e, SQArena arena) {
        if (!(e instanceof LivingEntity)) return;
        if (!e.isOnGround()) return;
        if (isCarried(e, arena)) return;

        if (isCarryingBonusEntity(player)) return;

        Team team = arena.getPlayerTeam().get(player);
        boolean isBonusSheep = isBonusEntity(e);

        if (isBonusSheep) {
            if (arena.getBonusEntity().get(e).equals(team)) {
                return;
            }
            if (player.getPassenger() != null) {
                return;
            }

        } else {
            if (!(e instanceof Sheep)) return;
            Sheep sheep = (Sheep) e;
            if (team.equals(Team.getByDyeColor(sheep.getColor()))) return;
        }

        Entity currPass = player;
        for (int i = 0; i < maxPassengers; i++) {
            if (currPass.getPassenger() == null) {
                if (currPass.setPassenger(e) || invokeAddPassenger(currPass, e)) {
                    pickupSheep(player, arena, i, isBonusSheep);
                }
                break;
            }

            currPass = currPass.getPassenger();
        }
    }

    private boolean invokeAddPassenger(Entity entity, Entity passenger) {
        try {
            Class<?> entityClass = Entity.class;
            Method addPassengerMethod = entityClass.getDeclaredMethod("addPassenger", entityClass);
            return (boolean) addPassengerMethod.invoke(entity, passenger);
        } catch (Exception e) {
            return false;
        }
    }

    private void pickupSheep(Player player, SQArena arena, int id, boolean bonus) {
        Sounds.playSound("sheep-pick", player);
        if (MinigamesAPI.getPlugin().getConfig().getBoolean("effects.sheep-slowness")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, bonus ? 2 : id, false, false));
        }

        if (bonus) {
            carryingParticles(player, arena);
        }

        passengerFix.updatePassengers(player);

        if (arena.getPlayerKit().get(player).equals(KitType.STANDARD)) {
            player.setAllowFlight(false);
        }
    }


    private boolean isCarried(Entity entity, SQArena arena) {
        if (entity.getPassenger() != null) {
            return true;
        }

        for (Player p : arena.getBukkitPlayers()) {
            Entity currentEntity = p;
            for (int i = 0; i < maxPassengers; i++) {
                if (currentEntity.getPassenger() == null) {
                    break;
                }

                if (currentEntity.getPassenger().equals(entity)) {
                    return true;
                }

                currentEntity = currentEntity.getPassenger();
            }
        }
        return false;
    }


    private void regionEnter(Player player, SQArena arena) {
        arena.addTask(new BukkitRunnable() {
            @Override
            public void run() {
                if (!arena.getBukkitPlayers().contains(player)) return;

                if (!arena.isInRegion(player, arena.getPlayerTeam().get(player))) return;

                List<Entity> sheep = removePassengers(player);

                for (Entity e : sheep) {
                    bringEntity(player, e, arena);
                }

            }
        }.runTaskLater(MinigamesAPI.getPlugin(), 10));
    }

    public void bringEntity(Player player, Entity entity, SQArena arena) {
        Team team = arena.getPlayerTeam().get(player);

        player.removePotionEffect(PotionEffectType.SLOW);

        Firework firework = (Firework) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = firework.getFireworkMeta();
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        Color color = team.getColor();
        FireworkEffect fwe = FireworkEffect.builder()
                .flicker(true)
                .withColor(color)
                .with(type)
                .withTrail()
                .build();
        fwm.addEffect(fwe);
        fwm.setPower(1);
        firework.setFireworkMeta(fwm);

        if (isBonusEntity(entity)) {
            addBonusPoint(player, (LivingEntity) entity, arena);
        } else {
            addPoint(player, (Sheep) entity, arena);
        }
    }

    private void addPoint(Player player, Sheep sheep, SQArena arena) {
        Team team = arena.getPlayerTeam().get(player);

        for (Team opponent : arena.getTeams()) {
            if (sheep.getColor().equals(opponent.getDyeColor()) && !team.equals(opponent)) {
                arena.getPoints().merge(opponent, -1, Integer::sum);
            }
        }
        arena.getPoints().merge(team, 1, Integer::sum);
        arena.getPlayerGameStats().get(player).addSheepTaken();

        sheep.setColor(team.getDyeColor());
        Sounds.playSound("sheep-bring", sheep.getLocation(), arena.getBukkitPlayers());
        Particles.spawnParticle(player.getLocation().add(0, 1.5, 0), "sheep-bring");

        sheepPathfinder.walkToLocation(sheep, arena.getTeamRegions().get(team), MinigamesAPI.getPlugin().getConfig().getDouble("sheep-speed"), arena);
    }

    private void addBonusPoint(Player player, LivingEntity entity, SQArena arena) {
        Team team = arena.getPlayerTeam().get(player);
        int points = MinigamesAPI.getPlugin().getConfig().getInt("bonus-sheep.points");

        Team bonusEntityTeam = arena.getBonusEntity().get(entity);
        if (!bonusEntityTeam.equals(Team.NONE)) {
            arena.getPoints().merge(bonusEntityTeam, -points, Integer::sum);
        }
        arena.getPoints().merge(team, points, Integer::sum);
        arena.getBonusEntity().replace(entity, team);

        arena.getPlayerGameStats().get(player).addBonusSheepTaken();

        Sounds.playSound("bonus-sheep-bring", player.getLocation(), arena.getBukkitPlayers());
        Particles.spawnParticle(player.getLocation().add(0, 1.5, 0), "bonus-sheep-bring");

        sheepPathfinder.walkToLocation(entity, arena.getTeamRegions().get(team), MinigamesAPI.getPlugin().getConfig().getDouble("bonus-sheep.speed"), arena);
    }

    public List<Entity> removePassengers(Player player) {
        List<Entity> carried = new ArrayList<>();

        Entity currentEntity = player;
        for (int i = 0; i < maxPassengers; i++) {
            if (currentEntity.getPassenger() == null) {
                break;
            }

            carried.add(currentEntity.getPassenger());
            currentEntity = currentEntity.getPassenger();

            Sounds.playSound("sheep-drop", player);
            player.removePotionEffect(PotionEffectType.SLOW);
        }

        for (Entity e : carried) {
            e.eject();
        }
        player.eject();


        if (((SQArena) arenasRegistry.getPlayerLocalArena(player)).getPlayerKit().get(player).equals(KitType.STANDARD)) {
            player.setAllowFlight(true);
        }

        passengerFix.updatePassengers(player);

        return carried;
    }

    @EventHandler
    public void onItemChange(PlayerItemHeldEvent evt) {
        Player player = evt.getPlayer();
        int oldSlot = evt.getPreviousSlot();

        if (player.getInventory().getItem(oldSlot) == null) return;
        if (arenasRegistry.getPlayerLocalArena(player) == null) return;
        if (!player.getInventory().getItem(oldSlot).equals(itemsRegistry.getSheepItem().getItem())) return;

        removePassengers(player);
    }

    public void addCooldown(Player player) {
        cooldownPlayers.add(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                cooldownPlayers.remove(player);
            }
        }.runTaskLater(MinigamesAPI.getPlugin(), cooldownTime * 20L);
    }

    private boolean isBonusEntity(Entity entity) {
        return entity.hasMetadata("SQ-bonus");
    }

    public boolean isCarryingBonusEntity(Player player) {
        return player.getPassenger() != null && isBonusEntity(player.getPassenger());
    }

    public void carryingParticles(Player player, SQArena arena) {
        arena.addTask(new BukkitRunnable() {
            @Override
            public void run() {
                if (!isCarryingBonusEntity(player)) {
                    this.cancel();
                    return;
                }

                Particles.spawnParticle(player.getLocation().add(0, 3.5, 0), "bonus-sheep-carrying");

            }
        }.runTaskTimer(MinigamesAPI.getPlugin(), 0, 10));
    }
}
