package me.cubixor.sheepquest.game;

import me.cubixor.sheepquest.Arena;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.Team;
import me.cubixor.sheepquest.Utils;
import me.cubixor.sheepquest.commands.PlayCommands;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Teams implements Listener {

    private final SheepQuest plugin;

    public Teams(SheepQuest sq) {
        plugin = sq;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent evt) {
        Utils utils = new Utils(plugin);
        Arena arena = utils.getArena(evt.getPlayer());
        if (arena != null && evt.getItem() != null && evt.getHand().equals(EquipmentSlot.HAND)) {
            evt.setCancelled(true);
            if (evt.getItem().equals(plugin.items.teamItem)) {

                evt.getPlayer().openInventory(arena.teamInventory);
                if (arena.teamInventory.getItem(1) == null) {
                    menuUpdate(arena);
                }


            } else if (evt.getItem().equals(plugin.items.leaveItem)) {
                PlayCommands playCommands = new PlayCommands(plugin);
                playCommands.sendKickMessage(evt.getPlayer(), arena);
                playCommands.kickPlayer(evt.getPlayer(), utils.getArenaString(arena));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        Utils utils = new Utils(plugin);
        Arena arena = utils.getArena((Player) evt.getWhoClicked());
        if (arena != null && evt.getInventory().equals(arena.teamInventory) && evt.getCurrentItem() != null) {

            evt.setCancelled(true);

            Player player = (Player) evt.getWhoClicked();
            Team team = utils.getWoolTeam(evt.getCurrentItem().getType());

            if (team.equals(Team.NONE)) {
                arena.playerTeam.replace(player, Team.NONE);
                player.sendMessage(plugin.getMessage("game.team-join-random"));
                utils.removeBossBars(player, arena);
                arena.teamBossBars.get(Team.NONE).addPlayer(player);
                player.getInventory().setHelmet(new ItemStack(Material.AIR));
                player.getOpenInventory().close();
            }

            if (evt.getCurrentItem().getType().toString().contains("WOOL")) {

                HashMap<Team, Integer> teamPlayers = utils.getTeamPlayers(arena);

                String teamMessage = plugin.getMessage("general.team-" + utils.getTeamString(team));
                if (teamPlayers.get(team) < plugin.getArenasConfig().getInt("Arenas." + utils.getArenaString(arena) + ".max-players") / 4) {
                    if (!arena.playerTeam.get(player).equals(team)) {
                        arena.playerTeam.replace(player, team);
                        utils.removeBossBars(player, arena);
                        arena.teamBossBars.get(team).addPlayer(player);
                        player.getInventory().setHelmet(new ItemStack(getTeamBanner(team)));
                        player.sendMessage(plugin.getMessage("game.team-join-success").replace("%team%", teamMessage));
                    } else {
                        player.sendMessage(plugin.getMessage("game.already-in-this-team").replace("%team%", teamMessage));
                    }
                } else {
                    player.sendMessage(plugin.getMessage("game.team-full").replace("%team%", teamMessage));
                }
                player.getOpenInventory().close();


                menuUpdate(arena);
            }
        }
    }

    private Material getTeamBanner(Team team) {
        Material banner = Material.WHITE_BANNER;
        switch (team) {
            case RED:
                banner = Material.RED_BANNER;
                break;
            case GREEN:
                banner = Material.GREEN_BANNER;
                break;
            case BLUE:
                banner = Material.BLUE_BANNER;
                break;
            case YELLOW:
                banner = Material.YELLOW_BANNER;
                break;
        }
        return banner;
    }

    public void loadBossBars(Arena arena) {
        Utils utils = new Utils(plugin);
        for (Team team : Team.values()) {
            String teamString = plugin.getMessage("general.team-" + utils.getTeamString(team));

            arena.teamBossBars.put(team, Bukkit.createBossBar(plugin.getMessage("game.bossbar-team").replace("%team%", teamString), utils.getBossBarColor(team), BarStyle.SOLID));
        }
    }


    public void menuUpdate(Arena arena) {
        Utils utils = new Utils(plugin);

        HashMap<Team, List<String>> lore = new HashMap<>();
        List<String> lorePlayers = new ArrayList<>(plugin.getMessageList("game.team-menu-players"));

        HashMap<Team, Integer> players = new HashMap<>();

        for (Team team : Team.values()) {
            if (!team.equals(Team.NONE)) {
                lore.put(team, lorePlayers);
                players.put(team, 0);
            }
        }


        for (Player p : arena.playerTeam.keySet()) {
            Team team = arena.playerTeam.get(p);
            if (!team.equals(Team.NONE)) {
                List<String> newLore = new ArrayList<>(lore.get(team));
                newLore.add(plugin.getMessage("game.team-menu-players-format").replace("%player%", p.getName()));
                lore.replace(team, newLore);
                players.replace(team, players.get(team) + 1);
            }
        }

        for (Team team : Team.values()) {
            if (!team.equals(Team.NONE)) {
                if (players.get(team) == 0) {
                    lore.replace(team, plugin.getMessageList("game.team-menu-no-players"));
                }
            }
        }

        HashMap<Team, ItemStack> teamItems = new HashMap<>(plugin.items.teamItems);
        for (Team team : teamItems.keySet()) {

            ItemStack itemStack = teamItems.get(team);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(lore.get(team));
            itemStack.setItemMeta(itemMeta);

            if (players.get(team) <= 1) {
                itemStack.setAmount(1);
            } else {
                itemStack.setAmount(players.get(team));
            }
            teamItems.replace(team, itemStack);
        }


        arena.teamInventory.setItem(0, teamItems.get(Team.RED));
        arena.teamInventory.setItem(2, teamItems.get(Team.GREEN));
        arena.teamInventory.setItem(4, teamItems.get(Team.BLUE));
        arena.teamInventory.setItem(6, teamItems.get(Team.YELLOW));
        arena.teamInventory.setItem(8, utils.setItemStack(Material.QUARTZ, "game.team-menu-team-random", "game.team-menu-team-random-lore"));
    }
}
