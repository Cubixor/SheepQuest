package me.cubixor.sheepquest.spigot.game;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.api.Sounds;
import me.cubixor.sheepquest.spigot.commands.PlayCommands;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.gameInfo.GameState;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

    public Teams() {
        plugin = SheepQuest.getInstance();
    }

    @EventHandler
    public void onClick(PlayerInteractEvent evt) {
        LocalArena localArena = Utils.getLocalArena(evt.getPlayer());
        if (localArena != null && evt.getItem() != null) {

            if (!plugin.isBefore9()) {
                if (!evt.getHand().equals(EquipmentSlot.HAND)) {
                    return;
                }
            }

            if (evt.getItem().equals(plugin.getItems().getTeamItem())) {
                if (localArena.getTeamChooseInv().getItem(0) == null || localArena.getTeamChooseInv().getItem(0).getType().equals(Material.AIR)) {
                    menuUpdate(localArena);
                }
                evt.getPlayer().openInventory(localArena.getTeamChooseInv());
                evt.setCancelled(true);
            } else if (evt.getItem().equals(plugin.getItems().getLeaveItem())) {
                PlayCommands playCommands = new PlayCommands();
                playCommands.sendKickMessage(evt.getPlayer(), localArena);
                playCommands.kickFromLocalArena(evt.getPlayer(), localArena, false, false);
                evt.setCancelled(true);
            } else if (evt.getItem().equals(plugin.getItems().getKitsItem())) {
                evt.getPlayer().openInventory(plugin.getItems().getKitsInventory());
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        LocalArena localArena = Utils.getLocalArena((Player) evt.getWhoClicked());
        if (localArena != null && evt.getClickedInventory().equals(localArena.getTeamChooseInv()) && evt.getCurrentItem() != null && !evt.getCurrentItem().getType().equals(Material.AIR)) {

            evt.setCancelled(true);

            Player player = (Player) evt.getWhoClicked();
            Team team = Utils.getTeamByWool(evt.getCurrentItem());
            String arenaString = localArena.getName();

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                int max = ConfigUtils.getInt(arenaString, ConfigField.MAX_PLAYERS);

                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (team.equals(Team.NONE)) {
                        localArena.getPlayerTeam().replace(player, Team.NONE);
                        player.sendMessage(plugin.getMessage("game.team-join-random"));
                        Utils.removeTeamBossBars(player, localArena);
                        localArena.getTeamBossBars().get(Team.NONE).addPlayer(player);
                        player.getInventory().setHelmet(new ItemStack(Material.AIR));
                    } else {
                        HashMap<Team, Integer> teamPlayers = new HashMap<>(Utils.getTeamPlayers(localArena));
                        String teamMessage = team.getName();

                        if (teamPlayers.get(team) < (max / ConfigUtils.getTeamList(arenaString).size())) {
                            if (!localArena.getPlayerTeam().get(player).equals(team)) {
                                localArena.getPlayerTeam().replace(player, team);
                                Utils.removeTeamBossBars(player, localArena);
                                localArena.getTeamBossBars().get(team).addPlayer(player);
                                player.getInventory().setHelmet(team.getBanner());
                                if (localArena.getState().equals(GameState.WAITING)) {
                                    player.setScoreboard(new Scoreboards().getWaitingScoreboard(localArena, player));
                                }
                                player.sendMessage(plugin.getMessage("game.team-join-success").replace("%team%", teamMessage));
                            } else {
                                player.sendMessage(plugin.getMessage("game.already-in-this-team").replace("%team%", teamMessage));
                            }
                        } else {
                            player.sendMessage(plugin.getMessage("game.team-full").replace("%team%", teamMessage));
                        }
                    }

                    menuUpdate(localArena);
                    Sounds.playSound(player, player.getLocation(), "click");
                    player.getOpenInventory().close();
                });
            });
        }
    }


    public void menuUpdate(LocalArena localArena) {
        HashMap<Team, List<String>> lore = new HashMap<>();
        List<String> lorePlayers = new ArrayList<>(plugin.getMessageList("game.team-menu-players"));
        List<Team> arenaTeams = new ArrayList<>(ConfigUtils.getTeamList(localArena.getName()));
        HashMap<Team, Integer> players = new HashMap<>();

        for (Team team : arenaTeams) {
            lore.put(team, lorePlayers);
            players.put(team, 0);
        }


        for (Player p : localArena.getPlayerTeam().keySet()) {
            Team team = localArena.getPlayerTeam().get(p);
            if (!team.equals(Team.NONE)) {
                List<String> newLore = new ArrayList<>(lore.get(team));
                newLore.add(plugin.getMessage("game.team-menu-players-format").replace("%player%", p.getName()));
                lore.replace(team, newLore);
                players.replace(team, players.get(team) + 1);
            }
        }

        for (Team team : arenaTeams) {
            if (players.get(team) == 0) {
                lore.replace(team, plugin.getMessageList("game.team-menu-no-players"));
            }
        }

        HashMap<Team, ItemStack> teamItems = new HashMap<>(plugin.getItems().getTeamItems());
        HashMap<Team, ItemStack> arenaTeamItems = new HashMap<>();
        for (Team team : teamItems.keySet()) {
            if (arenaTeams.contains(team)) {
                arenaTeamItems.put(team, teamItems.get(team));
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
            localArena.getTeamChooseInv().setItem(slot, arenaTeamItems.get(team));
            slot++;
        }
        localArena.getTeamChooseInv().setItem(localArena.getTeamChooseInv().getSize() == 9 ? 8 : 17, Utils.setItemStack(XMaterial.QUARTZ.parseMaterial(), "game.team-menu-team-random", "game.team-menu-team-random-lore"));
    }
}
