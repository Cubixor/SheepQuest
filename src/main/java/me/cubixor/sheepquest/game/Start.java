package me.cubixor.sheepquest.game;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.TitleAPI;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.game.events.SpecialEvents;
import me.cubixor.sheepquest.gameInfo.Arena;
import me.cubixor.sheepquest.gameInfo.GameState;
import me.cubixor.sheepquest.gameInfo.PlayerGameStats;
import me.cubixor.sheepquest.gameInfo.Team;
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

    public Start() {
        plugin = SheepQuest.getInstance();
    }

    public void start(Arena arena) {
        String arenaName = Utils.getArenaString(arena);

        arena.setState(GameState.GAME);
        arena.setTimer(plugin.getConfig().getInt("game-time"));
        arena.setSheepTimer(plugin.getConfig().getInt("sheep-time"));

        for (Team t : Team.values()) {
            if (!t.equals(Team.NONE)) {
                arena.getPoints().put(t, 0);
            }
        }

        GameTimer gameTimer = new GameTimer();
        gameTimer.gameTime(arenaName);
        gameTimer.spawnSheep(arena);

        SpecialEvents specialEvents = new SpecialEvents();
        specialEvents.setupSpecialEvents(arenaName);

        for (Player p : arena.getPlayers().keySet()) {

            p.getInventory().setItem(plugin.getItems().getSheepItemSlot(), plugin.getItems().getSheepItem());
            p.getInventory().setItem(plugin.getItems().getWeaponItemSlot(), plugin.getItems().getWeaponItem());
            p.setExp(0);
            p.setLevel(0);
            plugin.getPlayerInfo().get(p).getTipTask().cancel();
            Utils.removeBossBars(p, arena);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(" "));
            arena.getPlayerStats().put(p, new PlayerGameStats());


            Team team = arena.getPlayers().get(p);

            if (team.equals(Team.NONE)) {
                LinkedHashMap<Team, Integer> teamSort = Utils.sortTeams(Utils.getTeamPlayers(arena));
                team = (new ArrayList<>(teamSort.keySet())).get(0);

                arena.getPlayers().replace(p, team);
            }

            setArmor(p, arena.getPlayers().get(p));
            p.teleport((Location) plugin.getArenasConfig().get("Arenas." + arenaName + ".teams." + Utils.getTeamString(arena.getPlayers().get(p)) + "-spawn"));

            p.playSound(p.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.start")).get().parseSound(), 1000, 1);
            TitleAPI.sendTitle(p, 0, 60, 10, plugin.getMessage("game.start-title"), plugin.getMessage("game.start-subtitle")
                    .replace("%team%", plugin.getMessage("general.team-" + Utils.getTeamString(team))));

        }

        new Signs().updateSigns(arena);
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
        Color color = Utils.getColor(team);

        ItemStack helmet = setArmorItem(XMaterial.LEATHER_HELMET.parseMaterial(), color);
        ItemStack chestplate = setArmorItem(XMaterial.LEATHER_CHESTPLATE.parseMaterial(), color);
        ItemStack leggings = setArmorItem(XMaterial.LEATHER_LEGGINGS.parseMaterial(), color);
        ItemStack boots = setArmorItem(XMaterial.LEATHER_BOOTS.parseMaterial(), color);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);

        LeatherArmorMeta chestplateMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        chestplateMeta.setDisplayName(plugin.getMessage("game.team-menu-team-" + Utils.getTeamString(team)));
        chestplate.setItemMeta(chestplateMeta);
        player.getInventory().setItem(8, chestplate);
    }


}
