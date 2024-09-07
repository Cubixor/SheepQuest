package me.cubixor.sheepquest.game.kits;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;
import me.cubixor.sheepquest.game.SheepPickupHandler;
import me.cubixor.sheepquest.items.SQItemsRegistry;
import org.bukkit.Bukkit;

import java.util.EnumMap;
import java.util.Map;

public class KitManager {

    private final Map<KitType, Kit> kits = new EnumMap<>(KitType.class);

    public KitManager(ArenasRegistry arenasRegistry, SQItemsRegistry itemsRegistry, SheepPickupHandler sheepPickupHandler) {
        KitStandard kitStandard = new KitStandard(arenasRegistry, itemsRegistry);
        KitArcher kitArcher = new KitArcher(arenasRegistry, itemsRegistry);
        KitAthlete kitAthlete = new KitAthlete(arenasRegistry, sheepPickupHandler, itemsRegistry);

        kits.put(KitType.STANDARD, kitStandard);
        kits.put(KitType.ARCHER, kitArcher);
        kits.put(KitType.ATHLETE, kitAthlete);

        Bukkit.getPluginManager().registerEvents(kitStandard, MinigamesAPI.getPlugin());
        Bukkit.getPluginManager().registerEvents(kitArcher, MinigamesAPI.getPlugin());
        Bukkit.getPluginManager().registerEvents(kitAthlete, MinigamesAPI.getPlugin());
    }

    public Map<KitType, Kit> getKits() {
        return kits;
    }
}
