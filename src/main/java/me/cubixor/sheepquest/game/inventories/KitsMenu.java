package me.cubixor.sheepquest.game.inventories;

import me.cubixor.minigamesapi.spigot.game.arena.LocalArena;
import me.cubixor.minigamesapi.spigot.game.inventories.Menu;
import me.cubixor.minigamesapi.spigot.game.items.GameItem;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import me.cubixor.minigamesapi.spigot.utils.Permissions;
import me.cubixor.minigamesapi.spigot.utils.Sounds;
import me.cubixor.sheepquest.arena.SQArena;
import me.cubixor.sheepquest.game.BossBarManager;
import me.cubixor.sheepquest.game.kits.KitType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class KitsMenu extends Menu {

    private final SQArena sqArena;
    private final BossBarManager bossBarManager;
    private final Map<KitType, Integer> kitSlots = new HashMap<>();

    protected KitsMenu(LocalArena arena, BossBarManager bossBarManager) {
        super(arena);
        sqArena = (SQArena) arena;
        this.bossBarManager = bossBarManager;
    }

    @Override
    public Inventory create() {
        Inventory kitsInventory = Bukkit.createInventory(null, 9, Messages.get("kits.menu-name"));
        int slot = 0;
        for (KitType kitType : KitType.getEnabled().collect(Collectors.toList())) {
            GameItem kitItem = new GameItem("kits." + kitType + ".menu-icon", "kits." + kitType + "-name", "kits." + kitType + "-lore");

            kitsInventory.setItem(slot, kitItem.getItem());
            kitSlots.put(kitType, slot);
            slot++;
        }

        return kitsInventory;
    }

    @Override
    public void update() {
    }

    @Override
    public void handleClick(InventoryClickEvent evt, Player player) {
        KitType kitType = getKitTypeBySlot(evt.getSlot());
        if (kitType == null) return;

        if (sqArena.getPlayerKit().get(player).equals(kitType)) {
            Messages.send(player, "kits.already-selected");
            return;
        }

        if (!Permissions.has(player, kitType.getPermission())) {
            Messages.send(player, "general.no-permission");
            return;
        }

        sqArena.getPlayerKit().replace(player, kitType);
        bossBarManager.addKitBossBar(player, kitType);

        Sounds.playSound("click", player);
        Messages.send(player, "kits.choose-success", "%kit%", kitType.getName());

        sqArena.getScoreboardManager().updateScoreboard();
    }

    private KitType getKitTypeBySlot(int slot) {
        return kitSlots
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == slot)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }
}
