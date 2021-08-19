package me.cubixor.sheepquest.spigot.game;

import com.cryptomorin.xseries.XBlock;
import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.Utils;
import me.cubixor.sheepquest.spigot.api.VersionUtils;
import me.cubixor.sheepquest.spigot.commands.PlayCommands;
import me.cubixor.sheepquest.spigot.config.ConfigField;
import me.cubixor.sheepquest.spigot.config.ConfigUtils;
import me.cubixor.sheepquest.spigot.gameInfo.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import java.util.List;

public class Signs implements Listener {

    private final SheepQuest plugin;

    public Signs() {
        plugin = SheepQuest.getInstance();
    }

    @EventHandler
    public void signCreate(SignChangeEvent evt) {
        Sign sign = (Sign) evt.getBlock().getState();
        if (!(evt.getLine(0) != null && evt.getLine(1) != null
                && evt.getLine(0).equalsIgnoreCase("[sheepquest]")
                && evt.getPlayer().hasPermission("sheepquest.setup.signs"))) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            if (!ConfigUtils.getArenas().contains(evt.getLine(1)) && !evt.getLine(1).equalsIgnoreCase("quickjoin")) {
                return;
            }

            String arena = evt.getLine(1);

            List<String> signList = new ArrayList<>(getSignList(arena));
            signList.add(ConfigUtils.locationToString(sign.getBlock().getLocation()));
            plugin.getArenasConfig().set("signs." + arena, signList);
            plugin.saveArenas();
            plugin.getSigns().get(arena).add(sign.getLocation());

            evt.setCancelled(true);
            updateSign(sign.getLocation(), arena);
        });
    }

    @EventHandler
    public void signBreak(BlockBreakEvent evt) {
        if (!(evt.getBlock().getType().toString().contains("SIGN"))) {
            return;
        }

        Sign sign = (Sign) evt.getBlock().getState();
        String arena = arenaSign(sign);

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

        removeSign(arena, sign.getLocation());
    }

    public void removeSigns(String arena) {
        for (Location location : plugin.getSigns().get(arena)) {
            removeSignBlock(location.getBlock());
        }
        plugin.getSigns().remove(arena);
        plugin.getArenasConfig().set("signs." + arena, null);
        plugin.saveArenas();
    }

    private void removeSign(String arena, Location location) {
        plugin.getSigns().get(arena).remove(location);
        List<String> signList = new ArrayList<>(getSignList(arena));
        signList.remove(ConfigUtils.locationToString(location));
        plugin.getArenasConfig().set("signs." + arena, signList);
        plugin.saveArenas();
    }

    private void removeSignBlock(Block sign) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                Block block = getAttachedBlock(sign);
                if (sign.getType().toString().contains("SIGN")) {
                    sign.setType(Material.AIR);

                    if (plugin.getConfig().getBoolean("color-signs")) {
                        block.setType(Material.AIR);
                    }
                }
            } catch (Exception ignored) {
            }
        });
    }

    @EventHandler
    public void signClick(PlayerInteractEvent evt) {
        if (!VersionUtils.is18()) {
            if (evt.getHand() == null || !evt.getHand().equals(EquipmentSlot.HAND)) {
                return;
            }
        }

        if (evt.getClickedBlock() == null) {
            return;
        }
        if (!(evt.getClickedBlock() != null && !evt.getPlayer().isSneaking())) {
            return;
        }
        if (!evt.getClickedBlock().getType().toString().contains("SIGN")) {
            return;
        }

        Sign sign = (Sign) evt.getClickedBlock().getState();
        String arena = arenaSign(sign);

        if (arena == null) {
            return;
        }

        if (Cooldown.checkCooldown(evt.getPlayer())) {
            return;
        } else {
            Cooldown.addCooldown(evt.getPlayer());
        }

        if (!evt.getPlayer().hasPermission("sheepquest.play.signs")) {
            evt.getPlayer().sendMessage(plugin.getMessage("general.no-permission"));
            return;
        }
        evt.setCancelled(true);

        if (arena.equals("quickjoin")) {
            new PlayCommands().quickJoin(evt.getPlayer());
        } else {
            new PlayCommands().join(evt.getPlayer(), new String[]{"join", arena});
        }
    }

    public void updateSigns(String arena) {
        if (plugin.getSigns().get(arena) != null) {
            List<Location> signList = new ArrayList<>(plugin.getSigns().get(arena));
            for (Location location : signList) {
                updateSign(location, arena);
            }
        }
    }

    private void updateSign(Location location, String arenaString) {
        if (arenaString.equals("quickjoin")) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                Sign sign;
                Block block;

                try {
                    block = location.getBlock();
                    sign = (Sign) block.getState();
                    if (!block.getType().toString().contains("SIGN")) {
                        removeSign(arenaString, location);
                        return;
                    }
                } catch (Exception e) {
                    removeSign(arenaString, location);
                    return;
                }
                sign.setLine(0, plugin.getMessage("other.sign-quickjoin-first-line").replace("%count%", Integer.toString(plugin.getAllArenas().size())));
                sign.setLine(1, plugin.getMessage("other.sign-quickjoin-second-line").replace("%count%", Integer.toString(plugin.getAllArenas().size())));
                sign.setLine(2, plugin.getMessage("other.sign-quickjoin-third-line").replace("%count%", Integer.toString(plugin.getAllArenas().size())));
                sign.setLine(3, plugin.getMessage("other.sign-quickjoin-fourth-line").replace("%count%", Integer.toString(plugin.getAllArenas().size())));
                sign.update(true);
            });
            return;
        }


        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            Arena arena = plugin.getArena(arenaString);
            String max = Integer.toString(ConfigUtils.getInt(arenaString, ConfigField.MAX_PLAYERS));
            String gameState = Utils.getStringState(arena);
            String vip = ConfigUtils.getBoolean(arenaString, ConfigField.VIP) ? plugin.getMessage("general.vip-prefix") : "";

            String count;
            if (arena != null) {
                count = Integer.toString(arena.getPlayers().size());
            } else {
                count = "0";
            }
            ItemStack blockType = Utils.setGlassColor(arena);

            Bukkit.getScheduler().runTask(plugin, () -> {
                Sign sign;
                Block block;

                try {
                    block = location.getBlock();
                    sign = (Sign) block.getState();
                    if (!block.getType().toString().contains("SIGN")) {
                        removeSign(arenaString, location);
                        return;
                    }
                } catch (Exception e) {
                    removeSign(arenaString, location);
                    return;
                }

                if (plugin.getConfig().getBoolean("color-signs")) {
                    Block attachedBlock = getAttachedBlock(block);

                    attachedBlock.setType(blockType.getType());

                    BlockState state = attachedBlock.getState();
                    state.setData(blockType.getData());
                    state.update();
                }

                sign.setLine(0, plugin.getMessage("other.sign-first-line").replace("%arena%", arenaString).replace("%count%", count).replace("%max%", max).replace("%state%", gameState).replace("%?vip?%", vip));
                sign.setLine(1, plugin.getMessage("other.sign-second-line").replace("%arena%", arenaString).replace("%count%", count).replace("%max%", max).replace("%state%", gameState).replace("%?vip?%", vip));
                sign.setLine(2, plugin.getMessage("other.sign-third-line").replace("%arena%", arenaString).replace("%count%", count).replace("%max%", max).replace("%state%", gameState).replace("%?vip?%", vip));
                sign.setLine(3, plugin.getMessage("other.sign-fourth-line").replace("%arena%", arenaString).replace("%count%", count).replace("%max%", max).replace("%state%", gameState).replace("%?vip?%", vip));

                sign.update(true);
            });
        });
    }

    private Block getAttachedBlock(Block block) {
        Block attachedBlock;
        if (block.getType().toString().contains("WALL")) {
            attachedBlock = block.getRelative(XBlock.getDirection(block).getOppositeFace());
        } else {
            attachedBlock = block.getRelative(BlockFace.DOWN);
        }
        return attachedBlock;
    }

    public void loadSigns() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (plugin.getArenasConfig().getConfigurationSection("signs") == null) {
                return;
            }

            for (String arena : plugin.getArenasConfig().getConfigurationSection("signs").getKeys(false)) {
                loadArenaSigns(arena);
                updateSigns(arena);
            }
        });
    }

    public void loadArenaSigns(String arena) {
        if (plugin.getArenasConfig().get("signs." + arena) == null) {
            return;
        }

        for (String locStr : plugin.getArenasConfig().getStringList("signs." + arena)) {
            Location loc = ConfigUtils.stringToLocation(locStr);
            try {
                loc.getBlock().getState();
                plugin.getSigns().get(arena).add(loc);
            } catch (Exception ex) {
                List<String> signList = new ArrayList<>(getSignList(arena));
                signList.remove(locStr);
                plugin.getArenasConfig().set("signs." + arena, signList);
                plugin.saveArenas();

            }
        }
    }

    private String arenaSign(Sign sign) {
        for (String arena : plugin.getSigns().keySet()) {
            if (plugin.getSigns().get(arena).contains(sign.getLocation())) {
                return arena;
            }
        }
        return null;
    }

    private List<String> getSignList(String arena) {
        List<String> signList = new ArrayList<>();
        if (plugin.getArenasConfig().getList("signs." + arena) != null) {
            signList.addAll(plugin.getArenasConfig().getStringList("signs." + arena));
        }
        return signList;
    }
}
