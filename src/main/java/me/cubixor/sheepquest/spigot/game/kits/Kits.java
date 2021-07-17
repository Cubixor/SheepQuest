package me.cubixor.sheepquest.spigot.game.kits;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.api.Utils;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;
import org.bukkit.entity.Player;

public class Kits {

    public static boolean useKits() {
        int enabled = 0;
        for (KitType kitType : KitType.values()) {
            if (kitType.isEnabled()) {
                enabled++;
            }
        }
        return enabled > 1;
    }

    public static KitType getById(int id) {
        for (KitType kitType : KitType.values()) {
            if (kitType.getId() == id) {
                return kitType;
            }
        }
        return null;
    }

    public static Kit getByType(KitType kitType) {
        for (Kit kit : SheepQuest.getInstance().getKits()) {
            if (kitType.equals(kit.getKitType())) {
                return kit;
            }
        }
        return null;
    }

    public static Kit getPlayerKit(Player player) {
        LocalArena localArena = Utils.getLocalArena(player);
        if (localArena == null) {
            return null;
        }
        return getByType(localArena.getPlayerKit().get(player));
    }
}
