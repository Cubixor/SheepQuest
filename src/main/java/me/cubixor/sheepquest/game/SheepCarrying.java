package me.cubixor.sheepquest.game;

import me.cubixor.sheepquest.*;
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

    public SheepCarrying(SheepQuest s) {
        plugin = s;
    }


    @EventHandler
    public void onMove(PlayerMoveEvent evt) {
        Utils utils = new Utils(plugin);
        Player player = evt.getPlayer();
        Arena arena = utils.getArena(player);
        if (arena == null) {
            return;
        }
        if (!arena.state.equals(GameState.GAME)) {
            return;
        }

        if(!(arena.playerStats.get(player).sheepCooldown == null || arena.playerStats.get(player).sheepCooldown.isCancelled())){
            return;
        }

        Team team = arena.playerTeam.get(player);

        for (Entity e : evt.getPlayer().getNearbyEntities(1, 1, 1)) {
            if (e.getType().equals(EntityType.SHEEP) && !evt.getPlayer().getPassengers().contains(e) && evt.getPlayer().getInventory().getItemInMainHand().equals(plugin.items.sheepItem) && !arena.respawnTimer.containsKey(evt.getPlayer())) {
                Sheep sheep = (Sheep) e;
                if (sheep.getColor().equals(DyeColor.WHITE) || !team.equals(getTeamByColor(sheep.getColor()))) {
                    int pas = evt.getPlayer().getPassengers().size();
                    if (pas == 0) {
                        evt.getPlayer().addPassenger(e);
                    } else if (pas == 1) {
                        Entity pas1 = evt.getPlayer().getPassengers().get(0);
                        if (pas1.getPassengers().size() == 0) {
                            pas1.addPassenger(e);
                            player.removePotionEffect(PotionEffectType.SLOW);
                            evt.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 1, false, false, false));

                        } else if (pas1.getPassengers().size() == 1 && !pas1.getPassengers().contains(e)) {
                            Entity pas2 = pas1.getPassengers().get(0);
                            if (pas2.getPassengers().size() == 0) {
                                pas2.addPassenger(e);
                                player.removePotionEffect(PotionEffectType.SLOW);
                                evt.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 9999999, 2, false, false, false));
                            }
                        }
                    }
                }
            }
        }


        if (!player.getPassengers().isEmpty()) {
            if (utils.isInRegion(player, utils.getArenaString(arena), team)) {
                regionEnter(player);
            }
        }
    }

    private void regionEnter(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Utils utils = new Utils(plugin);
                if (utils.isInRegion(player, utils.getArenaString(utils.getArena(player)), utils.getArena(player).playerTeam.get(player))) {
                    if (player.getPassengers().size() != 0) {
                        if (player.getPassengers().get(0).getPassengers().size() != 0) {
                            if (player.getPassengers().get(0).getPassengers().get(0).getPassengers().size() != 0) {

                                addPoint(player, (Sheep) player.getPassengers().get(0).getPassengers().get(0).getPassengers().get(0));

                                player.getPassengers().get(0).getPassengers().get(0).removePassenger(player.getPassengers().get(0).getPassengers().get(0).getPassengers().get(0));

                            }
                            addPoint(player, (Sheep) player.getPassengers().get(0).getPassengers().get(0));

                            player.getPassengers().get(0).removePassenger(player.getPassengers().get(0).getPassengers().get(0));

                        }

                        addPoint(player, (Sheep) player.getPassengers().get(0));

                        player.removePassenger(player.getPassengers().get(0));
                    }
                }

            }
        }.runTaskLater(plugin, 5);
    }

    private void addPoint(Player player, Sheep sheep) {
        Utils utils = new Utils(plugin);

        Arena arena = utils.getArena(player);
        Team team = arena.playerTeam.get(player);

        arena.playerStats.get(player).sheepTaken++;

        Location path = (Location) plugin.getArenasConfig().get("Arenas." + utils.getArenaString(arena) + ".teams." + utils.getTeamString(team) + "-spawn");
        new PathFinding(plugin).walkToLocation(sheep, path, 0.7, arena, team);

        Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fwm = firework.getFireworkMeta();
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        Color color = utils.getColor(team);
        FireworkEffect fwe = FireworkEffect.builder().flicker(true).withColor(color).with(type).withTrail().build();
        fwm.addEffect(fwe);
        fwm.setPower(1);
        firework.setFireworkMeta(fwm);

        player.removePotionEffect(PotionEffectType.SLOW);

        for (Team t : Team.values()) {
            if (!t.equals(Team.NONE)) {
                if (sheep.getColor().equals(utils.getDyeColor(t)) && !team.equals(t)) {
                    arena.points.replace(t, arena.points.get(t) - 1);
                }

            }
        }

        sheep.setColor(utils.getDyeColor(team));


        arena.points.replace(team, arena.points.get(team) + 1);
    }


    @EventHandler
    public void onItemChange(PlayerItemHeldEvent evt) {
        Utils utils = new Utils(plugin);
        int oldSlot = evt.getPreviousSlot();
        if (evt.getPlayer().getInventory().getItem(oldSlot) != null) {
            if (evt.getPlayer().getInventory().getItem(oldSlot).equals(plugin.items.sheepItem) && utils.getArena(evt.getPlayer()) != null) {
                utils.removeSheep(evt.getPlayer());
            }
        }
    }

    private Team getTeamByColor(DyeColor color) {
        Team team = null;
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
