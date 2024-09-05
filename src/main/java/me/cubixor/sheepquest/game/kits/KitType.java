package me.cubixor.sheepquest.game.kits;

import me.cubixor.minigamesapi.spigot.MinigamesAPI;
import me.cubixor.minigamesapi.spigot.game.ArenasRegistry;
import me.cubixor.minigamesapi.spigot.utils.Messages;

public enum KitType {
    STANDARD(0) {
        @Override
        public Kit construct(ArenasRegistry arenasRegistry) {
            return new KitStandard(arenasRegistry);
        }
    }, ARCHER(1) {
        @Override
        public Kit construct(ArenasRegistry arenasRegistry) {
            return new KitArcher(arenasRegistry);
        }
    }, ATHLETE(2) {
        @Override
        public Kit construct(ArenasRegistry arenasRegistry) {
            return new KitAthlete(arenasRegistry);
        }
    };

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

    public abstract Kit construct(ArenasRegistry arenasRegistry);

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
