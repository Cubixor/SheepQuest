package me.cubixor.sheepquest.game.events;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.messages.Titles;
import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.Utils;
import me.cubixor.minigamesapi.spigot.events.GameStartEvent;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.minigamesapi.spigot.utils.Sounds;
import me.cubixor.sheepquest.arena.PlayerGameStats;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.arena.SQArenasManager;
import me.cubixor.sheepquest.arena.Team;
import me.cubixor.sheepquest.config.SQConfigField;
import me.cubixor.sheepquest.game.BossBarManager;
import me.cubixor.sheepquest.game.kits.KitArcher;
import me.cubixor.sheepquest.game.kits.KitManager;
import me.cubixor.sheepquest.game.kits.KitType;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

public class GameStartHandler implements Listener {

    private final SQArenasManager arenasManager;
    private final KitManager kitManager;
    private final BossBarManager bossBarManager;

    public GameStartHandler(SQArenasManager arenasManager, KitManager kitManager, BossBarManager bossBarManager) {
        this.arenasManager = arenasManager;
        this.kitManager = kitManager;
        this.bossBarManager = bossBarManager;
    }

    @EventHandler
    public void onGameStart(GameStartEvent evt) {
        SQArena arena = (SQArena) evt.getLocalArena();

        KitArcher kitArcher = (KitArcher) kitManager.getKits().get(KitType.ARCHER);
        if (kitArcher != null) {
            kitArcher.arrowTimer(arena);
        }

        if (MinigamesAPI.getPlugin().getConfig().getBoolean("bonus-sheep.enabled")) {
            arena.setNextBonusSheepTime();
        }

        for (Team t : arena.getTeams()) {
            arena.getPoints().put(t, 0);
        }

        List<Player> players = new ArrayList<>(arena.getPlayerTeam().keySet());
        Collections.shuffle(players);

        for (Player p : players) {
            Team team = chooseTeam(p, arena);

            arena.getPlayerGameStats().put(p, new PlayerGameStats());

            p.setInvulnerable(false);
            kitManager.getKits().get(arena.getPlayerKit().get(p)).giveKit(p);
            bossBarManager.removeKitBossBar(p);
            setItems(p, team);

            p.teleport(arenasManager.getConfigManager().getLocation(arena.getName(), SQConfigField.SPAWN, team.toString()));

            Messages.send(p, "game.start-chat");
            Sounds.playSound("start", p);
            Titles.sendTitle(p, 0, 60, 10,
                    Messages.get("game.start-title"),
                    Messages.get("game.start-subtitle", "%team%", team.getName()));

        }
    }


    private Team chooseTeam(Player p, SQArena arena) {
        Team team = arena.getPlayerTeam().get(p);

        if (team == Team.NONE) {
            LinkedHashMap<Team, Integer> teams = new LinkedHashMap<>(arena.countTeamPlayers());
            for (Team t : arena.getTeams()) {
                if (t == Team.NONE) continue;
                teams.putIfAbsent(t, 0);
            }

            Utils.sortByValueInPlace(teams);
            team = teams
                    .entrySet()
                    .stream()
                    .min(Comparator.comparingInt(Map.Entry::getValue))
                    .map(Map.Entry::getKey)
                    .get();


            arena.getPlayerTeam().put(p, team);
            bossBarManager.addTeamBossBar(p, team);
        }

        return team;
    }

    private ItemStack getArmorItem(Material armorType, Color color) {
        ItemStack armor = new ItemStack(armorType, 1);
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) armor.getItemMeta();
        armorMeta.setColor(color);
        armor.setItemMeta(armorMeta);
        return armor;
    }

    private ItemStack getTeamItem(Team team) {
        ItemStack itemStack = new ItemStack(team.getDye().parseItem());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(team.getName());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    private void setArmor(Player player, Team team) {
        Color color = team.getColor();

        ItemStack helmet = getArmorItem(XMaterial.LEATHER_HELMET.parseMaterial(), color);
        ItemStack chestPlate = getArmorItem(XMaterial.LEATHER_CHESTPLATE.parseMaterial(), color);
        ItemStack leggings = getArmorItem(XMaterial.LEATHER_LEGGINGS.parseMaterial(), color);
        ItemStack boots = getArmorItem(XMaterial.LEATHER_BOOTS.parseMaterial(), color);

        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestPlate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
    }

    private void setItems(Player p, Team team) {
        setArmor(p, team);
        p.getInventory().setItem(8, getTeamItem(team));
    }
}
