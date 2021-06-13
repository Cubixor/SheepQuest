package me.cubixor.sheepquest.spigot.game;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.commands.PlayCommands;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import me.cubixor.sheepquest.spigot.gameInfo.Team;
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

    public Teams() {
        plugin = SheepQuest.getInstance();
    }

    @EventHandler
    public void onClick(PlayerInteractEvent evt) {
        LocalArena localArena = Utils.getLocalArena(evt.getPlayer());
        if (localArena != null && evt.getItem() != null && evt.getHand().equals(EquipmentSlot.HAND)) {
            evt.setCancelled(true);
            if (evt.getItem().equals(plugin.getItems().getTeamItem())) {

                evt.getPlayer().openInventory(localArena.getTeamChooseInv());
                if (localArena.getTeamChooseInv().getItem(1) == null) {
                    menuUpdate(localArena);
                }


            } else if (evt.getItem().equals(plugin.getItems().getLeaveItem())) {
                PlayCommands playCommands = new PlayCommands();
                playCommands.sendKickMessage(evt.getPlayer(), localArena);
                playCommands.kickFromLocalArena(evt.getPlayer(), localArena, false, false);
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
                        Utils.removeBossBars(player, localArena);
                        localArena.getTeamBossBars().get(Team.NONE).addPlayer(player);
                        player.getInventory().setHelmet(new ItemStack(Material.AIR));
                    }

                    if (evt.getCurrentItem().getType().toString().contains("WOOL")) {

                        HashMap<Team, Integer> teamPlayers = new HashMap<>(Utils.getTeamPlayers(localArena));
                        String teamMessage = plugin.getMessage("general.team-" + team.getCode());

                        if (teamPlayers.get(team) < (max / 4)) {
                            if (!localArena.getPlayerTeam().get(player).equals(team)) {
                                localArena.getPlayerTeam().replace(player, team);
                                Utils.removeBossBars(player, localArena);
                                localArena.getTeamBossBars().get(team).addPlayer(player);
                                player.getInventory().setHelmet(getTeamBanner(team));
                                player.sendMessage(plugin.getMessage("game.team-join-success").replace("%team%", teamMessage));
                            } else {
                                player.sendMessage(plugin.getMessage("game.already-in-this-team").replace("%team%", teamMessage));
                            }
                        } else {
                            player.sendMessage(plugin.getMessage("game.team-full").replace("%team%", teamMessage));
                        }

                        menuUpdate(localArena);
                    }

                    player.playSound(player.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.team-choose")).get().parseSound(), 100, 1);
                    player.getOpenInventory().close();
                });
            });
        }
    }

    private ItemStack getTeamBanner(Team team) {
        ItemStack banner = null;
        switch (team) {
            case RED:
                banner = XMaterial.RED_BANNER.parseItem();
                break;
            case GREEN:
                banner = XMaterial.LIME_BANNER.parseItem();
                break;
            case BLUE:
                banner = XMaterial.BLUE_BANNER.parseItem();
                break;
            case YELLOW:
                banner = XMaterial.YELLOW_BANNER.parseItem();
                break;
        }
        return banner;
    }

    public void loadBossBars(LocalArena localArena) {
        for (Team team : Team.values()) {
            String teamString = plugin.getMessage("general.team-" + team.getCode());

            localArena.getTeamBossBars().put(team, Bukkit.createBossBar(plugin.getMessage("game.bossbar-team").replace("%team%", teamString), team.getBarColor(), BarStyle.SOLID));
        }
    }


    public void menuUpdate(LocalArena localArena) {
        HashMap<Team, List<String>> lore = new HashMap<>();
        List<String> lorePlayers = new ArrayList<>(plugin.getMessageList("game.team-menu-players"));

        HashMap<Team, Integer> players = new HashMap<>();

        for (Team team : Team.values()) {
            if (!team.equals(Team.NONE)) {
                lore.put(team, lorePlayers);
                players.put(team, 0);
            }
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

        for (Team team : Team.values()) {
            if (!team.equals(Team.NONE)) {
                if (players.get(team) == 0) {
                    lore.replace(team, plugin.getMessageList("game.team-menu-no-players"));
                }
            }
        }

        HashMap<Team, ItemStack> teamItems = new HashMap<>(plugin.getItems().getTeamItems());
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


        localArena.getTeamChooseInv().setItem(0, teamItems.get(Team.RED));
        localArena.getTeamChooseInv().setItem(2, teamItems.get(Team.GREEN));
        localArena.getTeamChooseInv().setItem(4, teamItems.get(Team.BLUE));
        localArena.getTeamChooseInv().setItem(6, teamItems.get(Team.YELLOW));
        localArena.getTeamChooseInv().setItem(8, Utils.setItemStack(XMaterial.QUARTZ.parseMaterial(), "game.team-menu-team-random", "game.team-menu-team-random-lore"));
    }
}
