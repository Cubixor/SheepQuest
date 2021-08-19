package me.cubixor.sheepquest.spigot.api;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.config.StatsField;
import me.cubixor.sheepquest.spigot.config.StatsUtils;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {
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
        SheepQuest plugin = SheepQuest.getInstance();
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
        }

        return null;
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
