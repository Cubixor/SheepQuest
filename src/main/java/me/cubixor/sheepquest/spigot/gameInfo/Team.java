package me.cubixor.sheepquest.spigot.gameInfo;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.boss.BarColor;
import org.bukkit.inventory.ItemStack;

public enum Team {
    RED {
        @Override
        public String getCode() {
            return "red";
        }

        @Override
        public Color getColor() {
            return Color.RED;
        }

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
    }, GREEN {
        @Override
        public String getCode() {
            return "green";
        }

        @Override
        public Color getColor() {
            return Color.LIME;
        }

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
    }, BLUE {
        @Override
        public String getCode() {
            return "blue";
        }

        @Override
        public Color getColor() {
            return Color.BLUE;
        }

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
    }, YELLOW {
        @Override
        public String getCode() {
            return "yellow";
        }

        @Override
        public Color getColor() {
            return Color.YELLOW;
        }

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
    }, NONE {
        @Override
        public String getCode() {
            return "none";
        }

        @Override
        public Color getColor() {
            return Color.WHITE;
        }

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
    };

    public abstract String getCode();

    public abstract Color getColor();

    public abstract DyeColor getDyeColor();

    public abstract BarColor getBarColor();

    public abstract ItemStack getWool();
}

