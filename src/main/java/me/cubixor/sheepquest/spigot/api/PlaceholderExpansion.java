package me.cubixor.sheepquest.spigot.api;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.config.StatsField;
import me.cubixor.sheepquest.spigot.config.StatsUtils;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {

    private final SheepQuest plugin;

    public PlaceholderExpansion() {
        plugin = SheepQuest.getInstance();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "sheepquest";
    }

    @Override
    public @NotNull String getAuthor() {
        return SheepQuest.getInstance().getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return SheepQuest.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        String[] paramsSplit = params.split("_");

        if (paramsSplit.length != 2) {
            return null;
        }
        String param1 = paramsSplit[1];
        if (param1.equalsIgnoreCase("{player-name}")) {
            param1 = player.getName();
        }

        switch (paramsSplit[0]) {
            case "status": {
                Arena arena = plugin.getArena(param1);
                return getState(arena);
            }
            case "players": {
                Arena arena = plugin.getArena(param1);
                if (arena == null) {
                    return "0";
                }
                return Integer.toString(arena.getPlayers().size());
            }
            case "wins":
                return Integer.toString(StatsUtils.getStats(param1, StatsField.WINS));
            case "looses":
                return Integer.toString(StatsUtils.getStats(param1, StatsField.LOOSES));
            case "games":
                return Integer.toString(StatsUtils.getStats(param1, StatsField.GAMES_PLAYED));
            case "kills":
                return Integer.toString(StatsUtils.getStats(param1, StatsField.KILLS));
            case "deaths":
                return Integer.toString(StatsUtils.getStats(param1, StatsField.DEATHS));
            case "sheep":
                return Integer.toString(StatsUtils.getStats(param1, StatsField.SHEEP_TAKEN));
            case "bonus-sheep":
                return Integer.toString(StatsUtils.getStats(param1, StatsField.BONUS_SHEEP_TAKEN));
            case "playtime":
                return new StatsUtils().convertPlaytime(StatsUtils.getStats(param1, StatsField.PLAYTIME));
            case "pos":
                if (!plugin.getRanking().containsKey(param1)) {
                    return "-";
                }
                return Integer.toString(new ArrayList<>(plugin.getRanking().keySet()).indexOf(param1) + 1);
            case "top":
                String topPlayer = getPlayerAtPlace(param1);
                return topPlayer == null ? plugin.getMessage("general.no-one") : topPlayer;
            case "topwins":
                String topWinsPlayer = getPlayerAtPlace(param1);
                return topWinsPlayer == null ? "0" : Integer.toString(plugin.getRanking().get(topWinsPlayer));
            case "team":
                Player p = Bukkit.getPlayerExact(param1);
                if (p != null) {
                    LocalArena arena = Utils.getLocalArena(p);
                    if (arena != null) {
                        Team team = arena.getPlayerTeam().get(p);
                        if (team != null) {
                            return team.getName();
                        }
                    }
                }
                return "";
        }

        return null;
    }

    private String getPlayerAtPlace(String param1) {
        int place = Integer.parseInt(param1);
        if (plugin.getRanking().size() < place || place < 1) {
            return null;
        }
        return (String) plugin.getRanking().keySet().toArray()[place - 1];
    }

    private String getState(Arena arena) {
        SheepQuest plugin = SheepQuest.getInstance();
        String gameState = null;
        if (arena == null) {
            gameState = plugin.getMessage("general.state-offline");
        } else if (arena.getState().equals(GameState.WAITING)) {
            gameState = plugin.getMessage("general.state-waiting");
        } else if (arena.getState().equals(GameState.STARTING)) {
            gameState = plugin.getMessage("general.state-starting");
        } else if (arena.getState().equals(GameState.GAME)) {
            gameState = plugin.getMessage("general.state-game");
        } else if (arena.getState().equals(GameState.ENDING)) {
            gameState = plugin.getMessage("general.state-ending");
        }
        return gameState;

    }
}
