package me.cubixor.sheepquest.game;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;
import me.cubixor.minigamesapi.spigot.game.arena.GameState;
import me.cubixor.minigamesapi.spigot.utils.Particles;
import me.cubixor.minigamesapi.spigot.utils.Sounds;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.arena.Team;
import me.cubixor.sheepquest.items.SQItemsRegistry;
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

import java.util.ArrayList;
import java.util.List;

public class SheepPickupHandler implements Listener {

    private final ArenasRegistry arenasRegistry;
    private final SQItemsRegistry itemsRegistry;
    private final int maxPassengers;

    public SheepPickupHandler(ArenasRegistry arenasRegistry, SQItemsRegistry itemsRegistry) {
        this.arenasRegistry = arenasRegistry;
        this.itemsRegistry = itemsRegistry;
        this.maxPassengers = MinigamesAPI.getPlugin().getConfig().getInt("max-sheep-carried");
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

        //TODO Sheep cooldown
        /*if (arena.getPlayerStats().get(player).getSheepCooldown() != null) {
            return;
        }*/
        if (arena.getRespawnTimer().containsKey(evt.getPlayer())) {
            return;
        }

        if (!evt.getPlayer().getInventory().getItemInMainHand().equals(itemsRegistry.getSheepItem().getItem())) {
            return;
        }

        Team team = arena.getPlayerTeam().get(player);

        if (player.getPassenger() != null) {
            if (arena.getTeamRegions().get(team).isInRegion(player)) {
                regionEnter(player, arena);
            }
        }


        for (Entity e : evt.getPlayer().getNearbyEntities(1, 1, 1)) {
            tryPickup(player, e, arena);
        }
    }

    private void tryPickup(Player player, Entity e, SQArena arena) {
        if (!(e instanceof LivingEntity)) return;
        if (!e.isOnGround()) return;
        if (isCarried(e, arena)) return;

        //TODO BonusEntity
        /*BonusEntity bonusEntity = new BonusEntity();
        if (bonusEntity.pickupEntity(player, (LivingEntity) e)) {
            continue;
        }
        if (player.getPassenger() != null && BonusEntity.isCarrying((LivingEntity) player.getPassenger())) {
            return;
        }*/

        if (!(e instanceof Sheep)) return;

        Team team = arena.getPlayerTeam().get(player);
        Sheep sheep = (Sheep) e;

        if (team.equals(Team.getByDyeColor(sheep.getColor()))) return;

        Entity currPass = player;
        for (int i = 0; i < maxPassengers; i++) {
            if (currPass.getPassenger() == null) {
                if (currPass.setPassenger(sheep)) {
                    pickupSheep(player, i);
                }
                break;
            }

            currPass = currPass.getPassenger();
        }
    }

    private void pickupSheep(Player player, int id) {
        Sounds.playSound("sheep-pick", player);
        if (MinigamesAPI.getPlugin().getConfig().getBoolean("effects.sheep-slowness")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, id, false, false));
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

                List<Entity> sheep = removeSheep(player);

                for (Entity e : sheep) {
                    bringSheep(player, (Sheep) e, arena);
                }

            }
        }.runTaskLater(MinigamesAPI.getPlugin(), 10));
    }

    public void bringSheep(Player player, Sheep sheep, SQArena arena) {
        Team team = arena.getPlayerTeam().get(player);

        //boolean specialSheep = BonusEntity.isCarrying(entity);
        //TODO Add stats, special sheep
        //localArena.getPlayerStats().get(player).setSheepTaken(localArena.getPlayerStats().get(player).getSheepTaken() + 1);

        player.removePotionEffect(PotionEffectType.SLOW);

        Firework firework = (Firework) sheep.getWorld().spawnEntity(sheep.getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = firework.getFireworkMeta();
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        Color color = /*specialSheep ? DyeColor.valueOf(MinigamesAPI.getPlugin().getConfig().getString("special-events.bonus-sheep.color")).getColor() :*/ team.getColor();
        FireworkEffect fwe = FireworkEffect.builder()
                .flicker(true)
                .withColor(color)
                .with(type)
                .withTrail()
                .build();
        fwm.addEffect(fwe);
        fwm.setPower(1);
        firework.setFireworkMeta(fwm);

        addPoint(player, sheep, arena);

        /*if (specialSheep) {
            new BonusEntity().bringEntity(player, entity);
        } else {
            addPoint(player, (Sheep) entity);
        }*/
        //TODO Pathfinding (!!!)
        //Pathfinding.walkToLocation(sheep, Pathfinding.getMiddleArea(localArena.getName(), team), plugin.getConfig().getDouble("sheep-speed"), localArena, team);

        sheep.setColor(team.getDyeColor());
        Sounds.playSound("sheep-bring", sheep.getLocation(), arena.getBukkitPlayers());
        Particles.spawnParticle(player.getLocation().add(0, 1.5, 0), "sheep-bring");
    }

    private void addPoint(Player player, Sheep sheep, SQArena arena) {
        Team team = arena.getPlayerTeam().get(player);

        for (Team opponent : arena.getTeams()) {
            if (sheep.getColor().equals(opponent.getDyeColor()) && !team.equals(opponent)) {
                arena.getPoints().merge(opponent, -1, Integer::sum);
            }
        }
        arena.getPoints().merge(team, 1, Integer::sum);
    }

    public List<Entity> removeSheep(Player player) {
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

        return carried;
    }

    @EventHandler
    public void onItemChange(PlayerItemHeldEvent evt) {
        Player player = evt.getPlayer();
        int oldSlot = evt.getPreviousSlot();

        if (player.getInventory().getItem(oldSlot) == null) return;
        if (arenasRegistry.getPlayerLocalArena(player) == null) return;
        if (!player.getInventory().getItem(oldSlot).equals(itemsRegistry.getSheepItem().getItem())) return;

        removeSheep(player);
    }
}
