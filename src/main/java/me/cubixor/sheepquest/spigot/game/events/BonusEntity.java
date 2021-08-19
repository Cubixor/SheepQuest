package me.cubixor.sheepquest.spigot.game.events;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.api.Particles;
import me.cubixor.sheepquest.spigot.api.PassengerFix;
import me.cubixor.sheepquest.spigot.api.Sounds;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.game.Pathfinding;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class BonusEntity {

    private final SheepQuest plugin;

    public BonusEntity() {
        plugin = SheepQuest.getInstance();
    }

    public static boolean isCarrying(LivingEntity entity) {
        SheepQuest plugin = SheepQuest.getInstance();
        if (entity.getType().equals(EntityType.SHEEP)) {
            return ((Sheep) entity).getColor().equals(DyeColor.valueOf(plugin.getConfig().getString("special-events.bonus-sheep.color")));
        } else {
            return entity.getType().toString().equalsIgnoreCase(plugin.getConfig().getString("special-events.bonus-sheep.entity-type"));
        }
    }

    public void spawnEntity(LocalArena localArena) {
        String arenaString = localArena.getName();

        Location loc = ConfigUtils.getLocation(arenaString, ConfigField.SHEEP_SPAWN);
        LivingEntity entity = (LivingEntity) loc.getWorld().spawn(loc, EntityType.valueOf(plugin.getConfig().getString("special-events.bonus-sheep.entity-type")).getEntityClass());
        if (entity.getType().equals(EntityType.SHEEP)) {
            ((Sheep) entity).setColor(DyeColor.valueOf(plugin.getConfig().getString("special-events.bonus-sheep.color")));
        }
        entity.setCustomName(plugin.getMessage("special-events.bonus-sheep-name").replace("%points%", plugin.getConfig().getString("special-events.bonus-sheep.points")));
        entity.setCustomNameVisible(true);
        localArena.getSpecialEventsData().getBonusEntityTeam().put(entity, Team.NONE);

        Sounds.playSound(localArena, loc, "bonus-sheep-spawn");
        Particles.spawnParticle(localArena, loc.add(0, 1.5, 0), "bonus-sheep-spawn");

        Pathfinding.walkToLocation(entity, loc, plugin.getConfig().getDouble("special-events.bonus-sheep.speed"), localArena, Team.NONE);

        for (Player p : localArena.getPlayerTeam().keySet()) {
            p.sendMessage(plugin.getMessage("special-events.bonus-sheep-spawn"));
        }
    }

    public void bringEntity(Player player, LivingEntity entity) {
        LocalArena localArena = Utils.getLocalArena(player);
        Team team = localArena.getPlayerTeam().get(player);
        int points = plugin.getConfig().getInt("special-events.bonus-sheep.points");

        player.removePotionEffect(PotionEffectType.SLOW);

        //Location path = ConfigUtils.getLocation(localArena.getName(), Utils.getTeamSpawn(team.getCode()));
        Pathfinding.walkToLocation(entity, Pathfinding.getMiddleArea(localArena.getName(), team), plugin.getConfig().getDouble("special-events.bonus-sheep.speed"), localArena, team);

        for (Team t : Utils.getTeams()) {
            if (team.equals(t)) {
                continue;
            }
            if (localArena.getSpecialEventsData().getBonusEntityTeam().get(entity).equals(t)) {
                localArena.getPoints().replace(t, localArena.getPoints().get(t) - points);
            }
        }
        localArena.getPoints().replace(team, localArena.getPoints().get(team) + points);
        localArena.getSpecialEventsData().getBonusEntityTeam().put(entity, team);

        localArena.getPlayerStats().get(player).setBonusSheepTaken(localArena.getPlayerStats().get(player).getBonusSheepTaken() + 1);

        Sounds.playSound(localArena, player.getLocation(), "bonus-sheep-bring");
        Particles.spawnParticle(localArena, player.getLocation().add(0, 1.5, 0), "bonus-sheep-bring");

    }

    public boolean pickupEntity(Player player, LivingEntity entity) {
        if (isCarrying(entity)) {
            LocalArena localArena = Utils.getLocalArena(player);
            if (!localArena.getSpecialEventsData().getBonusEntityTeam().get(entity).equals(localArena.getPlayerTeam().get(player))) {
                if (player.getPassenger() == null) {
                    player.setPassenger(entity);
                    if (player.getPassenger() != null) {
                        Sounds.playSound(player, player.getLocation(), "sheep-pick");
                        if (plugin.getConfig().getBoolean("effects.sheep-slowness")) {
                            player.removePotionEffect(PotionEffectType.SLOW);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 2, false, false));
                        }
                        carryingParticles(player, entity);

                        PassengerFix.updatePassengers(player);
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void carryingParticles(Player player, LivingEntity entity) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Utils.getLocalArena(player) == null || player.getPassenger() == null || !player.getPassenger().equals(entity)) {
                    this.cancel();
                    return;
                }

                Particles.spawnParticle(Utils.getLocalArena(player), player.getLocation().add(0, 3.5, 0), "bonus-sheep-carrying");

            }
        }.runTaskTimer(plugin, 0, 10);
    }
}
