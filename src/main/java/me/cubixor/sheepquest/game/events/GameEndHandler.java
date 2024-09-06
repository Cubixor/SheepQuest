package me.cubixor.sheepquest.game.events;

import com.cryptomorin.xseries.messages.Titles;
import com.google.common.collect.ImmutableMap;
import me.cubixor.minigamesapi.spigot.config.stats.StatsManager;
import me.cubixor.minigamesapi.spigot.events.GameEndEvent;
import me.cubixor.minigamesapi.spigot.events.TimerTickEvent;
import me.cubixor.minigamesapi.spigot.game.arena.GameState;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.sheepquest.arena.PlayerGameStats;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.arena.Team;
import me.cubixor.sheepquest.config.SQStatsField;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class GameEndHandler implements Listener {

    private final StatsManager statsManager;

    public GameEndHandler(StatsManager statsManager) {
        this.statsManager = statsManager;
    }

    @EventHandler
    public void onTimer(TimerTickEvent evt) {
        if (!evt.getGameState().equals(GameState.GAME)) {
            return;
        }
        SQArena arena = (SQArena) evt.getLocalArena();

        if (arena.getTimeLeft() <= 0) {
            Team winner = getWinner(arena);
            List<Player> winners = arena.getTeamPlayers(winner);

            arena.getStateManager().setEnd(winners);
        }
    }

    private Team getWinner(SQArena arena) {
        Optional<Map.Entry<Team, Integer>> winner = arena.getPoints()
                .entrySet()
                .stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .filter(e -> e.getValue() > 0);

        if (!winner.isPresent()) {
            return Team.NONE;
        }

        int sameScore = (int) arena.getPoints()
                .values()
                .stream()
                .filter(points -> points.equals(winner.get().getValue()))
                .count();

        if (sameScore == 0) {
            return Team.NONE;
        }

        return winner.get().getKey();
    }

    @EventHandler
    public void onGameEnd(GameEndEvent evt) {
        SQArena arena = (SQArena) evt.getLocalArena();
        List<Player> winners = evt.getWinners();
        boolean noWinners = winners.isEmpty();
        Team winner = noWinners ? Team.NONE : arena.getPlayerTeam().get(winners.get(0));

        String winnerTeamString = winner.toString();
        int stay = evt.getLocalArena().getTimer();

        StringBuilder summaryPoints = new StringBuilder();
        for (Team team : arena.getTeams()) {
            String teamPoints = Integer.toString(arena.getPoints().get(team));

            summaryPoints.append(Messages.get("game.summary-points", ImmutableMap.of(
                    "%team%", team.getName(),
                    "%points%", teamPoints)));
        }
        String wonTitle = Messages.get("game.win-title");
        String defeatTitle = Messages.get("game.defeat-title");
        String subtitle = Messages.get("game." + (noWinners ? "no-win" : "win-defeat") + "-subtitle",
                "%team%", winner.getName());


        for (Player p : arena.getBukkitPlayers()) {
            boolean won = winners.contains(p);


            p.setFlying(false);
            p.setAllowFlight(false);

            PlayerGameStats gameStats = arena.getPlayerGameStats().get(p);
            addStats(p, gameStats);

            Titles.sendTitle(p, 10, stay - 20, 20, won ? wonTitle : defeatTitle, subtitle);
            Messages.sendList(p, "game.summary", ImmutableMap.of(
                    "%teams%", summaryPoints.toString(),
                    "%kills%", Integer.toString(gameStats.getKills()),
                    "%deaths%", Integer.toString(gameStats.getDeaths()),
                    "%sheep%", Integer.toString(gameStats.getSheepTaken())));
        }
    }

    private void addStats(Player p, PlayerGameStats gameStats) {
        statsManager.addStats(p.getName(), SQStatsField.KILLS, gameStats.getKills());
        statsManager.addStats(p.getName(), SQStatsField.DEATHS, gameStats.getDeaths());
        statsManager.addStats(p.getName(), SQStatsField.SHEEP, gameStats.getSheepTaken());
        statsManager.addStats(p.getName(), SQStatsField.BONUS_SHEEP, gameStats.getBonusSheepTaken());
    }

    private void rewardPlayers() {
        //TODO Rewards for all
    }
}
