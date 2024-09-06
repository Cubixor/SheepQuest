package me.cubixor.sheepquest.game.kits;

import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;
import me.cubixor.sheepquest.game.SheepPickupHandler;

import java.util.HashMap;
import java.util.Map;

public class KitManager {

    private final Map<KitType, Kit> kits = new HashMap<>();

    public KitManager(ArenasRegistry arenasRegistry, SheepPickupHandler sheepPickupHandler) {
        kits.put(KitType.STANDARD, new KitStandard(arenasRegistry));
        kits.put(KitType.ARCHER, new KitArcher(arenasRegistry));
        kits.put(KitType.ATHLETE, new KitAthlete(arenasRegistry, sheepPickupHandler));
    }

    public Map<KitType, Kit> getKits() {
        return kits;
    }
}
