package me.cubixor.sheepquest.game.events;

import me.cubixor.sheepquest.SheepQuest;
import me.cubixor.sheepquest.gameInfo.Arena;

public enum SpecialEvent {
    BONUS_SHEEP {
        @Override
        public void runEvent(Arena arena) {
            new BonusSheep().spawnSheep(arena);
        }

        @Override
        public void runReset(Arena arena) {
        }

        @Override
        public String getName() {
            return "bonus-sheep";
        }

        @Override
        public boolean isEnabled() {
            SheepQuest plugin = SheepQuest.getInstance();
            return plugin.getConfig().getBoolean("special-events.bonus-sheep.enabled");
        }
    };

    public abstract void runEvent(Arena arena);

    public abstract void runReset(Arena arena);

    public abstract String getName();

    public abstract boolean isEnabled();
}
