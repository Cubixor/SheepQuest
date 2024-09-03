package me.cubixor.sheepquest.arena;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.minigamesapi.spigot.utils.Messages;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.boss.BarColor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Team {
    RED(ChatColor.RED, DyeColor.RED, BarColor.RED, XMaterial.RED_WOOL, XMaterial.RED_BANNER, XMaterial.RED_DYE),
    GREEN(ChatColor.GREEN, DyeColor.LIME, BarColor.GREEN, XMaterial.LIME_WOOL, XMaterial.LIME_BANNER, XMaterial.LIME_DYE),
    BLUE(ChatColor.BLUE, DyeColor.BLUE, BarColor.BLUE, XMaterial.BLUE_WOOL, XMaterial.BLUE_BANNER, XMaterial.BLUE_DYE),
    YELLOW(ChatColor.YELLOW, DyeColor.YELLOW, BarColor.YELLOW, XMaterial.YELLOW_WOOL, XMaterial.YELLOW_BANNER, XMaterial.YELLOW_DYE),
    ORANGE(ChatColor.GOLD, DyeColor.ORANGE, BarColor.YELLOW, XMaterial.ORANGE_WOOL, XMaterial.ORANGE_BANNER, XMaterial.ORANGE_DYE),
    DARK_GREEN(ChatColor.DARK_GREEN, DyeColor.GREEN, BarColor.GREEN, XMaterial.GREEN_WOOL, XMaterial.GREEN_BANNER, XMaterial.GREEN_DYE),
    CYAN(ChatColor.DARK_AQUA, DyeColor.CYAN, BarColor.BLUE, XMaterial.CYAN_WOOL, XMaterial.CYAN_BANNER, XMaterial.CYAN_DYE),
    LIGHT_BLUE(ChatColor.AQUA, DyeColor.LIGHT_BLUE, BarColor.BLUE, XMaterial.LIGHT_BLUE_WOOL, XMaterial.LIGHT_BLUE_BANNER, XMaterial.LIGHT_BLUE_DYE),
    PINK(ChatColor.LIGHT_PURPLE, DyeColor.PINK, BarColor.PINK, XMaterial.PINK_WOOL, XMaterial.PINK_BANNER, XMaterial.PINK_DYE),
    PURPLE(ChatColor.DARK_PURPLE, DyeColor.PURPLE, BarColor.PURPLE, XMaterial.PURPLE_WOOL, XMaterial.PURPLE_BANNER, XMaterial.PURPLE_DYE),
    GRAY(ChatColor.DARK_GRAY, DyeColor.GRAY, BarColor.WHITE, XMaterial.GRAY_WOOL, XMaterial.GRAY_BANNER, XMaterial.GRAY_DYE),
    BLACK(ChatColor.BLACK, DyeColor.BLACK, BarColor.WHITE, XMaterial.BLACK_WOOL, XMaterial.BLACK_BANNER, XMaterial.BLACK_DYE),
    NONE(ChatColor.GRAY, DyeColor.WHITE, BarColor.WHITE, XMaterial.WHITE_WOOL, XMaterial.WHITE_BANNER, XMaterial.WHITE_DYE);

    private static final List<Team> allTeams = Arrays.stream(Team.values()).filter(t -> !t.equals(NONE)).collect(Collectors.toList());

    private final ChatColor chatColor;
    private final DyeColor dyeColor;
    private final BarColor barColor;
    private final XMaterial wool;
    private final XMaterial banner;
    private final XMaterial dye;

    Team(ChatColor chatColor, DyeColor dyeColor, BarColor barColor, XMaterial wool, XMaterial banner, XMaterial dye) {
        this.chatColor = chatColor;
        this.dyeColor = dyeColor;
        this.barColor = barColor;
        this.wool = wool;
        this.banner = banner;
        this.dye = dye;
    }

    public static List<Team> getAll() {
        return allTeams;
    }

    public String getCode() {
        return this.toString().toLowerCase();
    }

    public String getName() {
        return Messages.get("general.team-" + getCode());
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }

    public BarColor getBarColor() {
        return barColor;
    }

    public XMaterial getWool() {
        return wool;
    }

    public XMaterial getBanner() {
        return banner;
    }

    public XMaterial getDye() {
        return dye;
    }

    public Color getColor() {
        return getDyeColor().getColor();
    }
}

