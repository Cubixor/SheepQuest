package me.cubixor.sheepquest.game;

import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.Team;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class Scoreboards {

    private final SheepQuest plugin;

    public Scoreboards() {
        plugin = SheepQuest.getInstance();
    }

    public Scoreboard getWaitingScoreboard(Arena arena) {
        String arenaString = Utils.getArenaString(arena);
        int count = arena.getPlayers().keySet().size();

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("SheepQuest", "");
        objective.setDisplayName(plugin.getMessage("game.scoreboard-title"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score clear1 = objective.getScore("");
        clear1.setScore(8);
        Score arenas = objective.getScore(plugin.getMessage("game.scoreboard-arena").replace("%arena%", arenaString));
        arenas.setScore(7);
        Score clear2 = objective.getScore(" ");
        clear2.setScore(6);
        Score players = objective.getScore(plugin.getMessage("game.scoreboard-players").replace("%players%", Integer.toString(count)));
        players.setScore(5);
        Score clear3 = objective.getScore("  ");
        clear3.setScore(4);
        Score waiting = objective.getScore(plugin.getMessage("game.scoreboard-waiting"));
        waiting.setScore(3);
        Score clear4 = objective.getScore("   ");
        clear4.setScore(2);
        Score spigot = objective.getScore(plugin.getMessage("game.scoreboard-server"));
        spigot.setScore(1);
        return scoreboard;
    }

    public Scoreboard getStartingScoreboard(Arena arena) {
        String arenaString = Utils.getArenaString(arena);
        int count = arena.getPlayers().keySet().size();

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("SheepQuest", "");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(plugin.getMessage("game.scoreboard-title"));

        Score clear1 = objective.getScore("");
        clear1.setScore(8);
        Score arenas = objective.getScore(plugin.getMessage("game.scoreboard-arena").replace("%arena%", arenaString));
        arenas.setScore(7);
        Score clear2 = objective.getScore(" ");
        clear2.setScore(6);
        Score players = objective.getScore(plugin.getMessage("game.scoreboard-players").replace("%players%", Integer.toString(count)));
        players.setScore(5);
        Score clear3 = objective.getScore("  ");
        clear3.setScore(4);
        Score waiting = objective.getScore(plugin.getMessage("game.scoreboard-starting").replace("%time%", Integer.toString(arena.getTimer())));
        waiting.setScore(3);
        Score clear4 = objective.getScore("   ");
        clear4.setScore(2);
        Score spigot = objective.getScore(plugin.getMessage("game.scoreboard-server"));
        spigot.setScore(1);

        return scoreboard;
    }

    public Scoreboard getGameScoreboard(Arena arena) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("SheepQuest", "");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(plugin.getMessage("game.scoreboard-title"));


        Score clear1 = objective.getScore("");
        clear1.setScore(10);
        Score red = objective.getScore(plugin.getMessage("game.scoreboard-red-sheep").replace("%count%", arena.getPoints().get(Team.RED).toString()));
        red.setScore(9);
        Score green = objective.getScore(plugin.getMessage("game.scoreboard-green-sheep").replace("%count%", arena.getPoints().get(Team.GREEN).toString()));
        green.setScore(8);
        Score blue = objective.getScore(plugin.getMessage("game.scoreboard-blue-sheep").replace("%count%", arena.getPoints().get(Team.BLUE).toString()));
        blue.setScore(7);
        Score yellow = objective.getScore(plugin.getMessage("game.scoreboard-yellow-sheep").replace("%count%", arena.getPoints().get(Team.YELLOW).toString()));
        yellow.setScore(6);
        Score clear2 = objective.getScore(" ");
        clear2.setScore(5);
        Score next = objective.getScore(plugin.getMessage("game.scoreboard-next-sheep"));
        next.setScore(4);
        Score nextTime;
        if (arena.getSheepTimer() == 1) {
            nextTime = objective.getScore(plugin.getMessage("game.scoreboard-second").replace("%count%", Integer.toString(arena.getSheepTimer())) + " ");
        } else {
            nextTime = objective.getScore(plugin.getMessage("game.scoreboard-seconds").replace("%count%", Integer.toString(arena.getSheepTimer())) + " ");
        }
        nextTime.setScore(3);
        Score clear3 = objective.getScore("  ");
        clear3.setScore(2);
        Score timeLeftName = objective.getScore(plugin.getMessage("game.scoreboard-time-left"));
        timeLeftName.setScore(1);


        int time;
        if (arena.getTimer() > 60) {
            time = arena.getTimer() / 60;
            String timeString = Integer.toString(time);
            Score timeLeft;
            if (time == 1) {
                timeLeft = objective.getScore(plugin.getMessage("game.scoreboard-minute").replace("%count%", timeString));
            } else {
                timeLeft = objective.getScore(plugin.getMessage("game.scoreboard-minutes").replace("%count%", timeString));

            }
            timeLeft.setScore(0);

        } else {
            time = arena.getTimer();
            String timeString = Integer.toString(time);
            Score timeLeft;
            if (time == 1) {
                timeLeft = objective.getScore(plugin.getMessage("game.scoreboard-second").replace("%count%", timeString));
            } else {
                timeLeft = objective.getScore(plugin.getMessage("game.scoreboard-seconds").replace("%count%", timeString));
            }
            timeLeft.setScore(0);
        }

        return scoreboard;
    }

    public Scoreboard getEndingScoreboard(Arena arena) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("SheepQuest", "");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(plugin.getMessage("game.scoreboard-title"));


        Score clear1 = objective.getScore("");
        clear1.setScore(7);
        Score red = objective.getScore(plugin.getMessage("game.scoreboard-red-sheep").replace("%count%", arena.getPoints().get(Team.RED).toString()));
        red.setScore(6);
        Score green = objective.getScore(plugin.getMessage("game.scoreboard-green-sheep").replace("%count%", arena.getPoints().get(Team.GREEN).toString()));
        green.setScore(5);
        Score blue = objective.getScore(plugin.getMessage("game.scoreboard-blue-sheep").replace("%count%", arena.getPoints().get(Team.BLUE).toString()));
        blue.setScore(4);
        Score yellow = objective.getScore(plugin.getMessage("game.scoreboard-yellow-sheep").replace("%count%", arena.getPoints().get(Team.YELLOW).toString()));
        yellow.setScore(3);
        Score clear2 = objective.getScore(" ");
        clear2.setScore(2);
        Score next = objective.getScore(plugin.getMessage("game.scoreboard-ending"));
        next.setScore(1);
        Score nextTime;
        if (arena.getTimer() == 1) {
            nextTime = objective.getScore(plugin.getMessage("game.scoreboard-second").replace("%count%", Integer.toString(arena.getTimer())) + " ");
        } else {
            nextTime = objective.getScore(plugin.getMessage("game.scoreboard-seconds").replace("%count%", Integer.toString(arena.getTimer())) + " ");
        }
        nextTime.setScore(0);
        return scoreboard;
    }

}
