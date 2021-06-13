package me.cubixor.sheepquest.spigot.config;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.mysql.MysqlConnection;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatsUtils {

    private final SheepQuest plugin;
    private final MysqlConnection mysqlConnection;

    public StatsUtils() {
        plugin = SheepQuest.getInstance();
        mysqlConnection = plugin.getMysqlConnection();
    }

    public static boolean mysqlEnabled() {
        return SheepQuest.getInstance().getConfig().getBoolean("database.enabled-stats");
    }

    public static int getStats(String player, StatsField statsField) {
        SheepQuest plugin = SheepQuest.getInstance();
        if (plugin.getPlayerInfo().get(Bukkit.getPlayerExact(player)) != null) {
            return plugin.getPlayerInfo().get(Bukkit.getPlayerExact(player)).getStats().get(statsField);
        } else {
            return 0;
        }
    }

    public static int getSavedStats(String player, StatsField statsField) {
        SheepQuest plugin = SheepQuest.getInstance();
        if (mysqlEnabled()) {
            return new StatsUtils().getSqlStats(player, statsField);
        } else {
            return plugin.getStats().getInt("players." + player + "." + statsField.getCode());
        }
    }

    public static void addStats(String player, StatsField statsField, int count) {
        SheepQuest plugin = SheepQuest.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int statsCount = plugin.getStats().getInt("players." + player + "." + statsField.getCode()) + count;
            if (mysqlEnabled()) {
                new StatsUtils().updateSqlStats(player, statsField, count);
            } else {
                plugin.getStats().set("players." + player + "." + statsField.getCode(), statsCount);
            }

            if (plugin.getPlayerInfo().get(Bukkit.getPlayerExact(player)) != null) {
                plugin.getPlayerInfo().get(Bukkit.getPlayerExact(player)).getStats().replace(statsField, statsCount);
            }
        });
    }

    public static List<String> getPlayers() {
        SheepQuest plugin = SheepQuest.getInstance();
        if (mysqlEnabled()) {
            return new StatsUtils().getSqlPlayers();
        } else {
            return new ArrayList<>(plugin.getStats().getConfigurationSection("players").getKeys(false));
        }
    }

    private List<String> getSqlPlayers() {
        try {
            Connection connection = mysqlConnection.getConnection();
            PreparedStatement statement = connection
                    .prepareStatement("SELECT player FROM " + mysqlConnection.getDatabase() + "." + mysqlConnection.getTableStats());

            ResultSet results = statement.executeQuery();

            List<String> players = new ArrayList<>();
            while (results.next()) {
                players.add(results.getString("player"));
            }
            if (Bukkit.isPrimaryThread()) {
                try {
                    throw new Exception("Synchronized database access");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return players;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateSqlStats(String player, StatsField statsField, int count) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                Connection connection = mysqlConnection.getConnection();
                PreparedStatement statement = connection
                        .prepareStatement("INSERT INTO " + mysqlConnection.getDatabase() + "." + mysqlConnection.getTableStats() + " (player, `" + statsField.getCode() + "`)" +
                                " VALUES('" + player + "', " + count + ") ON DUPLICATE KEY UPDATE `" + statsField.getCode() + "`=`" + statsField.getCode() + "`+" + count);
                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private int getSqlStats(String player, StatsField statsField) {
        try {
            Connection connection = mysqlConnection.getConnection();
            PreparedStatement statement = connection
                    .prepareStatement("SELECT `" + statsField.getCode() + "` FROM " + mysqlConnection.getDatabase() + "." + mysqlConnection.getTableStats() + " WHERE player='" + player + "'");

            ResultSet results = statement.executeQuery();
            if (Bukkit.isPrimaryThread()) {
                try {
                    throw new Exception("Synchronized database access");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (results.next()) {
                return results.getInt(statsField.getCode());
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String convertPlaytime(int playtime) {
        int hours;
        int minutes;
        int seconds;
        String h = plugin.getMessage("other.stats-playtime-hours");
        String min = plugin.getMessage("other.stats-playtime-minutes");
        String sec = plugin.getMessage("other.stats-playtime-seconds");
        String timeString;
        if (playtime < 60) {
            seconds = playtime;
            timeString = seconds + sec;
        } else if (playtime < 3600) {
            minutes = playtime / 60;
            seconds = playtime % 60;
            timeString = minutes + min + " " + seconds + sec;
        } else {
            hours = playtime / 3600;
            minutes = (playtime % 3600) / 60;
            seconds = playtime % 60;
            timeString = hours + h + " " + minutes + min + " " + seconds + sec;
        }

        return timeString;
    }

}

