package me.cubixor.sheepquest;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.commands.LeaveCommand;
import me.cubixor.minigamesapi.spigot.commands.MainCommand;
import me.cubixor.minigamesapi.spigot.commands.MainCommandCompleter;
import me.cubixor.minigamesapi.spigot.commands.arguments.CommandArgument;
import me.cubixor.minigamesapi.spigot.commands.arguments.impl.setup.ArgSetupWand;
import me.cubixor.minigamesapi.spigot.config.ConfigManager;
import me.cubixor.minigamesapi.spigot.game.*;
import me.cubixor.minigamesapi.spigot.game.inventories.GlobalMenuRegistry;
import me.cubixor.minigamesapi.spigot.game.inventories.MenuHandler;
import me.cubixor.minigamesapi.spigot.game.items.ItemHandler;
import me.cubixor.minigamesapi.spigot.integrations.Telemetry;
import me.cubixor.minigamesapi.spigot.integrations.Updater;
import me.cubixor.minigamesapi.spigot.sockets.PacketManagerSpigot;
import me.cubixor.minigamesapi.spigot.sockets.PacketSenderSpigot;
import me.cubixor.sheepquest.arena.SQArenaFactory;
import me.cubixor.sheepquest.arena.SQArenasManager;
import me.cubixor.sheepquest.arena.SQSetupChecker;
import me.cubixor.sheepquest.commands.impl.*;
import me.cubixor.sheepquest.config.SQStatsField;
import me.cubixor.sheepquest.game.BossBarManager;
import me.cubixor.sheepquest.game.SQArenaProtection;
import me.cubixor.sheepquest.game.SheepPathfinder;
import me.cubixor.sheepquest.game.SheepPickupHandler;
import me.cubixor.sheepquest.game.events.*;
import me.cubixor.sheepquest.game.kits.KitManager;
import me.cubixor.sheepquest.items.SQItemsRegistry;
import me.cubixor.sheepquest.utils.ConfigUpdater;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main extends JavaPlugin {

    private SQArenasManager arenasManager;

    @Override
    public void onEnable() {
        MinigamesAPI.INIT(this);
        PluginManager pluginManager = getServer().getPluginManager();
        Updater updater = new Updater(this, 83005);
        updater.runUpdaterTask();

        ConfigUpdater configUpdater = new ConfigUpdater();
        configUpdater.updateTo2(getConfig());
        ConfigManager configManager = new ConfigManager(SQStatsField.getAllFields(), new String[]{"en", "pl", "ru", "zh"});
        configUpdater.updateArenasTo2(configManager.getArenasConfigManager().getArenasConfig());

        ArenasRegistry arenasRegistry = new ArenasRegistry();
        SQItemsRegistry itemsRegistry = new SQItemsRegistry();
        PacketSenderSpigot packetSender = new PacketSenderSpigot(configManager.getConnectionConfig());
        SignManager signManager = new SignManager(configManager.getArenasConfigManager(), arenasRegistry, itemsRegistry);
        BossBarManager bossBarManager = new BossBarManager();
        SQArenaFactory arenaFactory = new SQArenaFactory(bossBarManager);
        arenasManager = new SQArenasManager(arenasRegistry, configManager.getArenasConfigManager(), signManager, packetSender, configManager.getStatsManager(), itemsRegistry, arenaFactory);
        PacketManagerSpigot packetManager = new PacketManagerSpigot(arenasManager, packetSender);
        SQArenaProtection arenaProtection = new SQArenaProtection(arenasManager);
        ItemHandler itemHandler = new ItemHandler(arenasManager, itemsRegistry);
        GlobalMenuRegistry globalMenuRegistry = new GlobalMenuRegistry(arenasManager, itemsRegistry);
        MenuHandler menuHandler = new MenuHandler(arenasRegistry, globalMenuRegistry);
        ChatBlocker chatBlocker = new ChatBlocker(arenasRegistry);
        SimpleBungeeMode simpleBungeeMode = new SimpleBungeeMode(arenasManager);
        WaitingTips waitingTips = new WaitingTips(arenasRegistry);
        SQSetupChecker arenaSetupChecker = new SQSetupChecker(configManager.getArenasConfigManager());

        List<CommandArgument> args = Stream.concat(
                MainCommand.getCommonArguments(arenasManager, arenaSetupChecker, configManager.getStatsManager(), globalMenuRegistry).stream(),
                Stream.of(new ArgSetupWand(itemsRegistry),
                        new ArgListTeams(arenasManager),
                        new ArgAddTeam(arenasManager),
                        new ArgRemoveTeam(arenasManager),
                        new ArgSetSheepSpawn(arenasManager),
                        new ArgSetSpawn(arenasManager),
                        new ArgSetArea(arenasManager),
                        new ArgNotImplemented("statsmenu", "play.stats.menu"),
                        new ArgNotImplemented("playersmenu", "staff.menu"),
                        new ArgNotImplemented("staffmenu", "staff.menu"),
                        new ArgNotImplemented("setupmenu", "setup.menu"),
                        new ArgNotImplemented("reload", "setup.reload")
                )
        ).collect(Collectors.toList());

        MainCommand mainCommand = new MainCommand(args);
        MainCommandCompleter mainCommandCompleter = new MainCommandCompleter(args);
        LeaveCommand leaveCommand = new LeaveCommand();

        SheepPathfinder sheepPathfinder = new SheepPathfinder();
        SheepPickupHandler sheepPickupHandler = new SheepPickupHandler(arenasManager, itemsRegistry, sheepPathfinder);
        KitManager kitManager = new KitManager(arenasRegistry, itemsRegistry, sheepPickupHandler);

        GameJoinLeaveHandler gameJoinLeaveHandler = new GameJoinLeaveHandler(itemsRegistry, bossBarManager);
        GameStartHandler gameStartHandler = new GameStartHandler(arenasManager, kitManager, bossBarManager);
        GameEndHandler gameEndHandler = new GameEndHandler(configManager.getStatsManager(), sheepPickupHandler);
        GameResetHandler gameResetHandler = new GameResetHandler();
        SheepSpawner sheepSpawner = new SheepSpawner(arenasManager.getConfigManager(), sheepPathfinder);
        ScoreboardFormatter scoreboardFormatter = new ScoreboardFormatter();
        DamageHandler damageHandler = new DamageHandler(arenasManager, kitManager, sheepPickupHandler);
        ChatManager chatManager = new ChatManager();

        pluginManager.registerEvents(sheepPickupHandler, this);
        pluginManager.registerEvents(gameJoinLeaveHandler, this);
        pluginManager.registerEvents(gameStartHandler, this);
        pluginManager.registerEvents(gameEndHandler, this);
        pluginManager.registerEvents(gameResetHandler, this);
        pluginManager.registerEvents(sheepSpawner, this);
        pluginManager.registerEvents(scoreboardFormatter, this);
        pluginManager.registerEvents(damageHandler, this);
        pluginManager.registerEvents(chatManager, this);

        PlaceholderManager placeholderManager = new PlaceholderManager(arenasRegistry);
        MinigamesAPI.registerPAPI(arenasRegistry, configManager.getStatsManager(), placeholderManager);

        Telemetry telemetry = new Telemetry();
        telemetry.runMetrics(arenasRegistry, 9022);
    }

    @Override
    public void onDisable() {
        MinigamesAPI.disable(arenasManager);
    }
}
