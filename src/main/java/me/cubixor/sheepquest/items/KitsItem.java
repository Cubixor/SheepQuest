package me.cubixor.sheepquest.items;

import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.game.arena.LocalArena;
import me.cubixor.minigamesapi.spigot.game.items.ClickableItem;
import me.cubixor.sheepquest.game.inventories.SQMenuRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class KitsItem extends ClickableItem {

    public KitsItem() {
        super("items.kit-choose-item", "kits.item-name", "kits.item-lore");
    }

    @Override
    public void handleClick(ArenasManager arenasManager, LocalArena localArena, Player player, PlayerInteractEvent evt) {
        SQMenuRegistry menuRegistry = (SQMenuRegistry) localArena.getMenuRegistry();
        menuRegistry.getKitsMenu().open(player);
    }
}
