package me.cubixor.sheepquest.game.kits;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.utils.Messages;

import java.util.Arrays;
import java.util.stream.Stream;

public enum KitType {
    STANDARD, ARCHER, ATHLETE;

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
        return KitType.getEnabled().findFirst().orElse(null);
    }

    public static Stream<KitType> getEnabled() {
        return Arrays.stream(KitType.values()).filter(KitType::isEnabled);
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
