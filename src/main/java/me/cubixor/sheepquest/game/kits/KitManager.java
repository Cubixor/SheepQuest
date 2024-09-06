package me.cubixor.sheepquest.game.kits;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;
import me.cubixor.sheepquest.game.SheepPickupHandler;
import org.bukkit.Bukkit;

import java.util.EnumMap;
import java.util.Map;

public class KitManager {

    private final Map<KitType, Kit> kits = new EnumMap<>(KitType.class);

    public KitManager(ArenasRegistry arenasRegistry, SheepPickupHandler sheepPickupHandler) {
        KitStandard kitStandard = new KitStandard(arenasRegistry);
        KitArcher kitArcher = new KitArcher(arenasRegistry);
        KitAthlete kitAthlete = new KitAthlete(arenasRegistry, sheepPickupHandler);

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
