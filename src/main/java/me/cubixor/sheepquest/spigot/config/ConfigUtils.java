package me.cubixor.sheepquest.spigot.config;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import me.cubixor.sheepquest.spigot.mysql.MysqlConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfigUtils {

    public static boolean mysqlEnabled() {
        return SheepQuest.getInstance().getConfig().getBoolean("database.enabled-arenas");
    }

    public static Location stringToLocation(String locStr) {
        String[] split = locStr.split(";");

        String worldStr = split[0];
        String xStr = split[1];
        String yStr = split[2];
        String zStr = split[3];
        String yawStr = split[4];
        String pitchStr = split[5];

        World world = Bukkit.getWorld(worldStr);
        float x = Float.parseFloat(xStr);
        float y = Float.parseFloat(yStr);
        float z = Float.parseFloat(zStr);
        float yaw = Float.parseFloat(yawStr);
        float pitch = Float.parseFloat(pitchStr);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static String locationToString(Location loc) {
        String world = loc.getWorld().getName();
        String x = String.valueOf(loc.getX());
        String y = String.valueOf(loc.getY());
        String z = String.valueOf(loc.getZ());
        String yaw = String.valueOf(loc.getYaw());
        String pitch = String.valueOf(loc.getPitch());

        String[] locArray = new String[6];
        locArray[0] = world;
        locArray[1] = x;
        locArray[2] = y;
        locArray[3] = z;
        locArray[4] = yaw;
        locArray[5] = pitch;

        return String.join(";", locArray);
    }

    public static String joinLocations(Location loc1, Location loc2) {
        String loc1Str = locationToString(loc1);
        String loc2Str = locationToString(loc2);

        return String.join(":", loc1Str, loc2Str);
    }

    public static String[] splitLocations(String locString) {
        return locString.split(":");
    }

    public static String getString(String arena, ConfigField configField) {
        if (mysqlEnabled()) {
            try {
                ResultSet results = executeStatement(arena, configField);
                if (results == null) return null;
                if (results.next()) {
                    return results.getString(configField.getCode());
                }
                return null;
            } catch (SQLException e) {

                sendErrorMessage(e);
                return null;
            }

        } else {
            SheepQuest plugin = SheepQuest.getInstance();

            return plugin.getArenasConfig().getString("arenas." + arena + "." + configField.getCode());
        }
    }

    public static List<Team> getTeamList(String arena) {
        SheepQuest plugin = SheepQuest.getInstance();

        List<String> stringTeams = new ArrayList<>(plugin.getArenasConfig().getStringList("arenas." + arena + "." + ConfigField.TEAMS.getCode()));
        List<Team> teams = new ArrayList<>();
        for (Team t : Team.values()) {
            if (stringTeams.contains(t.getCode())) {
                teams.add(t);
            }
        }
        return teams;
    }

    public static List<String> getArenas() {
        SheepQuest plugin = SheepQuest.getInstance();

        if (mysqlEnabled()) {
            MysqlConnection mysqlConnection = plugin.getMysqlConnection();
            try {
                Connection connection = mysqlConnection.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("SELECT `name` FROM " + mysqlConnection.getDatabase() + "." + mysqlConnection.getTableArenas());

                ResultSet results = statement.executeQuery();

                List<String> arenas = new ArrayList<>();
                while (results.next()) {
                    arenas.add(results.getString("name"));
                }
                return arenas;
            } catch (SQLException e) {
                sendErrorMessage(e);
                return null;
            }
        } else {
            if (plugin.getArenasConfig().getConfigurationSection("arenas") != null) {
                return new ArrayList<>(plugin.getArenasConfig().getConfigurationSection("arenas").getKeys(false));
            } else {
                return new ArrayList<>();
            }
        }
    }

    public static boolean getBoolean(String arena, ConfigField configField) {
        if (mysqlEnabled()) {
            try {
                ResultSet results = executeStatement(arena, configField);
                if (results == null) return false;
                if (results.next()) {
                    return results.getBoolean(configField.getCode());
                }
                return false;
            } catch (SQLException e) {
                sendErrorMessage(e);
            }
            return false;

        } else {
            SheepQuest plugin = SheepQuest.getInstance();

            return plugin.getArenasConfig().getBoolean("arenas." + arena + "." + configField.getCode());
        }
    }


    public static Location getLocation(String arena, ConfigField configField) {
        SheepQuest plugin = SheepQuest.getInstance();
        String locString = plugin.getArenasConfig().getString("arenas." + arena + "." + configField.getCode());
        if (locString == null) {
            return null;
        }
        return stringToLocation(locString);
    }

    public static Location[] getArea(String arena, ConfigField configField) {
        SheepQuest plugin = SheepQuest.getInstance();
        String locString = plugin.getArenasConfig().getString("arenas." + arena + "." + configField.getCode());
        if (locString == null) return null;

        String[] splitLoc = splitLocations(locString);

        Location[] locations = new Location[2];
        locations[0] = stringToLocation(splitLoc[0]);
        locations[1] = stringToLocation(splitLoc[1]);

        return locations;
    }

    public static int getInt(String arena, ConfigField configField) {
        if (mysqlEnabled()) {
            try {
                ResultSet results = executeStatement(arena, configField);
                if (results == null) return 0;
                if (results.next()) {
                    return results.getInt(configField.getCode());
                }
                return 0;
            } catch (SQLException e) {
                sendErrorMessage(e);
                return 0;
            }

        } else {
            SheepQuest plugin = SheepQuest.getInstance();

            return plugin.getArenasConfig().getInt("arenas." + arena + "." + configField.getCode());
        }
    }

    private static ResultSet executeStatement(String arena, ConfigField configField) throws SQLException {
        SheepQuest plugin = SheepQuest.getInstance();
        MysqlConnection mysqlConnection = plugin.getMysqlConnection();

        Connection connection = mysqlConnection.getConnection();
        PreparedStatement statement = connection
                .prepareStatement("SELECT `" + configField.getCode() + "` FROM " +
                        mysqlConnection.getDatabase() + "." + mysqlConnection.getTableArenas() +
                        " WHERE `name`='" + arena + "'");

        ResultSet results = statement.executeQuery();
        if (Bukkit.isPrimaryThread() && plugin.isEnabled()) {
            try {
                throw new Exception("Synchronized database access");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    public static void insertArena(String arena) {
        SheepQuest plugin = SheepQuest.getInstance();

        if (mysqlEnabled()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    MysqlConnection mysqlConnection = plugin.getMysqlConnection();
                    Connection connection = mysqlConnection.getConnection();
                    PreparedStatement statement = connection
                            .prepareStatement("INSERT INTO " + mysqlConnection.getDatabase() + "." + mysqlConnection.getTableArenas() + " (`name`, `server`)" +
                                    " VALUES ('" + arena + "', '" + plugin.getServerName() + "')");
                    statement.executeUpdate();
                } catch (SQLException e) {
                    sendErrorMessage(e);
                }
            });
        } else {
            plugin.getArenasConfig().set("arenas." + arena + ".server", plugin.getServerName());
            plugin.getArenasConfig().set("arenas." + arena + ".active", false);
            plugin.getArenasConfig().set("arenas." + arena + ".vip", false);
            plugin.getArenasConfig().set("arenas." + arena + ".min-players", 0);
            plugin.getArenasConfig().set("arenas." + arena + ".max-players", 0);
            plugin.saveArenas();
        }
    }

    public static void removeArena(String arena) {
        SheepQuest plugin = SheepQuest.getInstance();

        if (mysqlEnabled()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    MysqlConnection mysqlConnection = plugin.getMysqlConnection();
                    Connection connection = mysqlConnection.getConnection();
                    PreparedStatement statement = connection
                            .prepareStatement("DELETE FROM " + mysqlConnection.getDatabase() + "." + mysqlConnection.getTableArenas() + " WHERE `name` = '" + arena + "'");

                    statement.executeUpdate();
                } catch (SQLException e) {
                    sendErrorMessage(e);
                }
            });
        }
        plugin.getArenasConfig().set("arenas." + arena, null);
        plugin.saveArenas();

    }

    public static void updateField(String arena, ConfigField configField, Object value) {
        SheepQuest plugin = SheepQuest.getInstance();

        if (mysqlEnabled() && configField.savedInDatabase()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    String str = value.toString();
                    if (value instanceof Boolean) {
                        boolean bool = (boolean) value;
                        if (bool) {
                            str = "1";
                        } else {
                            str = "0";
                        }
                    }
                    MysqlConnection mysqlConnection = plugin.getMysqlConnection();
                    Connection connection = mysqlConnection.getConnection();
                    PreparedStatement statement = connection
                            .prepareStatement("UPDATE " + mysqlConnection.getDatabase() + "." + mysqlConnection.getTableArenas() + " SET `" +
                                    configField.getCode() + "` = '" + str + "' WHERE `name` = '" + arena + "'");

                    statement.executeUpdate();
                } catch (SQLException e) {
                    sendErrorMessage(e);
                }
            });
        } else {
            plugin.getArenasConfig().set("arenas." + arena + "." + configField.getCode(), value);
            plugin.saveArenas();
        }
    }

    private static void sendErrorMessage(SQLException e) {
        SheepQuest plugin = SheepQuest.getInstance();

        plugin.getLogger().severe(ChatColor.RED + "Lost connection with mysql server. Restart spigot server to reconnect.");
        e.printStackTrace();
    }
}