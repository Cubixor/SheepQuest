package me.cubixor.sheepquest;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.commands.MainCommand;
import me.cubixor.minigamesapi.spigot.commands.MainCommandCompleter;
import me.cubixor.minigamesapi.spigot.commands.arguments.CommandArgument;
import me.cubixor.minigamesapi.spigot.config.ConfigManager;
import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;
import me.cubixor.minigamesapi.spigot.game.ChatBlocker;
import me.cubixor.minigamesapi.spigot.game.SignManager;
import me.cubixor.minigamesapi.spigot.game.inventories.MenuHandler;
import me.cubixor.minigamesapi.spigot.game.items.ItemHandler;
import me.cubixor.minigamesapi.spigot.sockets.PacketManagerSpigot;
import me.cubixor.minigamesapi.spigot.sockets.PacketSenderSpigot;
import me.cubixor.sheepquest.arena.SQArenaFactory;
import me.cubixor.sheepquest.arena.SQArenasManager;
import me.cubixor.sheepquest.arena.SQSetupChecker;
import me.cubixor.sheepquest.commands.impl.*;
import me.cubixor.sheepquest.config.SQStatsField;
import me.cubixor.sheepquest.game.BossBarManager;
import me.cubixor.sheepquest.game.SQArenaProtection;
import me.cubixor.sheepquest.game.SheepPickupHandler;
import me.cubixor.sheepquest.game.events.GameEndHandler;
import me.cubixor.sheepquest.game.events.GameJoinLeaveHandler;
import me.cubixor.sheepquest.game.events.GameResetHandler;
import me.cubixor.sheepquest.game.events.GameStartHandler;
import me.cubixor.sheepquest.game.kits.KitManager;
import me.cubixor.sheepquest.items.SQItemsRegistry;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        MinigamesAPI.INIT(this);
        PluginManager pluginManager = getServer().getPluginManager();

        ConfigManager configManager = new ConfigManager(SQStatsField.getAllFields());
        ArenasRegistry arenasRegistry = new ArenasRegistry();
        SQItemsRegistry itemsRegistry = new SQItemsRegistry();
        PacketSenderSpigot packetSender = new PacketSenderSpigot(configManager.getConnectionConfig());
        SignManager signManager = new SignManager(configManager.getArenasConfigManager(), arenasRegistry);
        BossBarManager bossBarManager = new BossBarManager();
        SQArenaFactory arenaFactory = new SQArenaFactory(bossBarManager);
        SQArenasManager arenasManager = new SQArenasManager(arenasRegistry, configManager.getArenasConfigManager(), signManager, packetSender, configManager.getStatsManager(), itemsRegistry, arenaFactory);
        PacketManagerSpigot packetManager = new PacketManagerSpigot(arenasManager, packetSender);
        SQArenaProtection arenaProtection = new SQArenaProtection(arenasManager);
        ItemHandler itemHandler = new ItemHandler(arenasManager, itemsRegistry);
        MenuHandler menuHandler = new MenuHandler(arenasRegistry);
        ChatBlocker chatBlocker = new ChatBlocker(arenasRegistry);
        SQSetupChecker arenaSetupChecker = new SQSetupChecker(configManager.getArenasConfigManager());

        List<CommandArgument> args = Stream.concat(
                MainCommand.getCommonArguments(arenasManager, arenaSetupChecker, configManager.getStatsManager()).stream(),
                Stream.of(new ArgListTeams(arenasManager),
                        new ArgAddTeam(arenasManager),
                        new ArgRemoveTeam(arenasManager),
                        new ArgSetSheepSpawn(arenasManager),
                        new ArgSetSpawn(arenasManager),
                        new ArgSetArea(arenasManager)
                )
        ).collect(Collectors.toList());

        MainCommand mainCommand = new MainCommand(args);
        MainCommandCompleter mainCommandCompleter = new MainCommandCompleter(args);
        getServer().getPluginCommand(getName()).setExecutor(mainCommand);
        getServer().getPluginCommand(getName()).setTabCompleter(mainCommandCompleter);

        SheepPickupHandler sheepPickupHandler = new SheepPickupHandler(arenasRegistry, itemsRegistry);
        KitManager kitManager = new KitManager(arenasRegistry, sheepPickupHandler);

        GameJoinLeaveHandler gameJoinLeaveHandler = new GameJoinLeaveHandler(itemsRegistry, bossBarManager);
        GameStartHandler gameStartHandler = new GameStartHandler(arenasManager, itemsRegistry, kitManager);
        GameEndHandler gameEndHandler = new GameEndHandler(configManager.getStatsManager());
        GameResetHandler gameResetHandler = new GameResetHandler();

        pluginManager.registerEvents(sheepPickupHandler, this);
        pluginManager.registerEvents(gameJoinLeaveHandler, this);
        pluginManager.registerEvents(gameStartHandler, this);
        pluginManager.registerEvents(gameEndHandler, this);
        pluginManager.registerEvents(gameResetHandler, this);

        MinigamesAPI.registerPAPI(arenasRegistry, configManager.getStatsManager());
    }
}
