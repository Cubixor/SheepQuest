package me.cubixor.sheepquest.api;

import me.cubixor.sheepquest.SheepQuest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySql {
    //TODO MySql
    private Connection connection;
    private String host, database, username, password;
    private int port;
    private SheepQuest plugin;

    public void setupDatabase() {
        plugin = SheepQuest.getInstance();
        host = plugin.getConfig().getString("database.host");
        port = plugin.getConfig().getInt("database.port");
        database = plugin.getConfig().getString("database.database");
        username = plugin.getConfig().getString("database.username");
        password = plugin.getConfig().getString("database.password");
        try {
            openConnection();
            Statement statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.username, this.password);
        }
    }


}
