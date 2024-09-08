package me.cubixor.sheepquest.game.kits;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.utils.Messages;

public enum KitType {
    STANDARD(0), ARCHER(1), ATHLETE(2);

    private final int id;

    KitType(int id) {
        this.id = id;
    }

    public static KitType getById(int id) {
        for (KitType kitType : KitType.values()) {
            if (kitType.getId() == id) {
                return kitType;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return Messages.get("kits." + this);
    }

    public String getPermission() {
        return "kits." + this;
    }

    public boolean isEnabled() {
        return MinigamesAPI.getPlugin().getConfig().getBoolean("kits." + this + ".enabled");
    }

    public static KitType getFirstEnabled() {
        for (KitType kitType : KitType.values()) {
            if (kitType.isEnabled()) {
                return kitType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
