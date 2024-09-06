package me.cubixor.sheepquest.game.inventories;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.minigamesapi.spigot.game.arena.LocalArena;
import me.cubixor.minigamesapi.spigot.game.inventories.Menu;
import me.cubixor.minigamesapi.spigot.game.items.GameItem;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.arena.Team;
import me.cubixor.sheepquest.game.BossBarManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamsMenu extends Menu {

    private final SQArena arena;
    private final BossBarManager bossBarManager;

    protected TeamsMenu(LocalArena arena, BossBarManager bossBarManager) {
        super(arena);
        this.arena = (SQArena) arena;
        this.bossBarManager = bossBarManager;
    }

    @Override
    public Inventory create() {
        int invSlots;
        if (arena.getTeams().size() > 9) {
            invSlots = 18;
        } else {
            invSlots = 9;
        }
        return Bukkit.createInventory(null, invSlots, Messages.get("game.team-menu-name"));
    }

    @Override
    public void update() {
        Map<Team, List<String>> lore = new HashMap<>();
        List<String> lorePlayers = Messages.getList("game.team-menu-players");
        List<Team> arenaTeams = arena.getTeams();
        HashMap<Team, Integer> players = new HashMap<>();

        for (Team team : arenaTeams) {
            lore.put(team, new ArrayList<>(lorePlayers));
            players.put(team, 0);
        }


        for (Player p : arena.getPlayerTeam().keySet()) {
            Team team = arena.getPlayerTeam().get(p);
            if (!team.equals(Team.NONE)) {
                List<String> newLore = lore.get(team);
                newLore.add(Messages.get("game.team-menu-players-format", "%player%", p.getName()));
                players.replace(team, players.get(team) + 1);
            }
        }

        for (Team team : arenaTeams) {
            if (players.get(team) == 0) {
                lore.replace(team, Messages.getList("game.team-menu-no-players"));
            }
        }

        Map<Team, ItemStack> arenaTeamItems = new HashMap<>();
        for (Team team : arena.getTeams()) {
            if (arenaTeams.contains(team)) {
                arenaTeamItems.put(team, new GameItem(team.getWool().parseItem(), "game.team-menu-team-" + team, "game.team-menu-no-players").getItem());
            }
        }

        for (Team team : arenaTeamItems.keySet()) {

            ItemStack itemStack = arenaTeamItems.get(team);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(lore.get(team));
            itemStack.setItemMeta(itemMeta);

            if (players.get(team) <= 1) {
                itemStack.setAmount(1);
            } else {
                itemStack.setAmount(players.get(team));
            }
            arenaTeamItems.replace(team, itemStack);
        }

        int slot = 0;
        for (Team team : arenaTeams) {
            getInventory().setItem(slot, arenaTeamItems.get(team));
            slot++;
        }

        GameItem randomTeam = new GameItem(XMaterial.QUARTZ.parseItem(), "game.team-menu-team-random", "game.team-menu-team-random-lore");
        getInventory().setItem(getInventory().getSize() == 9 ? 8 : 17, randomTeam.getItem());
    }

    @Override
    public void handleClick(InventoryClickEvent evt, Player player) {
        Team team = Team.getByWool(XMaterial.matchXMaterial(evt.getCurrentItem()));

        if (team.equals(Team.NONE)) {
            putInTeam(player, Team.NONE, arena);

            Messages.send(player, "game.team-join-random");
        } else {
            Map<Team, Integer> teamPlayers = arena.countTeamPlayers();
            String teamMessage = team.getName();

            if (teamPlayers.get(team) >= ((float) arena.getPlayers().size() / (float) arena.getTeams().size())) {
                Messages.send(player, "game.team-full", "%team%", teamMessage);
                return;
            }

            if (!arena.getPlayerTeam().get(player).equals(team)) {
                Messages.send(player, "game.already-in-this-team", "%team%", teamMessage);
                return;
            }

            putInTeam(player, team, arena);

            Messages.send(player, "game.team-join-success", "%team%", teamMessage);
        }
    }

    private void putInTeam(Player player, Team team, SQArena arena) {
        arena.getPlayerTeam().replace(player, team);
        bossBarManager.addPlayer(player, team);
        //TODO Scoreboard
        player.getInventory().setHelmet(team.getBanner().parseItem());
    }
}
