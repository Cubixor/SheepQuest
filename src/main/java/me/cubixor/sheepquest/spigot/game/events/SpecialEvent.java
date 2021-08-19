package me.cubixor.sheepquest.spigot.game.events;

import me.cubixor.sheepquest.spigot.SheepQuest;
import me.cubixor.sheepquest.spigot.gameInfo.LocalArena;

public enum SpecialEvent {
    BONUS_ENTITY {
        @Override
        public void runEvent(LocalArena localArena) {
            new BonusEntity().spawnEntity(localArena);
        }

        @Override
        public void runReset(LocalArena localArena) {
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

    public abstract void runEvent(LocalArena localArena);

    public abstract void runReset(LocalArena localArena);

    public abstract String getName();

    public abstract boolean isEnabled();
}
