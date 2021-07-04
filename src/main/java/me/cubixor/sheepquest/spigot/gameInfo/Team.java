package me.cubixor.sheepquest.spigot.gameInfo;

import com.cryptomorin.xseries.XMaterial;
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
    };

    public String getCode() {
        return this.toString().toLowerCase();
    }

    public Color getColor() {
        return getDyeColor().getColor();
    }

    public abstract DyeColor getDyeColor();

    public abstract BarColor getBarColor();

    public abstract ItemStack getWool();
}

