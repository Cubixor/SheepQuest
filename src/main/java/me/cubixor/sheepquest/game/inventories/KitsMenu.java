package me.cubixor.sheepquest.game.inventories;

import me.cubixor.minigamesapi.spigot.game.arena.LocalArena;
import me.cubixor.minigamesapi.spigot.game.inventories.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class KitsMenu extends Menu {

    protected KitsMenu(LocalArena arena) {
        super(arena);
    }

    @Override
    public Inventory create() {
        return null;
    }

    @Override
    public void update() {

    }

    @Override
    public void handleClick(InventoryClickEvent evt, Player player) {

    }
}
