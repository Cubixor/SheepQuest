package me.cubixor.sheepquest.game;

import com.cryptomorin.xseries.XSound;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.PassengerFixReflection;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.game.events.BonusSheep;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.GameState;
import me.cubixor.sheepquest.gameInfo.Team;
import org.bukkit.*;
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
        Arena arena = Utils.getArena(player);
        if (arena == null) {
            return;
        }
        if (!arena.getState().equals(GameState.GAME)) {
            return;
        }

        if (arena.getPlayerStats().get(player).getSheepCooldown() != null) {
            return;
        }

        Team team = arena.getPlayers().get(player);

        for (Entity e : evt.getPlayer().getNearbyEntities(1, 1, 1)) {
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


            if (e.getType().equals(EntityType.SHEEP) & evt.getPlayer().getInventory().getItemInMainHand().equals(plugin.getItems().getSheepItem()) && !arena.getRespawnTimer().containsKey(evt.getPlayer())) {
                Sheep sheep = (Sheep) e;
                if (sheep.getColor().equals(DyeColor.WHITE) || !team.equals(getTeamByColor(sheep.getColor()))) {
                    BonusSheep bonusSheep = new BonusSheep();
                    if (bonusSheep.pickupSheep(player, sheep)) {
                        continue;
                    }

                    if (evt.getPlayer().getPassenger() == null) {
                        player.setPassenger(e);
                        if (player.getPassenger() != null) {
                            player.playSound(player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.sheep-pick")).get().parseSound(), 100, 1);
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
                            }
                            player.removePotionEffect(PotionEffectType.SLOW);
                            evt.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 1, false, false));

                        } else if (pas1.getPassenger() != null && !pas1.getPassenger().equals(e)) {
                            Entity pas2 = pas1.getPassenger();
                            if (pas2.getPassenger() == null) {
                                pas2.setPassenger(e);
                                if (pas2.getPassenger() != null) {
                                    player.playSound(player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.sheep-pick")).get().parseSound(), 100, 1);
                                }
                                player.removePotionEffect(PotionEffectType.SLOW);
                                evt.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 2, false, false));
                            }
                        }
                    }
                    if (plugin.isPassengerFix()) {
                        new PassengerFixReflection().updatePassengers(player);
                    }
                }
            }
        }


        if (player.getPassenger() != null) {
            if (Utils.isInRegion(player, Utils.getArenaString(arena), team)) {
                regionEnter(player);
            }
        }
    }

    private void regionEnter(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Arena arena = Utils.getArena(player);
                if (arena == null) {
                    return;
                }
                if (Utils.isInRegion(player, Utils.getArenaString(Utils.getArena(player)), arena.getPlayers().get(player))) {
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


                        if (plugin.isPassengerFix()) {
                            new PassengerFixReflection().updatePassengers(player);
                        }
                    }

                }

            }
        }.runTaskLater(plugin, 10);
    }

    private void sheepBring(Player player, Sheep sheep) {
        Arena arena = Utils.getArena(player);
        Team team = arena.getPlayers().get(player);
        boolean specialSheep = sheep.getColor().equals(DyeColor.valueOf(plugin.getConfig().getString("special-events.bonus-sheep.color")));

        arena.getPlayerStats().get(player).setSheepTaken(arena.getPlayerStats().get(player).getSheepTaken() + 1);
        arena.getPlayerStats().get(player).setBonusSheepTaken(arena.getPlayerStats().get(player).getBonusSheepTaken() + 1);
        player.removePotionEffect(PotionEffectType.SLOW);

        Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = firework.getFireworkMeta();
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        Color color = specialSheep ? DyeColor.valueOf(plugin.getConfig().getString("special-events.bonus-sheep.color")).getColor() : Utils.getColor(team);
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
        Arena arena = Utils.getArena(player);
        Team team = arena.getPlayers().get(player);

        Location path = (Location) plugin.getArenasConfig().get("Arenas." + Utils.getArenaString(arena) + ".teams." + Utils.getTeamString(team) + "-spawn");
        new PathFinding().walkToLocation(sheep, path, plugin.getConfig().getDouble("sheep-speed"), arena, team);

        for (Team t : Utils.getTeams()) {
            if (sheep.getColor().equals(Utils.getDyeColor(t)) && !team.equals(t)) {
                arena.getPoints().replace(t, arena.getPoints().get(t) - 1);
            }
        }
        arena.getPoints().replace(team, arena.getPoints().get(team) + 1);

        sheep.setColor(Utils.getDyeColor(team));

        Utils.playSound(arena, player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.sheep-bring")).get().parseSound(), 1, 1);
        player.getWorld().spawnParticle(Particle.valueOf(plugin.getConfig().getString("particles.sheep-bring")), player.getLocation().getX(), player.getLocation().getY() + 1.5, player.getLocation().getZ(), 50, 1, 1, 1, 0.1);
    }

    @EventHandler
    public void onItemChange(PlayerItemHeldEvent evt) {
        int oldSlot = evt.getPreviousSlot();
        if (evt.getPlayer().getInventory().getItem(oldSlot) != null) {
            if (evt.getPlayer().getInventory().getItem(oldSlot).equals(plugin.getItems().getSheepItem()) && Utils.getArena(evt.getPlayer()) != null) {
                Utils.removeSheep(evt.getPlayer());
            }
        }
    }

    private Team getTeamByColor(DyeColor color) {
        Team team = Team.NONE;
        if (color.equals(DyeColor.RED)) {
            team = Team.RED;
        } else if (color.equals(DyeColor.LIME)) {
            team = Team.GREEN;
        } else if (color.equals(DyeColor.BLUE)) {
            team = Team.BLUE;
        } else if (color.equals(DyeColor.YELLOW)) {
            team = Team.YELLOW;
        }
        return team;
    }
}
