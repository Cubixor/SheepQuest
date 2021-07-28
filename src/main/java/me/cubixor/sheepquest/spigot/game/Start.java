package me.cubixor.sheepquest.spigot.game;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.messages.ActionBar;
import com.cryptomorin.xseries.messages.Titles;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.api.Sounds;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.game.events.SpecialEvents;
import me.cubixor.sheepquest.spigot.game.kits.KitArcher;
import me.cubixor.sheepquest.spigot.game.kits.KitType;
import me.cubixor.sheepquest.spigot.game.kits.Kits;
import me.cubixor.sheepquest.spigot.gameInfo.*;
import me.cubixor.sheepquest.spigot.socket.SocketClientSender;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

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
        ((KitArcher) Kits.getByType(KitType.ARCHER)).arrowTimer(localArena);

        for (Team t : ConfigUtils.getTeamList(arenaName)) {
            localArena.getPoints().put(t, 0);
        }

        GameTimer gameTimer = new GameTimer();
        gameTimer.gameTime(arenaName);
        gameTimer.spawnSheep(localArena);

        SpecialEvents specialEvents = new SpecialEvents();
        specialEvents.setupSpecialEvents(arenaName);

        for (Player p : localArena.getPlayerTeam().keySet()) {

            p.getInventory().setItem(plugin.getItems().getSheepItemSlot(), plugin.getItems().getSheepItem());
            Kits.getPlayerKit(p).giveKit(p);
            p.setExp(0);
            p.setLevel(0);
            plugin.getPlayerInfo().get(p).getTipTask().cancel();
            Utils.removeTeamBossBars(p, localArena);
            ActionBar.clearActionBar(p);
            localArena.getPlayerStats().put(p, new PlayerGameStats());


            Team team = localArena.getPlayerTeam().get(p);

            if (team.equals(Team.NONE)) {
                LinkedHashMap<Team, Integer> teamSorted = new LinkedHashMap<>();
                Utils.getTeamPlayers(localArena).entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue())
                        .forEachOrdered(x -> teamSorted.put(x.getKey(), x.getValue()));
                team = (new ArrayList<>(teamSorted.keySet())).get(0);

                localArena.getPlayerTeam().replace(p, team);
            }

            setArmor(p, localArena.getPlayerTeam().get(p));
            p.getInventory().setItem(8, setTeamItem(team));
            p.teleport(ConfigUtils.getSpawn(arenaName, team));

            Sounds.playSound(p, p.getLocation(), "start");
            Titles.sendTitle(p, 0, 60, 10, plugin.getMessage("game.start-title"), plugin.getMessage("game.start-subtitle")
                    .replace("%team%", team.getName()));

        }

        new Signs().updateSigns(arenaName);
        if (plugin.isBungee()) {
            Arena arena = new Arena((localArena.getName()), localArena.getServer(), localArena.getState(), localArena.getPlayers());
            new SocketClientSender().sendUpdateArenaPacket(arena);
        }
    }

    private ItemStack setArmorItem(Material armorType, Color color) {
        ItemStack armor = new ItemStack(armorType, 1);
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) armor.getItemMeta();
        armorMeta.setColor(color);
        armor.setItemMeta(armorMeta);
        return armor;
    }

    private ItemStack setTeamItem(Team team) {
        ItemStack itemStack = new ItemStack(team.getTeamItem());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(team.getName());
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