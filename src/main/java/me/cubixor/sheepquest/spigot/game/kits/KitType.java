package me.cubixor.sheepquest.spigot.game.kits;

import me.cubixor.sheepquest.spigot.SheepQuest;

public enum KitType {
    STANDARD(0), ARCHER(1), ATHLETE(2);

    private final int id;

    KitType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return toString().toLowerCase();
    }

    public String getName() {
        return SheepQuest.getInstance().getMessage("kits." + getCode());
    }

    public String getPermission() {
        return "sheepquest.kits." + getCode();
    }

    public boolean isEnabled() {
        return SheepQuest.getInstance().getConfig().getBoolean("kits." + getCode() + ".enabled");
    }
}
