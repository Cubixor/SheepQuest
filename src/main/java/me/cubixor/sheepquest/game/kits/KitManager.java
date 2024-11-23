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
        registerKit(KitType.STANDARD, new KitStandard(arenasRegistry, itemsRegistry));
        registerKit(KitType.ARCHER, new KitArcher(arenasRegistry, itemsRegistry));
        registerKit(KitType.ATHLETE, new KitAthlete(arenasRegistry, sheepPickupHandler, itemsRegistry));
    }

    private void registerKit(KitType kitType, Kit kit) {
        if (kitType.isEnabled()) {
            kits.put(kitType, kit);
            Bukkit.getPluginManager().registerEvents(kit, MinigamesAPI.getPlugin());
        }
    }

    public Map<KitType, Kit> getKits() {
        return kits;
    }
}
