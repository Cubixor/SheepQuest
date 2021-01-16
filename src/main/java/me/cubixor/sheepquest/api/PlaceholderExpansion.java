package me.cubixor.sheepquest.api;

import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.gameInfo.Arena;
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

        if (paramsSplit[0].equals("status")) {
            Arena arena = plugin.getArenas().get(param1);
            if (arena == null) {
                return null;
            }
            return Utils.getStringState(arena);
        } else if (paramsSplit[0].equals("players")) {
            Arena arena = plugin.getArenas().get(param1);
            if (arena == null) {
                return null;
            }
            return Integer.toString(arena.getPlayers().keySet().size());
        } else if (paramsSplit[0].equals("wins")) {
            return Integer.toString(plugin.getStats().getInt("Players." + param1 + ".wins"));
        } else if (paramsSplit[0].equals("looses")) {
            return Integer.toString(plugin.getStats().getInt("Players." + param1 + ".looses"));
        } else if (paramsSplit[0].equals("games")) {
            return Integer.toString(plugin.getStats().getInt("Players." + param1 + ".games-played"));
        } else if (paramsSplit[0].equals("kills")) {
            return Integer.toString(plugin.getStats().getInt("Players." + param1 + ".kills"));
        } else if (paramsSplit[0].equals("deaths")) {
            return Integer.toString(plugin.getStats().getInt("Players." + param1 + ".deaths"));
        } else if (paramsSplit[0].equals("sheep")) {
            return Integer.toString(plugin.getStats().getInt("Players." + param1 + ".sheep-taken"));
        } else if (paramsSplit[0].equals("bonus-sheep")) {
            return Integer.toString(plugin.getStats().getInt("Players." + param1 + ".bonus-sheep-taken"));
        }


        return null;
    }
}
