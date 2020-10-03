package me.cubixor.sheepquest.game;

import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.api.Utils;
import me.cubixor.sheepquest.commands.PlayCommands;
import me.cubixor.sheepquest.gameInfo.Arena;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Signs implements Listener {

    private final SheepQuest plugin;

    public Signs() {
        plugin = SheepQuest.getInstance();
    }

    @EventHandler
    public void signCreate(SignChangeEvent evt) {
        Sign sign = (Sign) evt.getBlock().getState();
        if (!(evt.getLine(0) != null && evt.getLine(1) != null && evt.getLine(0).equalsIgnoreCase("[sheepquest]") && plugin.getArenasConfig().getConfigurationSection("Arenas").contains(evt.getLine(1)) && !Utils.checkIfReady(evt.getLine(1)).containsValue(false))) {
            return;
        }
        if (!evt.getPlayer().hasPermission("sheepquest.setup.signs")) {
            return;
        }

        String arena = evt.getLine(1);

        int count = 0;
        while (true) {
            if (plugin.getArenasConfig().get("Signs." + arena + "." + count) == null) {
                plugin.getArenasConfig().set("Signs." + arena + "." + count, sign.getBlock().getLocation());
                plugin.saveArenas();
                break;
            } else {
                count++;
            }
        }

        plugin.getArenas().get(arena).getSigns().add(sign);

        evt.setCancelled(true);
        updateSign(sign, plugin.getArenas().get(arena));

    }

    @EventHandler
    public void signBreak(BlockBreakEvent evt) {
        if (!evt.getBlock().getType().toString().contains("SIGN")) {
            return;
        }

        Sign sign = (Sign) evt.getBlock().getState();
        Arena arena = arenaSign(sign);

        if (arena == null) {
            return;
        }
        if (!evt.getPlayer().isSneaking()) {
            evt.setCancelled(true);
            return;
        }
        if (!evt.getPlayer().hasPermission("sheepquest.setup.signs")) {
            evt.getPlayer().sendMessage(plugin.getMessage("general.no-permission"));
            evt.setCancelled(true);
            return;
        }

        removeSign(arena, sign);

    }

    public void removeSigns(Arena arena) {
        String arenaString = Utils.getArenaString(arena);
        arena.setSigns(new ArrayList<>());
        plugin.getArenasConfig().set("Signs." + arenaString, null);
        plugin.saveArenas();
    }

    private void removeSign(Arena arena, Sign sign) {
        String arenaString = Utils.getArenaString(arena);
        arena.getSigns().remove(sign);
        int count = 0;
        while (true) {
            if (plugin.getArenasConfig().get("Signs." + arenaString + "." + count).equals(sign.getLocation())) {
                plugin.getArenasConfig().set("Signs." + arenaString + "." + count, null);
                plugin.saveArenas();
                break;
            } else {
                count++;
            }
        }
    }

    @EventHandler
    public void signClick(PlayerInteractEvent evt) {
        if (!(evt.getHand().equals(EquipmentSlot.HAND) && evt.getClickedBlock() != null)) {
            return;
        }
        if (!(evt.getClickedBlock() != null && !evt.getPlayer().isSneaking())) {
            return;
        }
        if (!evt.getClickedBlock().getType().toString().contains("SIGN")) {
            return;
        }

        Sign sign = (Sign) evt.getClickedBlock().getState();

        if (arenaSign(sign) == null) {
            return;
        }
        if (!evt.getPlayer().hasPermission("sheepquest.play.signs")) {
            evt.getPlayer().sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        evt.setCancelled(true);

        new PlayCommands().join(evt.getPlayer(), new String[]{"join", Utils.getArenaString(arenaSign(sign))});
        if (Utils.getArena(evt.getPlayer()) != null) {
            evt.getPlayer().getInventory().setHeldItemSlot(4);
        }
    }

    public void updateSigns(Arena arena) {
        for (Sign sign : arena.getSigns()) {
            updateSign(sign, arena);
        }
    }

    public void updateSign(Sign sign, Arena arena) {
        String arenaString = Utils.getArenaString(arena);
        Block block;
        Block attachedBlock;
        try {
            block = sign.getBlock();
            attachedBlock = block.getRelative(((org.bukkit.material.Sign) sign.getBlock().getState().getData()).getAttachedFace());
        } catch (Exception e) {
            removeSign(arena, sign);
            return;
        }


        String count = Integer.toString(arena.getPlayers().keySet().size());
        String max = Integer.toString(plugin.getArenasConfig().getInt("Arenas." + arenaString + ".max-players"));

        String gameState = Utils.getStringState(arena);
        if (plugin.getConfig().getBoolean("color-signs")) {
            ItemStack blockType = Utils.setGlassColor(arena);

            attachedBlock.setType(blockType.getType());
            BlockState state = attachedBlock.getState();

            state.setData(blockType.getData());
            state.update();

        }

        String vip = plugin.getConfig().getStringList("vip-arenas").contains(arenaString) ? plugin.getMessage("general.vip-prefix") : "";
        sign.setLine(0, plugin.getMessage("other.sign-first-line").replace("%arena%", arenaString).replace("%count%", count).replace("%max%", max).replace("%state%", gameState).replace("%?vip?%", vip));
        sign.setLine(1, plugin.getMessage("other.sign-second-line").replace("%arena%", arenaString).replace("%count%", count).replace("%max%", max).replace("%state%", gameState).replace("%?vip?%", vip));
        sign.setLine(2, plugin.getMessage("other.sign-third-line").replace("%arena%", arenaString).replace("%count%", count).replace("%max%", max).replace("%state%", gameState).replace("%?vip?%", vip));
        sign.setLine(3, plugin.getMessage("other.sign-fourth-line").replace("%arena%", arenaString).replace("%count%", count).replace("%max%", max).replace("%state%", gameState).replace("%?vip?%", vip));


        sign.update(true);

    }

    public void loadSigns() {
        if (plugin.getArenasConfig().getConfigurationSection("Signs") == null) {
            return;
        }
        for (String arena : plugin.getArenasConfig().getConfigurationSection("Arenas").getKeys(false)) {
            loadArenaSigns(arena);
            updateSigns(plugin.getArenas().get(arena));
        }
    }

    public void loadArenaSigns(String arena) {
        if (plugin.getArenasConfig().getConfigurationSection("Signs." + arena) == null) {
            return;
        }
        for (String signNumber : plugin.getArenasConfig().getConfigurationSection("Signs." + arena).getKeys(false)) {
            Location loc = (Location) plugin.getArenasConfig().get("Signs." + arena + "." + signNumber);
            try {
                Sign sign = (Sign) loc.getBlock().getState();
                plugin.getArenas().get(arena).getSigns().add(sign);
            } catch (Exception ex) {
                plugin.getArenasConfig().set("Signs." + arena + "." + signNumber, null);
            }
        }
    }

    private Arena arenaSign(Sign sign) {
        for (String s : plugin.getArenas().keySet()) {
            if (plugin.getArenas().get(s).getSigns().contains(sign)) {
                return plugin.getArenas().get(s);
            }
        }
        return null;
    }
}
