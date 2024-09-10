package me.cubixor.sheepquest.game.inventories;

import me.cubixor.minigamesapi.spigot.game.arena.LocalArena;
import me.cubixor.minigamesapi.spigot.game.inventories.Menu;
import me.cubixor.minigamesapi.spigot.game.items.GameItem;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.minigamesapi.spigot.utils.Sounds;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.game.BossBarManager;
import me.cubixor.sheepquest.game.kits.KitType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class KitsMenu extends Menu {

    private final SQArena sqArena;
    private final BossBarManager bossBarManager;

    protected KitsMenu(LocalArena arena, BossBarManager bossBarManager) {
        super(arena);
        sqArena = (SQArena) arena;
        this.bossBarManager = bossBarManager;
    }

    @Override
    public Inventory create() {
        Inventory kitsInventory = Bukkit.createInventory(null, 9, Messages.get("kits.menu-name"));
        for (KitType kitType : KitType.values()) {
            GameItem kitItem = new GameItem("kits." + kitType + ".menu-icon", "kits." + kitType + "-name", "kits." + kitType + "-lore");
            kitsInventory.setItem(kitType.getId(), kitItem.getItem());
        }

        return kitsInventory;
    }

    @Override
    public void update() {
    }

    @Override
    public void handleClick(InventoryClickEvent evt, Player player) {
        KitType kitType = KitType.getById(evt.getSlot());
        if (kitType == null) return;

        if (sqArena.getPlayerKit().get(player).equals(kitType)) {
            Messages.send(player, "kits.already-selected");
            return;
        }

        if (!player.hasPermission(kitType.getPermission())) {
            Messages.send(player, "general.no-permission");
            return;
        }

        sqArena.getPlayerKit().replace(player, kitType);
        bossBarManager.addKitBossBar(player, kitType);

        Sounds.playSound("click", player);
        Messages.send(player, "kits.choose-success", "%kit%", kitType.getName());
    }
}
