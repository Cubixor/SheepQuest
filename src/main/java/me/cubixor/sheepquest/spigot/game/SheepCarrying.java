package me.cubixor.sheepquest.spigot.game;

import com.cryptomorin.xseries.XSound;
import com.cryptomorin.xseries.particles.XParticle;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.game.events.BonusSheep;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import org.bukkit.Color;
import org.bukkit.DyeColor;
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

public class SheepCarrying implements Listener {

    private final SheepQuest plugin;

    public SheepCarrying() {
        plugin = SheepQuest.getInstance();
    }


    @EventHandler
    public void onMove(PlayerMoveEvent evt) {
        Player player = evt.getPlayer();
        LocalArena localArena = Utils.getLocalArena(player);
        if (localArena == null) {
            return;
        }
        if (!localArena.getState().equals(GameState.GAME)) {
            return;
        }

        if (localArena.getPlayerStats().get(player).getSheepCooldown() != null) {
            return;
        }

        Team team = localArena.getPlayerTeam().get(player);

        for (Entity e : evt.getPlayer().getNearbyEntities(1, 1, 1)) {

            if (e.getType().equals(EntityType.SHEEP) &&
                    evt.getPlayer().getInventory().getItemInMainHand().equals(plugin.getItems().getSheepItem()) &&
                    !localArena.getRespawnTimer().containsKey(evt.getPlayer()) &&
                    !isSheepCarried(e, localArena)) {
                Sheep sheep = (Sheep) e;
                if (sheep.getColor().equals(DyeColor.WHITE) || !team.equals(getTeamByColor(sheep.getColor()))) {
                    boolean pas1Exists = player.getPassenger() != null;
                    boolean pas2Exists;
                    boolean pas3Exists;

                    if (pas1Exists) {
                        if (player.getPassenger().equals(e)) {
                            continue;
                        }
                        pas2Exists = player.getPassenger().getPassenger() != null;
                        if (pas2Exists) {
                            if (player.getPassenger().getPassenger().equals(e)) {
                                continue;
                            }
                            pas3Exists = player.getPassenger().getPassenger().getPassenger() != null;
                            if (pas3Exists) {
                                break;
                            }
                        }
                    }

                    BonusSheep bonusSheep = new BonusSheep();
                    if (bonusSheep.pickupSheep(player, sheep)) {
                        continue;
                    }

                    if (evt.getPlayer().getPassenger() == null) {
                        player.setPassenger(e);
                        if (player.getPassenger() != null) {
                            player.playSound(player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.sheep-pick")).get().parseSound(), 100, 1);
                            if (plugin.getConfig().getBoolean("effects.sheep-slowness")) {
                                player.removePotionEffect(PotionEffectType.SLOW);
                                evt.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 0, false, false));
                            }
                        }
                    } else {
                        Entity pas1 = evt.getPlayer().getPassenger();
                        if (bonusSheep.carryingSheep((Sheep) pas1)) {
                            return;
                        }
                        if (pas1.getPassenger() == null) {
                            pas1.setPassenger(e);
                            if (pas1.getPassenger() != null) {
                                player.playSound(player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.sheep-pick")).get().parseSound(), 100, 1);
                                if (plugin.getConfig().getBoolean("effects.sheep-slowness")) {
                                    player.removePotionEffect(PotionEffectType.SLOW);
                                    evt.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 1, false, false));
                                }
                            }
                        } else if (pas1.getPassenger() != null && !pas1.getPassenger().equals(e)) {
                            Entity pas2 = pas1.getPassenger();
                            if (pas2.getPassenger() == null) {
                                pas2.setPassenger(e);
                                if (pas2.getPassenger() != null) {
                                    player.playSound(player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.sheep-pick")).get().parseSound(), 100, 1);
                                    if (plugin.getConfig().getBoolean("effects.sheep-slowness")) {
                                        player.removePotionEffect(PotionEffectType.SLOW);
                                        evt.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 2, false, false));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        if (player.getPassenger() != null && player.getPassenger().getType().equals(EntityType.SHEEP)) {
            if (Utils.isInRegion(player, localArena.getName(), team)) {
                regionEnter(player);
            }
        }
    }

    private boolean isSheepCarried(Entity entity, LocalArena localArena) {
        if (entity.getPassenger() != null) {
            return true;
        }
        for (Player p : localArena.getPlayerTeam().keySet()) {
            if (p.getPassenger() != null) {
                if (p.getPassenger().equals(entity)) {
                    return true;
                }
                if (p.getPassenger().getPassenger() != null) {
                    if (p.getPassenger().getPassenger().equals(entity)) {
                        return true;
                    }
                    if (p.getPassenger().getPassenger().getPassenger() != null) {
                        if (p.getPassenger().getPassenger().getPassenger().equals(entity)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void regionEnter(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                LocalArena localArena = Utils.getLocalArena(player);
                if (localArena == null) {
                    return;
                }
                if (Utils.isInRegion(player, localArena.getName(), localArena.getPlayerTeam().get(player))) {
                    if (player.getPassenger() != null) {
                        if (player.getPassenger().getPassenger() != null) {
                            if (player.getPassenger().getPassenger().getPassenger() != null) {

                                sheepBring(player, (Sheep) player.getPassenger().getPassenger().getPassenger());

                                player.getPassenger().getPassenger().eject();

                            }
                            sheepBring(player, (Sheep) player.getPassenger().getPassenger());

                            player.getPassenger().eject();

                        }

                        sheepBring(player, (Sheep) player.getPassenger());

                        player.eject();

                    }

                }

            }
        }.runTaskLater(plugin, 10);
    }

    public void sheepBring(Player player, Sheep sheep) {
        LocalArena localArena = Utils.getLocalArena(player);
        Team team = localArena.getPlayerTeam().get(player);
        boolean specialSheep = sheep.getColor().equals(DyeColor.valueOf(plugin.getConfig().getString("special-events.bonus-sheep.color")));

        localArena.getPlayerStats().get(player).setSheepTaken(localArena.getPlayerStats().get(player).getSheepTaken() + 1);
        player.removePotionEffect(PotionEffectType.SLOW);

        Firework firework = (Firework) sheep.getWorld().spawnEntity(sheep.getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = firework.getFireworkMeta();
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        Color color = specialSheep ? DyeColor.valueOf(plugin.getConfig().getString("special-events.bonus-sheep.color")).getColor() : team.getColor();
        FireworkEffect fwe = FireworkEffect.builder().flicker(true).withColor(color).with(type).withTrail().build();
        fwm.addEffect(fwe);
        fwm.setPower(1);
        firework.setFireworkMeta(fwm);

        if (specialSheep) {
            new BonusSheep().bringSheep(player, sheep);
        } else {
            addPoint(player, sheep);
        }
    }

    private void addPoint(Player player, Sheep sheep) {
        LocalArena localArena = Utils.getLocalArena(player);
        Team team = localArena.getPlayerTeam().get(player);

        //Location path = ConfigUtils.getLocation(localArena.getName(), Utils.getTeamSpawn(team.getCode()));
        PathFinding.walkToLocation(sheep, PathFinding.getMiddleArea(localArena.getName(), team), plugin.getConfig().getDouble("sheep-speed"), localArena, team);

        for (Team t : Utils.getTeams()) {
            if (sheep.getColor().equals(t.getDyeColor()) && !team.equals(t)) {
                localArena.getPoints().replace(t, localArena.getPoints().get(t) - 1);
            }
        }
        localArena.getPoints().replace(team, localArena.getPoints().get(team) + 1);

        sheep.setColor(team.getDyeColor());

        Utils.playSound(localArena, sheep.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.sheep-bring")).get().parseSound(), 1, 1);
        sheep.getWorld().spawnParticle(XParticle.getParticle(plugin.getConfig().getString("particles.sheep-bring")), sheep.getLocation().getX(), sheep.getLocation().getY() + 1.5, sheep.getLocation().getZ(), 50, 1, 1, 1, 0.1);
    }

    @EventHandler
    public void onItemChange(PlayerItemHeldEvent evt) {
        int oldSlot = evt.getPreviousSlot();
        if (evt.getPlayer().getInventory().getItem(oldSlot) != null) {
            if (evt.getPlayer().getInventory().getItem(oldSlot).equals(plugin.getItems().getSheepItem()) && Utils.getLocalArena(evt.getPlayer()) != null) {
                Utils.removeSheep(evt.getPlayer());
            }
        }
    }

    private Team getTeamByColor(DyeColor color) {
        for (Team team : Team.values()) {
            if (color.equals(team.getDyeColor())) {
                return team;
            }
        }
        return Team.NONE;
    }
}
