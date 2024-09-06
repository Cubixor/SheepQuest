package me.cubixor.sheepquest.game.events;

import me.cubixor.minigamesapi.spigot.events.GameResetEvent;
import me.cubixor.sheepquest.arena.SQArena;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameResetHandler implements Listener {

    @EventHandler
    public void onReset(GameResetEvent evt) {
        SQArena arena = (SQArena) evt.getLocalArena();

        for (Entity e : arena.getSheep().keySet()) {
            arena.getSheep().get(e).cancel();
            e.remove();
        }
    }
}
