package me.cubixor.sheepquest.spigot.game;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.VersionUtils;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Scoreboards {

    private static final String[] lineNames = new String[]{
            ChatColor.BLACK.toString(), ChatColor.BLACK.toString() + ChatColor.WHITE,
            ChatColor.DARK_BLUE.toString(), ChatColor.DARK_BLUE.toString() + ChatColor.WHITE,
            ChatColor.DARK_GREEN.toString(), ChatColor.DARK_GREEN.toString() + ChatColor.WHITE,
            ChatColor.DARK_AQUA.toString(), ChatColor.DARK_AQUA.toString() + ChatColor.WHITE,
            ChatColor.DARK_RED.toString(), ChatColor.DARK_RED.toString() + ChatColor.WHITE,
            ChatColor.DARK_PURPLE.toString(), ChatColor.DARK_PURPLE.toString() + ChatColor.WHITE,
            ChatColor.GOLD.toString(), ChatColor.GOLD.toString() + ChatColor.WHITE,
            ChatColor.GRAY.toString(), ChatColor.GRAY.toString() + ChatColor.WHITE,
            ChatColor.DARK_GRAY.toString(), ChatColor.DARK_GRAY.toString() + ChatColor.WHITE,
            ChatColor.BLUE.toString(), ChatColor.BLUE.toString() + ChatColor.WHITE,
            ChatColor.GREEN.toString(), ChatColor.GREEN.toString() + ChatColor.WHITE,
            ChatColor.AQUA.toString(), ChatColor.AQUA.toString() + ChatColor.WHITE,
            ChatColor.RED.toString(), ChatColor.RED.toString() + ChatColor.WHITE,
            ChatColor.LIGHT_PURPLE.toString(), ChatColor.LIGHT_PURPLE.toString() + ChatColor.WHITE,
            ChatColor.YELLOW.toString(), ChatColor.YELLOW.toString() + ChatColor.WHITE,
            ChatColor.WHITE.toString(), ChatColor.WHITE.toString() + ChatColor.BLACK
    };

    private final SheepQuest plugin;

    public Scoreboards() {
        plugin = SheepQuest.getInstance();
    }

    public Scoreboard getWaitingScoreboard(LocalArena localArena, Player player) {
        Scoreboard scoreboard = localArena.getPlayerScoreboards().get(player);

        String arenaString = localArena.getName();
        int count = localArena.getPlayerTeam().keySet().size();

        LinkedList<String> message = new LinkedList<>(plugin.getMessageList("game.scoreboard-new-waiting"));

        String countString = Integer.toString(count);
        String teamName = localArena.getPlayerTeam().get(player).getName();
        String kitName = localArena.getPlayerKit().get(player).getName();
        String date = getDate();


        String[] msg = new String[message.size()];

        for (int i = 0; i < message.size(); i++) {
            msg[i] = message.get(i)
                    .replace("%arena%", arenaString)
                    .replace("%players%", countString)
                    .replace("%team%", teamName)
                    .replace("%kit%", kitName)
                    .replace("%date%", date);
        }

        setMsg(scoreboard, msg);

        return scoreboard;
    }

    public Scoreboard getStartingScoreboard(LocalArena localArena, Player player) {
        Scoreboard scoreboard = localArena.getPlayerScoreboards().get(player);

        String arenaString = localArena.getName();
        int count = localArena.getPlayerTeam().keySet().size();
        int time = localArena.getTimer();

        LinkedList<String> message = new LinkedList<>(plugin.getMessageList("game.scoreboard-new-starting"));

        String countString = Integer.toString(count);
        String teamName = localArena.getPlayerTeam().get(player).getName();
        String kitName = localArena.getPlayerKit().get(player).getName();
        String timeLongTime = getTimeLong(time);
        String timeShortTime = Integer.toString(time);
        String date = getDate();


        String[] msg = new String[message.size()];

        for (int i = 0; i < message.size(); i++) {
            msg[i] = message.get(i)
                    .replace("%arena%", arenaString)
                    .replace("%players%", countString)
                    .replace("%team%", teamName)
                    .replace("%kit%", kitName)
                    .replace("%time-long%", timeLongTime)
                    .replace("%time-short%", timeShortTime)
                    .replace("%date%", date);
        }

        setMsg(scoreboard, msg);

        return scoreboard;
    }

    public Scoreboard getGameScoreboard(LocalArena localArena, Player player) {
        Scoreboard scoreboard = localArena.getPlayerScoreboards().get(player);

        String arenaString = localArena.getName();
        int count = localArena.getPlayerTeam().keySet().size();
        int sheepTime = localArena.getSheepTimer();
        int time = localArena.getTimer();

        List<String> teamPoints = new ArrayList<>();
        for (Team team : ConfigUtils.getTeamList(arenaString)) {
            teamPoints.add(plugin.getMessage("game.scoreboard-team")
                    .replace("%team%", team.getName())
                    .replace("%points%", Integer.toString(localArena.getPoints().get(team))));
        }

        List<String> message = new ArrayList<>(plugin.getMessageList("game.scoreboard-new-game"));
        for (int i = 0; i < message.size(); i++) {
            String line = message.get(i);
            if (line.contains("%teams%")) {
                message.remove(line);
                message.addAll(i, teamPoints);
                break;
            }
        }

        String countString = Integer.toString(count);
        String teamName = localArena.getPlayerTeam().get(player).getName();
        String kitName = localArena.getPlayerKit().get(player).getName();
        String timeLongSheep = getTimeLong(sheepTime);
        String timeShortSheep = Integer.toString(sheepTime);
        String timeLongTime = getTimeLong(time);
        String timeShortTime = Integer.toString(time);
        String date = getDate();

        String[] msg = new String[message.size()];

        for (int i = 0; i < message.size(); i++) {
            msg[i] = message.get(i)
                    .replace("%arena%", arenaString)
                    .replace("%players%", countString)
                    .replace("%team%", teamName)
                    .replace("%kit%", kitName)
                    .replace("%sheep-long%", timeLongSheep)
                    .replace("%sheep-short%", timeShortSheep)
                    .replace("%time-long%", timeLongTime)
                    .replace("%time-short%", timeShortTime)
                    .replace("%date%", date);
        }

        setMsg(scoreboard, msg);

        return scoreboard;
    }

    public Scoreboard getEndingScoreboard(LocalArena localArena, Player player) {
        Scoreboard scoreboard = localArena.getPlayerScoreboards().get(player);

        String arenaString = localArena.getName();
        int count = localArena.getPlayerTeam().keySet().size();
        int time = localArena.getTimer();

        List<String> teamPoints = new ArrayList<>();
        for (Team team : ConfigUtils.getTeamList(arenaString)) {
            teamPoints.add(plugin.getMessage("game.scoreboard-team")
                    .replace("%team%", team.getName())
                    .replace("%points%", Integer.toString(localArena.getPoints().get(team))));
        }

        List<String> message = new ArrayList<>(plugin.getMessageList("game.scoreboard-new-ending"));
        for (int i = 0; i < message.size(); i++) {
            String line = message.get(i);
            if (line.contains("%teams%")) {
                message.remove(line);
                message.addAll(i, teamPoints);
                break;
            }
        }

        String countString = Integer.toString(count);
        String teamName = localArena.getPlayerTeam().get(player).getName();
        String kitName = localArena.getPlayerKit().get(player).getName();
        String timeLongTime = getTimeLong(time);
        String timeShortTime = Integer.toString(time);
        String date = getDate();

        String[] msg = new String[message.size()];

        for (int i = 0; i < message.size(); i++) {
            msg[i] = message.get(i)
                    .replace("%arena%", arenaString)
                    .replace("%players%", countString)
                    .replace("%team%", teamName)
                    .replace("%kit%", kitName)
                    .replace("%time-long%", timeLongTime)
                    .replace("%time-short%", timeShortTime)
                    .replace("%date%", date);
        }

        setMsg(scoreboard, msg);

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

    private String getDate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(plugin.getConfig().getString("date-format"));
        return localDateTime.format(dateTimeFormatter);
    }

    private void setMsg(Scoreboard scoreboard, String[] text) {
        for (int j = 0; j < text.length; j++) {
            org.bukkit.scoreboard.Team team = scoreboard.getTeam(Integer.toString(j));

            int i = text.length - 1 - j;

            if (!VersionUtils.isBefore13() || text[i].length() <= 16) {
                team.setPrefix(text[i]);
                continue;
            }


            if (text[i].length() > 32) {
                text[i] = text[i].substring(0, 32);
            }

            String str1 = text[i].substring(0, 16);
            String str2 = text[i].substring(16);

            team.setPrefix(str1);
            team.setSuffix(ChatColor.RESET + ChatColor.getLastColors(str1) + str2);
        }
    }

    public void createScoreboard(LocalArena localArena, Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective(player.getName(), "");
        objective.setDisplayName(plugin.getMessage("game.scoreboard-title"));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        localArena.getPlayerScoreboards().put(player, scoreboard);

        setSizeForPlayer(player, localArena);

    }

    public void changeScoreboardSize(LocalArena localArena) {
        for (Player p : localArena.getPlayerScoreboards().keySet()) {
            setSizeForPlayer(p, localArena);
        }
    }

    private void setSizeForPlayer(Player p, LocalArena localArena) {
        Scoreboard scoreboard = localArena.getPlayerScoreboards().get(p);
        Objective objective = scoreboard.getObjective(p.getName());

        int size = plugin.getMessageList("game.scoreboard-new-" + localArena.getState().getCode()).size();
        if (localArena.getState().equals(GameState.GAME) || localArena.getState().equals(GameState.ENDING)) {
            size = size + localArena.getTeamRegions().size() - 2;
        }

        int j = 0;
        for (org.bukkit.scoreboard.Team team : scoreboard.getTeams()) {
            scoreboard.resetScores(lineNames[j]);
            team.unregister();
            j++;
        }


        for (int i = 0; i < size; i++) {
            org.bukkit.scoreboard.Team team = scoreboard.registerNewTeam(Integer.toString(i));
            team.addEntry(lineNames[i]);
            Score score = objective.getScore(lineNames[i]);
            score.setScore(i);
        }
    }

}
