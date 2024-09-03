package me.cubixor.sheepquest.items;

import me.cubixor.minigamesapi.spigot.game.ArenasManager;
import me.cubixor.minigamesapi.spigot.game.arena.LocalArena;
import me.cubixor.minigamesapi.spigot.game.items.ClickableItem;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class SheepItem extends ClickableItem {

    public SheepItem() {
        super("items.sheep-item", "game.sheep-item-name", "game.sheep-item-lore");
    }

    @Override
    public void handleClick(ArenasManager arenasManager, LocalArena localArena, Player player, PlayerInteractEvent evt) {

    }
}
