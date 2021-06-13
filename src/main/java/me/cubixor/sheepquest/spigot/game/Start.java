package me.cubixor.sheepquest.spigot.game;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.game.events.SpecialEvents;
import me.cubixor.sheepquest.spigot.gameInfo.*;
import me.cubixor.sheepquest.spigot.socket.SocketClientSender;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Start {

    private final SheepQuest plugin;

    public Start() {
        plugin = SheepQuest.getInstance();
    }

    public void start(LocalArena localArena) {
        String arenaName = localArena.getName();

        localArena.setState(GameState.GAME);
        localArena.setTimer(plugin.getConfig().getInt("game-time"));
        localArena.setSheepTimer(plugin.getConfig().getInt("sheep-time"));

        for (Team t : Team.values()) {
            if (!t.equals(Team.NONE)) {
                localArena.getPoints().put(t, 0);
            }
        }

        GameTimer gameTimer = new GameTimer();
        gameTimer.gameTime(arenaName);
        gameTimer.spawnSheep(localArena);

        SpecialEvents specialEvents = new SpecialEvents();
        specialEvents.setupSpecialEvents(arenaName);

        for (Player p : localArena.getPlayerTeam().keySet()) {

            p.getInventory().setItem(plugin.getItems().getSheepItemSlot(), plugin.getItems().getSheepItem());
            p.getInventory().setItem(plugin.getItems().getWeaponItemSlot(), plugin.getItems().getWeaponItem());
            p.setExp(0);
            p.setLevel(0);
            plugin.getPlayerInfo().get(p).getTipTask().cancel();
            Utils.removeBossBars(p, localArena);
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(" "));
            localArena.getPlayerStats().put(p, new PlayerGameStats());


            Team team = localArena.getPlayerTeam().get(p);

            if (team.equals(Team.NONE)) {
                LinkedHashMap<Team, Integer> teamSort = Utils.sortTeams(Utils.getTeamPlayers(localArena));
                team = (new ArrayList<>(teamSort.keySet())).get(0);

                localArena.getPlayerTeam().replace(p, team);
            }

            ConfigField configField = Utils.getTeamSpawn(team.getCode());

            setArmor(p, localArena.getPlayerTeam().get(p));
            p.getInventory().setItem(8, setTeamItem(team));
            p.teleport(ConfigUtils.getLocation(arenaName, configField));

            p.playSound(p.getLocation(), XSound.matchXSound(plugin.getConfig().getString("sounds.start")).get().parseSound(), 1000, 1);
            p.sendTitle(plugin.getMessage("game.start-title"), plugin.getMessage("game.start-subtitle")
                    .replace("%team%", plugin.getMessage("general.team-" + team.getCode())), 0, 60, 10);

        }

        new Signs().updateSigns(arenaName);
        if (plugin.isBungee()) {
            Arena arena = new Arena((localArena.getName()), localArena.getServer(), localArena.getState(), localArena.getPlayers());
            new SocketClientSender().sendUpdateArenaPacket(arena);
        }
    }

    private ItemStack setArmorItem(Material armorType, Color color) {
        ItemStack armor = new ItemStack(armorType, 1);
        //armor = NBTEditor.set(armor, (byte) 1, "Unbreakable");
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) armor.getItemMeta();
        armorMeta.setColor(color);
        armor.setItemMeta(armorMeta);
        return armor;
    }

    private ItemStack setTeamItem(Team team) {
        ItemStack itemStack = null;
        switch (team) {
            case RED: {
                itemStack = new ItemStack(XMaterial.RED_DYE.parseItem());
                break;
            }
            case GREEN: {
                itemStack = new ItemStack(XMaterial.LIME_DYE.parseItem());
                break;
            }
            case BLUE: {
                itemStack = new ItemStack(XMaterial.BLUE_DYE.parseItem());
                break;
            }
            case YELLOW: {
                itemStack = new ItemStack(XMaterial.YELLOW_DYE.parseItem());
                break;
            }

        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(plugin.getMessage("game.team-menu-team-" + team.getCode()));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private void setArmor(Player player, Team team) {
        Color color = team.getColor();

        ItemStack helmet = setArmorItem(XMaterial.LEATHER_HELMET.parseMaterial(), color);
        ItemStack chestplate = setArmorItem(XMaterial.LEATHER_CHESTPLATE.parseMaterial(), color);
        ItemStack leggings = setArmorItem(XMaterial.LEATHER_LEGGINGS.parseMaterial(), color);
        ItemStack boots = setArmorItem(XMaterial.LEATHER_BOOTS.parseMaterial(), color);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
    }
}