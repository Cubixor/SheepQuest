package me.cubixor.sheepquest.spigot.mysql;

import me.cubixor.sheepquest.spigot.SheepQuest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectionSetup {

    private final SheepQuest plugin;

    public ConnectionSetup() {
        plugin = SheepQuest.getInstance();
    }


    public boolean connectToDatabase(MysqlConnection mysqlConnection) {
        try {
            Connection connection = mysqlConnection.getConnection();

            if (plugin.getMysqlConnection() != null && connection != null && !connection.isClosed()) {
                connection.close();
            }

            synchronized (this) {
/*
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                    } catch (ClassNotFoundException e2) {
                        e2.printStackTrace();
                    }
                }
*/
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                mysqlConnection.setConnection(DriverManager.getConnection("jdbc:mysql://" + mysqlConnection.getHost() + ":" + mysqlConnection.getPort() + "/" +
                        mysqlConnection.getDatabase() + "?autoReconnect=true&useSSL=false", mysqlConnection.getUsername(), mysqlConnection.getPassword()));

                //if (plugin.getMysqlConnection() == null) {
                plugin.setMysqlConnection(mysqlConnection);
                //}

                if (plugin.getConfig().getBoolean("database.enabled-stats")) {
                    PreparedStatement statement1 = mysqlConnection.getConnection()
                            .prepareStatement("CREATE DATABASE IF NOT EXISTS " + mysqlConnection.getDatabase());
                    statement1.executeUpdate();
                    PreparedStatement statement2 = mysqlConnection.getConnection()
                            .prepareStatement("CREATE TABLE IF NOT EXISTS " + mysqlConnection.getDatabase() + "." + mysqlConnection.getTableStats() +
                                    "( `player` VARCHAR(14) NOT NULL ,  " +
                                    "`kills` INT NOT NULL DEFAULT '0' ,  " +
                                    "`deaths` INT NOT NULL DEFAULT '0' ,  " +
                                    "`wins` INT NOT NULL DEFAULT '0' ,  " +
                                    "`looses` INT NOT NULL DEFAULT '0' ,  " +
                                    "`games-played` INT NOT NULL DEFAULT '0' ,  " +
                                    "`sheep-taken` INT NOT NULL DEFAULT '0' ,  " +
                                    "`bonus-sheep-taken` INT NOT NULL DEFAULT '0' , " +
                                    "`playtime` INT NOT NULL DEFAULT '0' , " +
                                    "PRIMARY KEY  (`player`)) ENGINE = InnoDB;");
                    statement2.executeUpdate();
                }
                if (plugin.getConfig().getBoolean("database.enabled-arenas")) {
                    PreparedStatement statement1 = mysqlConnection.getConnection()
                            .prepareStatement("CREATE DATABASE IF NOT EXISTS " + mysqlConnection.getDatabase());
                    statement1.executeUpdate();
                    PreparedStatement statement2 = mysqlConnection.getConnection()
                            .prepareStatement("CREATE TABLE IF NOT EXISTS `" + mysqlConnection.getDatabase() + "`.`" + mysqlConnection.getTableArenas() + "` ( `name` VARCHAR(14)  , `server` TEXT NULL DEFAULT NULL , `active` BOOLEAN NULL DEFAULT '0' , `vip` BOOLEAN NULL DEFAULT '0' , `min-players` INT NULL DEFAULT '0' ,  `max-players` INT NULL DEFAULT '0' ,    PRIMARY KEY  (`name`(14))) ENGINE = InnoDB;");
                    statement2.executeUpdate();

                }
                pingDatabase(plugin.getConfig().getInt("database.ping-period"));
                plugin.getLogger().info(ChatColor.GREEN + "Successfully connected to a mysql server!");
                return true;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe(ChatColor.RED + "Plugin couldn't connect to mysql database using provided login data. Check if they are correct and if mysql server is working properly and then restart the spigot server.");
            e.printStackTrace();
            return false;
        }
    }

    public MysqlConnection mysqlSetup() {
        MysqlConnection mysqlConnection = new MysqlConnection();
        mysqlConnection.setHost(plugin.getConfig().getString("database.host"));
        mysqlConnection.setPort(plugin.getConfig().getString("database.port"));
        mysqlConnection.setDatabase(plugin.getConfig().getString("database.database"));
        mysqlConnection.setUsername(plugin.getConfig().getString("database.username"));
        mysqlConnection.setPassword(plugin.getConfig().getString("database.password"));
        mysqlConnection.setTableStats(plugin.getConfig().getString("database.table-stats"));
        mysqlConnection.setTableArenas(plugin.getConfig().getString("database.table-arenas"));
        return mysqlConnection;
    }

    public void pingDatabase(int period) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (!plugin.isEnabled() || plugin.getMysqlConnection().getConnection().isClosed()) {
                        this.cancel();
                        return;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    this.cancel();
                    return;
                }

                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    try {
                        MysqlConnection mysqlConnection = plugin.getMysqlConnection();
                        Connection connection = mysqlConnection.getConnection();
                        PreparedStatement statement;
                        statement = connection.prepareStatement("SELECT 1");
                        statement.executeQuery();
                    } catch (Exception e) {
                        e.printStackTrace();
                        this.cancel();
                    }
                });
            }
        }.runTaskTimer(plugin, 0, period);
    }

}
