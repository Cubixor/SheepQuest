package me.cubixor.sheepquest.game;

import com.cryptomorin.xseries.XMaterial;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import me.cubixor.sheepquest.*;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Start {

    private final SheepQuest plugin;

    public Start(SheepQuest s) {
        plugin = s;
    }

    public void start(Arena arena) {
        Utils utils = new Utils(plugin);

        String arenaName = utils.getArenaString(arena);
        GameTimer gameTimer = new GameTimer(plugin);
        gameTimer.gameTime(arenaName);

        arena.state = GameState.GAME;
        arena.timer = plugin.getConfig().getInt("game-time");
        arena.sheepTimer = plugin.getConfig().getInt("sheep-time");

        for (Team t : Team.values()) {
            if (!t.equals(Team.NONE)) {
                arena.points.put(t, 0);
            }
        }

        for (Player p : arena.playerTeam.keySet()) {

            p.getInventory().setItem(plugin.items.sheepItemSlot, plugin.items.sheepItem);
            p.getInventory().setItem(plugin.items.weaponItemSlot, plugin.items.weaponItem);
            p.setExp(0);
            p.setLevel(0);
            plugin.playerInfo.get(p).tipTask.cancel();
            utils.removeBossBars(p, arena);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(" "));
            arena.playerStats.put(p, new PlayerGameStats());


            if (arena.playerTeam.get(p).equals(Team.NONE)) {

                LinkedHashMap<Team, Integer> teamSort = utils.sortTeams(utils.getTeamPlayers(arena));
                Team team = (new ArrayList<>(teamSort.keySet())).get(0);

                arena.playerTeam.replace(p, team);
            }

            setArmor(p, arena.playerTeam.get(p));
            p.teleport((Location) plugin.getArenasConfig().get("Arenas." + arenaName + ".teams." + utils.getTeamString(arena.playerTeam.get(p)) + "-spawn"));
        }

        new Signs(plugin).updateSigns(arena);
    }

    private ItemStack setArmorItem(Material armorType, Color color) {
        ItemStack armor = new ItemStack(armorType, 1);
        armor = NBTEditor.set(armor, (byte) 1, "Unbreakable");
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) armor.getItemMeta();
        armorMeta.setColor(color);
        armor.setItemMeta(armorMeta);
        return armor;
    }

    private void setArmor(Player player, Team team) {
        Utils utils = new Utils(plugin);

        Color color = utils.getColor(team);

        ItemStack helmet = setArmorItem(XMaterial.LEATHER_HELMET.parseMaterial(), color);
        ItemStack chestplate = setArmorItem(XMaterial.LEATHER_CHESTPLATE.parseMaterial(), color);
        ItemStack leggings = setArmorItem(XMaterial.LEATHER_LEGGINGS.parseMaterial(), color);
        ItemStack boots = setArmorItem(XMaterial.LEATHER_BOOTS.parseMaterial(), color);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);

        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        chestplateMeta.setDisplayName(plugin.getMessage("game.team-menu-team-" + utils.getTeamString(team)));
        chestplate.setItemMeta(chestplateMeta);
        player.getInventory().setItem(8, chestplate);
    }


}
