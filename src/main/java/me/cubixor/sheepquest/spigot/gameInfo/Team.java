package me.cubixor.sheepquest.spigot.gameInfo;

import com.cryptomorin.xseries.XMaterial;
import me.cubixor.sheepquest.spigot.SheepQuest;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.boss.BarColor;
import org.bukkit.inventory.ItemStack;

public enum Team {
    RED {
        @Override
        public DyeColor getDyeColor() {
            return DyeColor.RED;
        }

        @Override
        public BarColor getBarColor() {
            return BarColor.RED;
        }

        @Override
        public ItemStack getWool() {
            return XMaterial.RED_WOOL.parseItem();
        }

        @Override
        public ItemStack getBanner() {
            return XMaterial.RED_BANNER.parseItem();
        }

        @Override
        public ItemStack getTeamItem() {
            return XMaterial.RED_DYE.parseItem();
        }

        @Override
        public ChatColor getChatColor() {
            return ChatColor.RED;
        }
    }, GREEN {
        @Override
        public DyeColor getDyeColor() {
            return DyeColor.LIME;
        }

        @Override
        public BarColor getBarColor() {
            return BarColor.GREEN;
        }

        @Override
        public ItemStack getWool() {
            return XMaterial.LIME_WOOL.parseItem();
        }

        @Override
        public ItemStack getBanner() {
            return XMaterial.LIME_BANNER.parseItem();
        }

        @Override
        public ItemStack getTeamItem() {
            return XMaterial.LIME_DYE.parseItem();
        }

        @Override
        public ChatColor getChatColor() {
            return ChatColor.GREEN;
        }
    }, BLUE {
        @Override
        public DyeColor getDyeColor() {
            return DyeColor.BLUE;
        }

        @Override
        public BarColor getBarColor() {
            return BarColor.BLUE;
        }

        @Override
        public ItemStack getWool() {
            return XMaterial.BLUE_WOOL.parseItem();
        }

        @Override
        public ItemStack getBanner() {
            return XMaterial.BLUE_BANNER.parseItem();
        }

        @Override
        public ItemStack getTeamItem() {
            return XMaterial.BLUE_DYE.parseItem();
        }

        @Override
        public ChatColor getChatColor() {
            return ChatColor.BLUE;
        }
    }, YELLOW {
        @Override
        public DyeColor getDyeColor() {
            return DyeColor.YELLOW;
        }

        @Override
        public BarColor getBarColor() {
            return BarColor.YELLOW;
        }

        @Override
        public ItemStack getWool() {
            return XMaterial.YELLOW_WOOL.parseItem();
        }

        @Override
        public ItemStack getBanner() {
            return XMaterial.YELLOW_BANNER.parseItem();
        }

        @Override
        public ItemStack getTeamItem() {
            return XMaterial.YELLOW_DYE.parseItem();
        }

        @Override
        public ChatColor getChatColor() {
            return ChatColor.YELLOW;
        }
    }, ORANGE {
        @Override
        public DyeColor getDyeColor() {
            return DyeColor.ORANGE;
        }

        @Override
        public BarColor getBarColor() {
            return BarColor.YELLOW;
        }

        @Override
        public ItemStack getWool() {
            return XMaterial.ORANGE_WOOL.parseItem();
        }

        @Override
        public ItemStack getBanner() {
            return XMaterial.ORANGE_BANNER.parseItem();
        }

        @Override
        public ItemStack getTeamItem() {
            return XMaterial.ORANGE_DYE.parseItem();
        }

        @Override
        public ChatColor getChatColor() {
            return ChatColor.GOLD;
        }
    }, DARK_GREEN {
        @Override
        public DyeColor getDyeColor() {
            return DyeColor.GREEN;
        }

        @Override
        public BarColor getBarColor() {
            return BarColor.GREEN;
        }

        @Override
        public ItemStack getWool() {
            return XMaterial.GREEN_WOOL.parseItem();
        }

        @Override
        public ItemStack getBanner() {
            return XMaterial.GREEN_BANNER.parseItem();
        }

        @Override
        public ItemStack getTeamItem() {
            return XMaterial.GREEN_DYE.parseItem();
        }

        @Override
        public ChatColor getChatColor() {
            return ChatColor.DARK_GREEN;
        }
    }, CYAN {
        @Override
        public DyeColor getDyeColor() {
            return DyeColor.CYAN;
        }

        @Override
        public BarColor getBarColor() {
            return BarColor.BLUE;
        }

        @Override
        public ItemStack getWool() {
            return XMaterial.CYAN_WOOL.parseItem();
        }

        @Override
        public ItemStack getBanner() {
            return XMaterial.CYAN_BANNER.parseItem();
        }

        @Override
        public ItemStack getTeamItem() {
            return XMaterial.CYAN_DYE.parseItem();
        }

        @Override
        public ChatColor getChatColor() {
            return ChatColor.DARK_AQUA;
        }
    }, LIGHT_BLUE {
        @Override
        public DyeColor getDyeColor() {
            return DyeColor.LIGHT_BLUE;
        }

        @Override
        public BarColor getBarColor() {
            return BarColor.BLUE;
        }

        @Override
        public ItemStack getWool() {
            return XMaterial.LIGHT_BLUE_WOOL.parseItem();
        }

        @Override
        public ItemStack getBanner() {
            return XMaterial.LIGHT_BLUE_BANNER.parseItem();
        }

        @Override
        public ItemStack getTeamItem() {
            return XMaterial.LIGHT_BLUE_DYE.parseItem();
        }

        @Override
        public ChatColor getChatColor() {
            return ChatColor.AQUA;
        }
    }, PINK {
        @Override
        public DyeColor getDyeColor() {
            return DyeColor.PINK;
        }

        @Override
        public BarColor getBarColor() {
            return BarColor.PINK;
        }

        @Override
        public ItemStack getWool() {
            return XMaterial.PINK_WOOL.parseItem();
        }

        @Override
        public ItemStack getBanner() {
            return XMaterial.PINK_BANNER.parseItem();
        }

        @Override
        public ItemStack getTeamItem() {
            return XMaterial.PINK_DYE.parseItem();
        }

        @Override
        public ChatColor getChatColor() {
            return ChatColor.LIGHT_PURPLE;
        }
    }, PURPLE {
        @Override
        public DyeColor getDyeColor() {
            return DyeColor.PURPLE;
        }

        @Override
        public BarColor getBarColor() {
            return BarColor.PURPLE;
        }

        @Override
        public ItemStack getWool() {
            return XMaterial.PURPLE_WOOL.parseItem();
        }

        @Override
        public ItemStack getBanner() {
            return XMaterial.PURPLE_BANNER.parseItem();
        }

        @Override
        public ItemStack getTeamItem() {
            return XMaterial.PURPLE_DYE.parseItem();
        }

        @Override
        public ChatColor getChatColor() {
            return ChatColor.DARK_PURPLE;
        }
    }, GRAY {
        @Override
        public DyeColor getDyeColor() {
            return DyeColor.GRAY;
        }

        @Override
        public BarColor getBarColor() {
            return BarColor.WHITE;
        }

        @Override
        public ItemStack getWool() {
            return XMaterial.GRAY_WOOL.parseItem();
        }

        @Override
        public ItemStack getBanner() {
            return XMaterial.GRAY_BANNER.parseItem();
        }

        @Override
        public ItemStack getTeamItem() {
            return XMaterial.GRAY_DYE.parseItem();
        }

        @Override
        public ChatColor getChatColor() {
            return ChatColor.DARK_GRAY;
        }
    }, BLACK {
        @Override
        public DyeColor getDyeColor() {
            return DyeColor.BLACK;
        }

        @Override
        public BarColor getBarColor() {
            return BarColor.WHITE;
        }

        @Override
        public ItemStack getWool() {
            return XMaterial.BLACK_WOOL.parseItem();
        }

        @Override
        public ItemStack getBanner() {
            return XMaterial.BLACK_BANNER.parseItem();
        }

        @Override
        public ItemStack getTeamItem() {
            return XMaterial.BLACK_DYE.parseItem();
        }

        @Override
        public ChatColor getChatColor() {
            return ChatColor.BLACK;
        }
    }, NONE {
        @Override
        public DyeColor getDyeColor() {
            return DyeColor.WHITE;
        }

        @Override
        public BarColor getBarColor() {
            return BarColor.WHITE;
        }

        @Override
        public ItemStack getWool() {
            return XMaterial.WHITE_WOOL.parseItem();
        }

        @Override
        public ItemStack getBanner() {
            return XMaterial.WHITE_BANNER.parseItem();
        }

        @Override
        public ItemStack getTeamItem() {
            return XMaterial.WHITE_DYE.parseItem();
        }

        @Override
        public ChatColor getChatColor() {
            return ChatColor.GRAY;
        }
    };

    public String getCode() {
        return this.toString().toLowerCase();
    }

    public String getName() {
        return SheepQuest.getInstance().getMessage("general.team-" + getCode());
    }

    public Color getColor() {
        return getDyeColor().getColor();
    }

    public abstract DyeColor getDyeColor();

    public abstract BarColor getBarColor();

    public abstract ItemStack getWool();

    public abstract ItemStack getBanner();

    public abstract ItemStack getTeamItem();

    public abstract ChatColor getChatColor();
}

