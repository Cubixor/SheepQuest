package me.cubixor.sheepquest.game.events;

import com.cryptomorin.xseries.XSound;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.PassengerFixReflection;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.game.PathFinding;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.Team;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class BonusSheep {

    private final SheepQuest plugin;

    public BonusSheep() {
        plugin = SheepQuest.getInstance();
    }

    public void spawnSheep(Arena arena) {
        String arenaString = Utils.getArenaString(arena);

        Location loc = (Location) plugin.getArenasConfig().get("Arenas." + arenaString + ".sheep-spawn");
        Sheep sheep = loc.getWorld().spawn(loc, Sheep.class);
        sheep.setColor(DyeColor.valueOf(plugin.getConfig().getString("special-events.bonus-sheep.color")));
        sheep.setInvulnerable(true);
        sheep.setCustomName(plugin.getMessage("special-events.bonus-sheep-name").replace("%points%", plugin.getConfig().getString("special-events.bonus-sheep.points")));
        arena.getSpecialEventsData().getBonusSheepTeam().put(sheep, Team.NONE);

        Utils.playSound(arena, loc, XSound.matchXSound(plugin.getConfig().getString("special-events.bonus-sheep.sounds.spawn")).get().parseSound(), 1, 0.9f);
        loc.getWorld().spawnParticle(Particle.valueOf(plugin.getConfig().getString("special-events.bonus-sheep.particles.spawn")), loc.getX(), loc.getY() + 1, loc.getZ(), 1, 0, 0, 0, 0.1);

        new PathFinding().walkToLocation(sheep, loc, plugin.getConfig().getDouble("special-events.bonus-sheep.speed"), arena, Team.NONE);

        for (Player p : arena.getPlayers().keySet()) {
            p.sendMessage(plugin.getMessage("special-events.bonus-sheep-spawn"));
        }
    }

    public void bringSheep(Player player, Sheep sheep) {
        Arena arena = Utils.getArena(player);
        Team team = arena.getPlayers().get(player);
        int points = plugin.getConfig().getInt("special-events.bonus-sheep.points");

        player.removePotionEffect(PotionEffectType.SLOW);

        Location path = (Location) plugin.getArenasConfig().get("Arenas." + Utils.getArenaString(arena) + ".teams." + Utils.getTeamString(team) + "-spawn");
        new PathFinding().walkToLocation(sheep, path, plugin.getConfig().getDouble("special-events.bonus-sheep.speed"), arena, team);

        for (Team t : Utils.getTeams()) {
            if (team.equals(t)) {
                continue;
            }
            if (arena.getSpecialEventsData().getBonusSheepTeam().get(sheep).equals(t)) {
                arena.getPoints().replace(t, arena.getPoints().get(t) - points);
            }
        }
        arena.getPoints().replace(team, arena.getPoints().get(team) + points);
        arena.getSpecialEventsData().getBonusSheepTeam().put(sheep, team);

        Utils.playSound(arena, player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("special-events.bonus-sheep.sounds.bring")).get().parseSound(), 1, 0);
        player.getWorld().spawnParticle(Particle.valueOf(plugin.getConfig().getString("special-events.bonus-sheep.particles.bring")), player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ(), 70, 1, 1, 1, 0.1);

    }

    public boolean pickupSheep(Player player, Sheep sheep) {
        if (sheep.getColor().equals(DyeColor.valueOf(plugin.getConfig().getString("special-events.bonus-sheep.color")))) {
            Arena arena = Utils.getArena(player);
            if (!arena.getSpecialEventsData().getBonusSheepTeam().get(sheep).equals(arena.getPlayers().get(player))) {
                if (player.getPassenger() == null) {
                    player.setPassenger(sheep);
                    if (player.getPassenger() != null) {
                        player.playSound(player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.sheep-pick")).get().parseSound(), 100, 1);
                        player.removePotionEffect(PotionEffectType.SLOW);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 2, false, false));
                        carryingParticles(player, sheep);

                        if (plugin.isPassengerFix()) {
                            new PassengerFixReflection().updatePassengers(player);
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    public boolean carryingSheep(Sheep sheep) {
        return sheep.getColor().equals(DyeColor.valueOf(plugin.getConfig().getString("special-events.bonus-sheep.color")));
    }

    public void carryingParticles(Player player, Sheep sheep) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Utils.getArena(player) == null || player.getPassenger() == null || !player.getPassenger().equals(sheep)) {
                    this.cancel();
                    return;
                }

                player.getWorld().spawnParticle(Particle.valueOf(plugin.getConfig().getString("special-events.bonus-sheep.particles.carrying")),
                        player.getLocation().getX(), player.getLocation().getY() + 3.5, player.getLocation().getZ(), 50, 0.1, 0.1, 0.1, 0.1);

            }
        }.runTaskTimer(plugin, 0, 10);
    }
}
