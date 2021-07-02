package me.cubixor.sheepquest.spigot.game;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;

public class Scoreboards {

    private final SheepQuest plugin;

    public Scoreboards() {
        plugin = SheepQuest.getInstance();
    }

    public Scoreboard getWaitingScoreboard(LocalArena localArena) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = getObjective(scoreboard);

        String arenaString = localArena.getName();
        int count = localArena.getPlayerTeam().keySet().size();

        LinkedList<String> message = new LinkedList<>(plugin.getMessageList("game.scoreboard-new-waiting"));
        int rowCount = message.size();

        int row = 0;
        for (int i = rowCount; i > 0; i--) {
            Score score = objective.getScore(message.get(row)
                    .replace("%arena%", arenaString)
                    .replace("%players%", Integer.toString(count))
                    .replace("%date%", getDate()));
            score.setScore(i);
            row++;
        }
        return scoreboard;
    }

    public Scoreboard getStartingScoreboard(LocalArena localArena) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = getObjective(scoreboard);

        String arenaString = localArena.getName();
        int count = localArena.getPlayerTeam().keySet().size();
        int time = localArena.getTimer();

        LinkedList<String> message = new LinkedList<>(plugin.getMessageList("game.scoreboard-new-starting"));
        int rowCount = message.size();

        int row = 0;
        for (int i = rowCount; i > 0; i--) {
            Score score = objective.getScore(message.get(row)
                    .replace("%arena%", arenaString)
                    .replace("%players%", Integer.toString(count))
                    .replace("%time-short%", Integer.toString(time))
                    .replace("%time-long%", getTimeLong(time))
                    .replace("%date%", getDate()));
            score.setScore(i);
            row++;
        }

        return scoreboard;
    }

    public Scoreboard getGameScoreboard(LocalArena localArena) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = getObjective(scoreboard);

        String arenaString = localArena.getName();
        int count = localArena.getPlayerTeam().keySet().size();
        int sheepTime = localArena.getSheepTimer();
        int time = localArena.getTimer();
        int red = localArena.getPoints().get(Team.RED);
        int green = localArena.getPoints().get(Team.GREEN);
        int blue = localArena.getPoints().get(Team.BLUE);
        int yellow = localArena.getPoints().get(Team.YELLOW);

        LinkedList<String> message = new LinkedList<>(plugin.getMessageList("game.scoreboard-new-game"));
        int rowCount = message.size();

        int row = 0;
        for (int i = rowCount; i > 0; i--) {
            Score score = objective.getScore(message.get(row)
                    .replace("%arena%", arenaString)
                    .replace("%players%", Integer.toString(count))
                    .replace("%sheep-long%", getTimeLong(sheepTime))
                    .replace("%sheep-short%", Integer.toString(sheepTime))
                    .replace("%time-long%", getTimeLong(time))
                    .replace("%time-short%", Integer.toString(time))
                    .replace("%red%", Integer.toString(red))
                    .replace("%green%", Integer.toString(green))
                    .replace("%blue%", Integer.toString(blue))
                    .replace("%yellow%", Integer.toString(yellow))
                    .replace("%date%", getDate()));
            score.setScore(i);
            row++;
        }

        return scoreboard;
    }

    public Scoreboard getEndingScoreboard(LocalArena localArena) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = getObjective(scoreboard);

        String arenaString = localArena.getName();
        int count = localArena.getPlayerTeam().keySet().size();
        int time = localArena.getTimer();
        int red = localArena.getPoints().get(Team.RED);
        int green = localArena.getPoints().get(Team.GREEN);
        int blue = localArena.getPoints().get(Team.BLUE);
        int yellow = localArena.getPoints().get(Team.YELLOW);

        LinkedList<String> message = new LinkedList<>(plugin.getMessageList("game.scoreboard-new-ending"));
        int rowCount = message.size();

        int row = 0;
        for (int i = rowCount; i > 0; i--) {
            Score score = objective.getScore(message.get(row)
                    .replace("%arena%", arenaString)
                    .replace("%players%", Integer.toString(count))
                    .replace("%time-long%", getTimeLong(time))
                    .replace("%time-short%", Integer.toString(time))
                    .replace("%red%", Integer.toString(red))
                    .replace("%green%", Integer.toString(green))
                    .replace("%blue%", Integer.toString(blue))
                    .replace("%yellow%", Integer.toString(yellow))
                    .replace("%date%", getDate()));
            score.setScore(i);
            row++;
        }

        return scoreboard;
    }

    private String getTimeLong(int time) {
        String timeLeft;
        if (time > 60) {
            int timeParsed = time / 60;
            if (timeParsed == 1) {
                timeLeft = plugin.getMessage("game.scoreboard-minute").replace("%count%", Integer.toString(timeParsed));
            } else {
                timeLeft = plugin.getMessage("game.scoreboard-minutes").replace("%count%", Integer.toString(timeParsed));
            }
        } else {
            if (time == 1) {
                timeLeft = plugin.getMessage("game.scoreboard-second").replace("%count%", Integer.toString(time));
            } else {
                timeLeft = plugin.getMessage("game.scoreboard-seconds").replace("%count%", Integer.toString(time));
            }
        }
        return timeLeft;
    }

    private Objective getObjective(Scoreboard scoreboard) {
        Objective objective = scoreboard.registerNewObjective("SheepQuest", "");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(plugin.getMessage("game.scoreboard-title"));
        return objective;
    }

    private String getDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(plugin.getConfig().getString("date-format"));
        return localDateTime.format(dateTimeFormatter);
    }

}
