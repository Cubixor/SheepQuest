package me.cubixor.sheepquest.spigot.game.events;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.game.PathFinding;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import org.bukkit.DyeColor;
import org.bukkit.Location;
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

    public void spawnSheep(LocalArena localArena) {
        String arenaString = localArena.getName();

        Location loc = ConfigUtils.getLocation(arenaString, ConfigField.SHEEP_SPAWN);
        Sheep sheep = loc.getWorld().spawn(loc, Sheep.class);
        sheep.setColor(DyeColor.valueOf(plugin.getConfig().getString("special-events.bonus-sheep.color")));
        sheep.setInvulnerable(true);
        sheep.setCustomName(plugin.getMessage("special-events.bonus-sheep-name").replace("%points%", plugin.getConfig().getString("special-events.bonus-sheep.points")));
        localArena.getSpecialEventsData().getBonusSheepTeam().put(sheep, Team.NONE);

        Utils.playSound(localArena, loc, XSound.matchXSound(plugin.getConfig().getString("special-events.bonus-sheep.sounds.spawn")).get().parseSound(), 1, 0.9f);
        loc.getWorld().spawnParticle(XParticle.getParticle(plugin.getConfig().getString("special-events.bonus-sheep.particles.spawn")), loc.getX(), loc.getY() + 1, loc.getZ(), 1, 0, 0, 0, 0.1);

        PathFinding.walkToLocation(sheep, loc, plugin.getConfig().getDouble("special-events.bonus-sheep.speed"), localArena, Team.NONE);

        for (Player p : localArena.getPlayerTeam().keySet()) {
            p.sendMessage(plugin.getMessage("special-events.bonus-sheep-spawn"));
        }
    }

    public void bringSheep(Player player, Sheep sheep) {
        LocalArena localArena = Utils.getLocalArena(player);
        Team team = localArena.getPlayerTeam().get(player);
        int points = plugin.getConfig().getInt("special-events.bonus-sheep.points");

        player.removePotionEffect(PotionEffectType.SLOW);

        //Location path = ConfigUtils.getLocation(localArena.getName(), Utils.getTeamSpawn(team.getCode()));
        PathFinding.walkToLocation(sheep, PathFinding.getMiddleArea(localArena.getName(), team), plugin.getConfig().getDouble("special-events.bonus-sheep.speed"), localArena, team);

        for (Team t : Utils.getTeams()) {
            if (team.equals(t)) {
                continue;
            }
            if (localArena.getSpecialEventsData().getBonusSheepTeam().get(sheep).equals(t)) {
                localArena.getPoints().replace(t, localArena.getPoints().get(t) - points);
            }
        }
        localArena.getPoints().replace(team, localArena.getPoints().get(team) + points);
        localArena.getSpecialEventsData().getBonusSheepTeam().put(sheep, team);

        localArena.getPlayerStats().get(player).setBonusSheepTaken(localArena.getPlayerStats().get(player).getBonusSheepTaken() + 1);

        Utils.playSound(localArena, player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("special-events.bonus-sheep.sounds.bring")).get().parseSound(), 1, 0);
        player.getWorld().spawnParticle(XParticle.getParticle(plugin.getConfig().getString("special-events.bonus-sheep.particles.bring")), player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ(), 70, 1, 1, 1, 0.1);

    }

    public boolean pickupSheep(Player player, Sheep sheep) {
        if (sheep.getColor().equals(DyeColor.valueOf(plugin.getConfig().getString("special-events.bonus-sheep.color")))) {
            LocalArena localArena = Utils.getLocalArena(player);
            if (!localArena.getSpecialEventsData().getBonusSheepTeam().get(sheep).equals(localArena.getPlayerTeam().get(player))) {
                if (player.getPassenger() == null) {
                    player.setPassenger(sheep);
                    if (player.getPassenger() != null) {
                        player.playSound(player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.sheep-pick")).get().parseSound(), 100, 1);
                        if (plugin.getConfig().getBoolean("effects.sheep-slowness")) {
                            player.removePotionEffect(PotionEffectType.SLOW);
                            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 2, false, false));
                        }
                        carryingParticles(player, sheep);
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
                if (Utils.getLocalArena(player) == null || player.getPassenger() == null || !player.getPassenger().equals(sheep)) {
                    this.cancel();
                    return;
                }

                player.getWorld().spawnParticle(XParticle.getParticle(plugin.getConfig().getString("special-events.bonus-sheep.particles.carrying")),
                        player.getLocation().getX(), player.getLocation().getY() + 3.5, player.getLocation().getZ(), 50, 0.1, 0.1, 0.1, 0.1);

            }
        }.runTaskTimer(plugin, 0, 10);
    }
}
