package me.cubixor.sheepquest.spigot.game;

import com.cryptomorin.xseries.ReflectionUtils;
import com.google.common.collect.Sets;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.api.VersionUtils;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class Pathfinding {

    public static Object getEntityInsentient(Entity entity) {
        try {
            Class<?> craftLivingEntityClass = ReflectionUtils.getCraftClass("entity.CraftLivingEntity");
            Object craftLivingEntity = craftLivingEntityClass.cast(entity);
            Object entityLiving = craftLivingEntityClass.getMethod("getHandle").invoke(craftLivingEntity);
            return ReflectionUtils.getNMSClass("world.entity", "EntityInsentient").cast(entityLiving);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearGoals(Object entityInsentient) {
        try {

            Class<?> pathfinderGoalSelectorClass = ReflectionUtils.getNMSClass("world.entity.ai.goal", "PathfinderGoalSelector");
            Class<?> entityInsentientClass = ReflectionUtils.getNMSClass("world.entity", "EntityInsentient");
            Field f3 = pathfinderGoalSelectorClass.getDeclaredField("d");
            f3.setAccessible(true);

            if (!VersionUtils.isBefore120()) {
                f3.set(entityInsentientClass.getField("bO").get(entityInsentient), Sets.newLinkedHashSet());
            } else if (!VersionUtils.isBefore19()) {
                f3.set(entityInsentientClass.getField("bN").get(entityInsentient), Sets.newLinkedHashSet());
            } else if (!VersionUtils.isBefore18()) {
                f3.set(entityInsentientClass.getField("bR").get(entityInsentient), Sets.newLinkedHashSet());
            } else {
                if (VersionUtils.isBefore17()) {
                    Field f1 = pathfinderGoalSelectorClass.getDeclaredField("b");
                    Field f2 = pathfinderGoalSelectorClass.getDeclaredField("c");
                    f1.setAccessible(true);
                    f2.setAccessible(true);


                    if (VersionUtils.is1_8()) {
                        Class<?> unsafeListClass = ReflectionUtils.getCraftClass("util.UnsafeList");

                        Method m = unsafeListClass.getMethod("clear");
                        m.invoke(f1.get(entityInsentientClass.getField("goalSelector").get(entityInsentient)));
                        m.invoke(f2.get(entityInsentientClass.getField("goalSelector").get(entityInsentient)));
                        m.invoke(f2.get(entityInsentientClass.getField("targetSelector").get(entityInsentient)));
                        m.invoke(f1.get(entityInsentientClass.getField("targetSelector").get(entityInsentient)));
                    } else if (VersionUtils.is1416()) {
                        f3.set(entityInsentientClass.getField("goalSelector").get(entityInsentient), Sets.newLinkedHashSet());
                        f3.set(entityInsentientClass.getField("targetSelector").get(entityInsentient), Sets.newLinkedHashSet());
                    } else {
                        f1.set(entityInsentientClass.getField("goalSelector").get(entityInsentient), Sets.newLinkedHashSet());
                        f2.set(entityInsentientClass.getField("targetSelector").get(entityInsentient), Sets.newLinkedHashSet());
                    }
                } else {
                    f3.set(entityInsentientClass.getField("bP").get(entityInsentient), Sets.newLinkedHashSet());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void changeFollowRange(Object entityInsentient) {
        try {
            Field f;
            if (VersionUtils.isBefore17()) {
                f = ReflectionUtils.getNMSClass("GenericAttributes").getField("FOLLOW_RANGE");
            } else {
                f = ReflectionUtils.getNMSClass("world.entity.ai.attributes", "GenericAttributes").getField("b");
            }
            Class<?> c = VersionUtils.isBefore16() ? ReflectionUtils.getNMSClass("IAttribute") : ReflectionUtils.getNMSClass("world.entity.ai.attributes", "AttributeBase");
            Method m = VersionUtils.isBefore18() ? ReflectionUtils.getNMSClass("world.entity", "EntityInsentient").getMethod("getAttributeInstance", c) : ReflectionUtils.getNMSClass("world.entity", "EntityInsentient").getMethod("a", c);
            Object ai = m.invoke(entityInsentient, f.get(null));
            Method m2 = VersionUtils.isBefore18() ? ReflectionUtils.getNMSClass("world.entity.ai.attributes", "AttributeModifiable").getMethod("setValue", double.class) : ReflectionUtils.getNMSClass("world.entity.ai.attributes", "AttributeModifiable").getMethod("a", double.class);
            m2.invoke(ai, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addWalkToLocationGoal(Object entityInsentient, Location loc, double speed) {
        try {
            Class<?> entityInsentientClass = ReflectionUtils.getNMSClass("world.entity", "EntityInsentient");
            String navigation;
            if (VersionUtils.isBefore18()) {
                navigation = "getNavigation";
            } else if (VersionUtils.isBefore193()) {
                navigation = "D";
            } else if (VersionUtils.isBefore120()) {
                navigation = "G";
            } else {
                navigation = "J";
            }

            Method getNavigation = entityInsentientClass.getMethod(navigation);
            Object a = getNavigation.invoke(entityInsentient);
            Method method = ReflectionUtils.getNMSClass("world.entity.ai.navigation", "NavigationAbstract")
                    .getMethod("a", double.class, double.class, double.class, double.class);
            method.invoke(a, loc.getX(), loc.getY(), loc.getZ(), speed);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addOtherGoals(Object entityInsentient) {
        try {
            Pathfinding.clearGoals(entityInsentient);

            Class<?> entityInsentientClass = ReflectionUtils.getNMSClass("world.entity", "EntityInsentient");
            Class<?> entityCreatureClass = ReflectionUtils.getNMSClass("world.entity", "EntityCreature");
            Field f;
            if (!VersionUtils.isBefore120()) {
                f = entityInsentientClass.getField("bO");
            } else if (!VersionUtils.isBefore19()) {
                f = entityInsentientClass.getField("bN");
            } else if (!VersionUtils.isBefore18()) {
                f = entityInsentientClass.getField("bR");
            } else if (!VersionUtils.isBefore17()) {
                f = entityInsentientClass.getField("bP");
            } else {
                f = entityInsentientClass.getField("goalSelector");
            }
            Object a = f.get(entityInsentient);
            Object goal1 = ReflectionUtils.getNMSClass("world.entity.ai.goal", "PathfinderGoalRandomLookaround").getConstructor(entityInsentientClass).newInstance(entityInsentient);
            Object goal2 = ReflectionUtils.getNMSClass("world.entity.ai.goal", "PathfinderGoalRandomStroll").getConstructor(entityCreatureClass, double.class).newInstance(entityInsentient, 1);
            Object goal3 = ReflectionUtils.getNMSClass("world.entity.ai.goal", "PathfinderGoalFloat").getConstructor(entityInsentientClass).newInstance(entityInsentient);
            Method method = ReflectionUtils.getNMSClass("world.entity.ai.goal", "PathfinderGoalSelector")
                    .getMethod("a", int.class, ReflectionUtils.getNMSClass("world.entity.ai.goal", "PathfinderGoal"));
            method.invoke(a, 0, goal3);
            method.invoke(a, 1, goal2);
            method.invoke(a, 2, goal1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Location getMiddleArea(String arena, Team team) {
        Location[] loc = ConfigUtils.getArea(arena, team);
        double x = (loc[0].getX() + loc[1].getX()) / 2;
        double y = (loc[0].getY() + loc[1].getY()) / 2;
        double z = (loc[0].getZ() + loc[1].getZ()) / 2;

        return new Location(loc[0].getWorld(), x, y, z);
    }

    public static void walkToLocation(LivingEntity entity, Location location, double speed, LocalArena localArena, Team team) {
        if (localArena.getSheep().containsKey(entity)) {
            localArena.getSheep().get(entity).cancel();
        }
        Object e = Pathfinding.getEntityInsentient(entity);
        Pathfinding.clearGoals(e);
        Pathfinding.changeFollowRange(e);
        Pathfinding.addOtherGoals(e);

        localArena.getSheep().put(entity, new BukkitRunnable() {
            boolean wasInRegion = true;

            public void run() {
                if (!entity.isOnGround()) {
                    return;
                }
                boolean inRegion = Utils.isInRegion(entity, localArena, team);
                if (!inRegion && wasInRegion) {
                    Pathfinding.clearGoals(e);
                    Pathfinding.addWalkToLocationGoal(e, location, speed);
                    wasInRegion = false;
                } else if (inRegion && !wasInRegion) {
                    Pathfinding.addOtherGoals(e);
                    wasInRegion = true;
                }
            }
        }.runTaskTimer(SheepQuest.getInstance(), 0, 10));
    }

/*
    public static float[] getRotations(Location one, Location two) {
        double diffX = two.getX() - one.getX();
        double diffZ = two.getZ() - one.getZ();
        double diffY = two.getY() + 2.0 - 0.4 - (one.getY() + 2.0);
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (Math.atan2(diffZ, diffX) * 180.0 / 3.141592653589793) - 90.0f;
        float pitch = (float) (-Math.atan2(diffY, dist) * 180.0 / 3.141592653589793);
        return new float[]{yaw, pitch};
    }

    private static void applyMovement(Entity entity, Location location, double speed) {
        float yaw = getRotations(entity.getLocation(), location)[0];

        Vector direction = new Vector(-Math.sin(yaw * 3.1415927F / 180.0F) * (float) 1 * 0.5F, 0, Math.cos(yaw * 3.1415927F / 180.0F) * (float) 1 * 0.5F).multiply(speed);

        if (entity.getLocation().getY() - location.getY() > 0 && entity.isOnGround()) {
            direction.setY(Math.min(0.42, entity.getLocation().getY() - location.getY()));
        }
        if (entity.isOnGround() && (
                entity.getWorld().getBlockAt(entity.getLocation().add(1, 0, 0)).getType().isSolid()) ||
                entity.getWorld().getBlockAt(entity.getLocation().add(-1, 0, 0)).getType().isSolid() ||
                entity.getWorld().getBlockAt(entity.getLocation().add(0, 0, 1)).getType().isSolid() ||
                entity.getWorld().getBlockAt(entity.getLocation().add(0, 0, -1)).getType().isSolid()) {
            direction.setY(0.5);
        }
        if (entity.isOnGround()) {
            entity.setVelocity(direction);
        }
    }
*/
}
