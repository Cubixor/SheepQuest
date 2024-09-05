package me.cubixor.sheepquest.game.kits;

import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;

import java.util.HashMap;
import java.util.Map;

public class KitManager {

    private final Map<KitType, Kit> kits = new HashMap<>();

    public KitManager(ArenasRegistry arenasRegistry) {

        for (KitType kitType : KitType.values()) {
            kits.put(kitType, kitType.construct(arenasRegistry));
        }
    }

    public Map<KitType, Kit> getKits() {
        return kits;
    }
}
