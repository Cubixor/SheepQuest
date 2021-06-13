package me.cubixor.sheepquest.spigot.mysql;

import java.sql.Connection;

public class MysqlConnection {

    private Connection connection;
    private String host, database, port, username, password, tableStats, tableArenas;

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getTableStats() {
        return tableStats;
    }

    public void setTableStats(String tableStats) {
        this.tableStats = tableStats;
    }

    public String getTableArenas() {
        return tableArenas;
    }

    public void setTableArenas(String tableArenas) {
        this.tableArenas = tableArenas;
    }
}
