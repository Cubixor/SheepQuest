package me.cubixor.sheepquest.game.events;

import com.google.common.collect.ImmutableMap;
import me.cubixor.minigamesapi.spigot.events.ScoreboardUpdateEvent;
import me.cubixor.minigamesapi.spigot.utils.MessageUtils;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.arena.Team;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardFormatter implements Listener {

    @EventHandler
    public void onScoreboardUpdate(ScoreboardUpdateEvent evt) {
        SQArena arena = (SQArena) evt.getLocalArena();

        String timeSheep = String.valueOf(arena.getSheepTimer());

        evt.addReplacement("%time-left%", MessageUtils.formatTime(arena.getTimeLeft(), "time-format"));
        evt.addReplacement("%time-sheep%", timeSheep);
        evt.addMultiLineReplacement("%teams%", getTeamScoreboard(arena));
    }


    private List<String> getTeamScoreboard(SQArena arena) {
        List<String> teamsList = new ArrayList<>();
        for (Team team : arena.getTeams()) {
            String teamPoints = Integer.toString(arena.getPoints().getOrDefault(team, 0));
            String teamLine = Messages.get("game.scoreboard-team", ImmutableMap.of(
                    "%team%", team.getName(),
                    "%points%", teamPoints));
            teamsList.add(teamLine);
        }
        return teamsList;
    }
}
