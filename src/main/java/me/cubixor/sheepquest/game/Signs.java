package me.cubixor.sheepquest.game;

import me.cubixor.sheepquest.Arena;
import me.cubixor.sheepquest.GameState;
import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.Utils;
import me.cubixor.sheepquest.commands.PlayCommands;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class Signs implements Listener {

    private final SheepQuest plugin;

    public Signs(SheepQuest s) {
        plugin = s;
    }

    @EventHandler
    public void signCreate(SignChangeEvent evt) {
        Utils utils = new Utils(plugin);
        Sign sign = (Sign) evt.getBlock().getState();
        if (!(evt.getLine(0) != null && evt.getLine(1) != null && evt.getLine(0).equalsIgnoreCase("[sheepquest]") && plugin.getArenasConfig().getConfigurationSection("Arenas").contains(evt.getLine(1)) && !utils.checkIfReady(evt.getLine(1)).containsValue(false))) {
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

        plugin.arenas.get(arena).signs.add(sign);

        evt.setCancelled(true);
        updateSigns(plugin.arenas.get(arena));

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

    private void removeSign(Arena arena, Sign sign) {
        Utils utils = new Utils(plugin);
        String arenaString = utils.getArenaString(arena);
        arena.signs.remove(sign);
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

        Utils utils = new Utils(plugin);
        new PlayCommands(plugin).join(evt.getPlayer(), new String[]{"join", utils.getArenaString(arenaSign(sign))});
        if (utils.getArena(evt.getPlayer()) != null) {
            evt.getPlayer().getInventory().setHeldItemSlot(4);
        }
    }

    public void updateSigns(Arena arena) {
        Utils utils = new Utils(plugin);
        for (Sign sign : arena.signs) {
            String arenaString = utils.getArenaString(arena);

            Block block;
            try {
                block = sign.getBlock();
            } catch (Exception e) {
                removeSign(arena, sign);
                return;
            }
            BlockData data = block.getBlockData();
            Block attachedBlock;
            if (data instanceof Directional) {
                Directional directional = (Directional) data;
                attachedBlock = block.getRelative(directional.getFacing().getOppositeFace());
            } else {
                attachedBlock = block.getRelative(0, -1, 0);
            }

            String count = Integer.toString(arena.playerTeam.keySet().size());
            String max = Integer.toString(plugin.getArenasConfig().getInt("Arenas." + arenaString + ".max-players"));

            String gameState = utils.getStringState(arena);
            if (plugin.getConfig().getBoolean("color-signs")) {
                if (!plugin.getArenasConfig().getBoolean("Arenas." + arenaString + ".active")) {
                    attachedBlock.setType(Material.matchMaterial(plugin.getConfig().getString("sign-colors.inactive")));
                } else if (arena.state.equals(GameState.WAITING)) {
                    attachedBlock.setType(Material.matchMaterial(plugin.getConfig().getString("sign-colors.waiting")));
                } else if (arena.state.equals(GameState.STARTING)) {
                    attachedBlock.setType(Material.matchMaterial(plugin.getConfig().getString("sign-colors.starting")));
                } else if (arena.state.equals(GameState.GAME)) {
                    attachedBlock.setType(Material.matchMaterial(plugin.getConfig().getString("sign-colors.ingame")));
                } else if (arena.state.equals(GameState.ENDING)) {
                    attachedBlock.setType(Material.matchMaterial(plugin.getConfig().getString("sign-colors.ending")));
                }
            }

            String vip = plugin.getConfig().getStringList("vip-arenas").contains(arenaString) ? plugin.getMessage("general.vip-prefix") : "";
            sign.setLine(0, plugin.getMessage("other.sign-first-line").replace("%arena%", arenaString).replace("%count%", count).replace("%max%", max).replace("%state%", gameState).replace("%?vip?%", vip));
            sign.setLine(1, plugin.getMessage("other.sign-second-line").replace("%arena%", arenaString).replace("%count%", count).replace("%max%", max).replace("%state%", gameState).replace("%?vip?%", vip));
            sign.setLine(2, plugin.getMessage("other.sign-third-line").replace("%arena%", arenaString).replace("%count%", count).replace("%max%", max).replace("%state%", gameState).replace("%?vip?%", vip));
            sign.setLine(3, plugin.getMessage("other.sign-fourth-line").replace("%arena%", arenaString).replace("%count%", count).replace("%max%", max).replace("%state%", gameState).replace("%?vip?%", vip));
            sign.update(true);
        }
    }

    public void loadSigns() {
        if (plugin.getArenasConfig().getConfigurationSection("Signs") == null) {
            return;
        }
        for (String arena : plugin.getArenasConfig().getConfigurationSection("Signs").getKeys(false)) {
            loadArenaSigns(arena);
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
                plugin.arenas.get(arena).signs.add(sign);
            } catch (Exception ex) {
                plugin.getArenasConfig().set("Signs." + arena + "." + signNumber, null);
            }
        }
    }

    private Arena arenaSign(Sign sign) {
        for (String s : plugin.arenas.keySet()) {
            if (plugin.arenas.get(s).signs.contains(sign)) {
                return plugin.arenas.get(s);
            }
        }
        return null;
    }
}
