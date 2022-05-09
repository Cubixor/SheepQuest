package me.cubixor.sheepquest.spigot.gameInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Arena implements Serializable {

    private final String name;
    private final String server;
    private GameState state = GameState.WAITING;
    private List<String> players = new ArrayList<>();

    public Arena(String name, String server) {
        this.name = name;
        this.server = server;
    }

    public Arena(String name, String server, GameState state, List<String> players) {
        this.name = name;
        this.server = server;
        this.state = state;
        this.players = players;
    }

    public String getName() {
        return name;
    }

    public String getServer() {
        return server;
    }

    public GameState getState() {
        return state;
    }

    public void setGameState(GameState state) {
        this.state = state;
    }

    public List<String> getPlayers() {
        return players;
    }

    @Override
    public String toString() {
        return "Arena{" +
                "name='" + name + '\'' +
                ", server='" + server + '\'' +
                ", state=" + state +
                ", players=" + players +
                '}';
    }
}
